/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.15.0.1751 modeling language!*/

package cruise.umple.sync;
import cruise.umple.compiler.*;
import cruise.umple.util.*;

// line 41 "../../../../src/UmpleSync.ump"
// line 337 "../../../../src/UmpleSync_Code.ump"
public class EditAction extends SynchronizationAction
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public EditAction(String aDeltaCode, String aUmpleCode)
  {
    super(aDeltaCode, aUmpleCode);
  }

  //------------------------
  // INTERFACE
  //------------------------

  public void delete()
  {
    super.delete();
  }
  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 340 ../../../../src/UmpleSync_Code.ump
  public void go()
  {
    try
    {
      TextParser textParser = new TextParser(getUmpleCode());
      UmpleParser umpleParser = new UmpleInternalParser();
      ParseResult result = umpleParser.parse("program", getUmpleCode());

      if (!result.getWasSuccess())
      {
        textParser.insert("//Unable to update umple code due to error at " + result.getPosition());
        setUmpleCode(textParser.getText());
        return;
      }
      
      JsonParser jsonParser = new JsonParser("json");
      ParseResult jsonResult = jsonParser.parse("json", getDeltaCode());
      
      if (!jsonResult.getWasSuccess())
      {
        setUmpleCode("//Failed at: " + jsonResult.getPosition() + "\n\n//" + getDeltaCode() + "\n\n" + getUmpleCode());
        return;
      }
      
      Json json = jsonParser.analyze();
      Json position = json.getAttribute("position");
      Json[] attributes = json.getArray("attributes");
      
      String newClassName = json.getValue("name");
      String oldClassName = json.getValue("oldname");
      String lookupName = oldClassName == null ? newClassName : oldClassName;
      
      boolean didEditName = false;
      boolean didEditAttributes = false;
      boolean didEditId = false;
      

      for (Token t : umpleParser.getRootToken().getSubTokens())
      {
        if (!t.is("classDefinition") && !t.is("associationDefinition") && !t.is("associationClassDefinition"))
        {
          continue;
        }
        
        if (oldClassName != null)
        {
          for (Token subT : t.getSubTokens())
          {
            if (subT.is("inlineAssociation"))
            {
              Token associationEnd = subT.getSubToken("associationEnd");
              if (associationEnd == null) { continue; }
              Token type = associationEnd.getSubToken("type");
              if (type == null || !type.getValue().equals(oldClassName)) { continue; }
              textParser.replace(type,newClassName);
            }
            else if (subT.is("association"))
            {
              Token end1 = subT.getSubToken("associationEnd", 0);
              Token end2 = subT.getSubToken("associationEnd", 1);
              if (end1 != null) 
              {
                Token type1 = end1.getSubToken("type"); 
                if (type1 != null)
                {
                  if(type1.getValue().equals(oldClassName))
                  {
                    textParser.replace(type1,newClassName);
                  }
                } 
              }
              if (end2 != null) 
              {
                Token type2 = end2.getSubToken("type"); 
                if (type2 != null)
                {
                  if(type2.getValue().equals(oldClassName))
                  {
                    textParser.replace(type2,newClassName);
                  }
                } 
              }
            }
            else if (subT.is("extendsName"))
            {
              if (!subT.getValue().equals(oldClassName)) { continue; }
              textParser.replace(subT, newClassName);
            }
            else if (subT.is("associationPosition"))
            {
              Token type = subT.getSubToken("name");
			  String temp = type.getValue();
			  String[] names = temp.split("_");
			  String res = "";
			  for (int i=0; i<names.length; i++)
			  {
			  	if (names[i].equals(oldClassName))
			  		names[i] = newClassName;
			  	if (i != names.length-1)
			  		res += names[i] + "_";
			  	else
			  		res += names[i];
			  }
			  textParser.replace(type,res);
            }            
          }
        }
        
        if (!lookupName.equals(t.getValue("name")))
        {
          continue;
        }
        
        Token positionT = t.getSubToken("elementPosition");
        
        didEditName = true; 
        textParser.replace(t.getSubToken("name"),newClassName);
        if (positionT != null)
        {
          didEditId = true;
          textParser.replace(positionT.getSubToken("x"),position.getValue("x"));
          textParser.replace(positionT.getSubToken("y"),position.getValue("y"));
          textParser.replace(positionT.getSubToken("width"),position.getValue("width"));
          textParser.replace(positionT.getSubToken("height"),position.getValue("height"));
        }
        else
        {
          Token firstAssociationPositionT = null;
          for(Token subT : t.getSubTokens())
          {
            if (subT.is("associationPosition"))
            {
              firstAssociationPositionT = subT;
            }
          }
          if (firstAssociationPositionT != null)
          {
            didEditId = true;
            String newClassPosition = StringFormatter.format("  position {0} {1} {2} {3};\n",position.getValue("x"),position.getValue("y"),position.getValue("width"),position.getValue("height")); 
            textParser.insertAfter(firstAssociationPositionT, newClassPosition);
          } 
        }
                
        if (!didEditAttributes && attributes != null)
        {
          didEditAttributes = true;
          for (Json attr : attributes)
          {
            if (attr.getValue("deleteName") != null)
            {
              for(Token subT : t.getSubTokens())
              {
                if (subT.is("attribute") && attr.getValue("deleteName").equals(subT.getValue("name")))
                {
                  textParser.replace(subT,"");
                }
              }
            }
            else if (attr.getValue("newName") != null)
            {
              Token addAfterLastAttribute = null;
              Token addBefore = null;
              for (Token subT : t.getSubTokens())
              {
                boolean wasAdded = false;
                if (addAfterLastAttribute == null || subT.isStatic("{") || subT.is("attribute")) // } TODO: FIX PARSER COUPLES
                {
                  wasAdded = true;
                  addAfterLastAttribute = subT;
                  addBefore = null;
                }
                if (!wasAdded && addAfterLastAttribute != null && addBefore == null)
                {
                  addBefore = subT;
                }
              }
              
              String type = "String".equals(attr.getValue("newType")) ? "" : StringFormatter.format("{0} ",attr.getValue("newType"));
              String attribute = "";
              
              if (addBefore.isStatic("}")) // { TODO: FIX PARSER COUPLES
              {
                attribute = StringFormatter.format("  {0}{1};\n",type,attr.getValue("newName"));
              }
              else
              {
                attribute = StringFormatter.format("{0}{1};\n  ",type,attr.getValue("newName"));
              }
              
              textParser.insertAfter(addAfterLastAttribute, attribute);
            }
            else if (attr.getValue("oldName") != null)
            {
              
              for(Token subT : t.getSubTokens())
              {
                if (subT.is("attribute") && attr.getValue("oldName").equals(subT.getValue("name")))
                {
                  Token type = subT.getSubToken("type");
                  Token name = subT.getSubToken("name");
                  boolean isDefaultType = "String".equals(attr.getValue("type"));

                  if (type == null && !isDefaultType)
                  {
                    textParser.replace(name,StringFormatter.format("{0} {1}",attr.getValue("type"),attr.getValue("name")));
                    continue;
                  }

                  if (type != null && !isDefaultType)
                  {
                    textParser.replace(type,attr.getValue("type"));
                  }
                  else if (type != null && isDefaultType)
                  {
                    textParser.replace(type,"");
                  }
                  textParser.replace(name,attr.getValue("name"));
                }
              }
            }
          }
        }
      }
      setUmpleCode(textParser.getText());
      
      if (didEditName && !didEditId)
      {
        String x = position.getValue("x");
        String y = position.getValue("y");
        String width = position.getValue("width");
        String height = position.getValue("height");
        
        String newUmple = getUmpleCode();
        if (newUmple.length() > 0)
        {
          newUmple += "\n";
        }
        newUmple += StringFormatter.format("class {0}\n{\n  position {1} {2} {3} {4};\n}\n", newClassName, x, y, width, height);
        setUmpleCode(newUmple);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      String output = "FATAL ERROR PARSING UMPLE DIAGRAM\n\n";
      
      for (StackTraceElement trace : e.getStackTrace())
      {
        output += trace.toString() + "\n";
      }
      setUmpleCode(output);
    }
  }
}