/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.17.0.2716 modeling language!*/

package cruise.umple.util;
import java.util.*;
import java.io.*;
import org.junit.*;

/**
 * @umplesource Util_Code.ump 567
 */
// line 567 "../../../../src/Util_Code.ump"
public class SampleFileWriter
{
  @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
  public @interface umplesourcefile{int[] line();String[] file();int[] javaline();int[] length();}

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public SampleFileWriter()
  {}

  //------------------------
  // INTERFACE
  //------------------------

  public void delete()
  {}
  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  // line 574 ../../../../src/Util_Code.ump
  @umplesourcefile(line={574},file={"Util_Code.ump"},javaline={41},length={11})
  public static File[] getAllFiles(File inputDirectory)
  {
    File[] allFiles = inputDirectory.listFiles();
    Arrays.sort(allFiles, new Comparator<File>(){
  @umplesourcefile(line={578},file={"Util_Code.ump"},javaline={46},length={4})
      public int compare(File f1, File f2)
      {
        return f1.getName().compareTo(f2.getName());
      } 
    });
    return allFiles;
  }

  @umplesourcefile(line={586},file={"Util_Code.ump"},javaline={55},length={27})
  public static String readContent(File filename)
  {
    BufferedReader reader = null;
    StringBuffer answer = new StringBuffer();
    
    try
    {
      reader = new BufferedReader(new FileReader(filename));
      String nextLine = null;
      while (( nextLine = reader.readLine()) != null)
      {
        answer.append(nextLine);
        answer.append("\n");
      } 
      
    }
    catch (Exception e)
    {
      throw new RuntimeException("Unable to read content",e);
    }
    finally
    {
      closeAsRequired(reader);
    }    
    return answer.toString();

  }
  
  @umplesourcefile(line={614},file={"Util_Code.ump"},javaline={84},length={11})
  public static void assertEitherFileContent(File expected1, File expected2, String actual)
  {
    try
    {
      assertFileContent(expected1,actual);
    }
    catch (AssertionError e)
    {
      assertFileContent(expected2,actual);
    }
  }
  
  @umplesourcefile(line={626},file={"Util_Code.ump"},javaline={97},length={45})
  public static void assertPartialFileContent(File partial, String actual)
  {
    BufferedReader expectedReader = null;
    BufferedReader actualReader = null;
    boolean foundMatch = false;
    
    try
    {
      expectedReader = new BufferedReader(new FileReader(partial));
      actualReader = new BufferedReader(new StringReader(actual));

      String expectedLine = null;
      String actualLine = null;
      
      int line = 0;
      do 
      {
        if (expectedLine == null || foundMatch)
        {
          expectedLine = expectedReader.readLine();
        }
        actualLine = actualReader.readLine();
        // HACK: To deal with // line # comments
        while (actualLine != null && actualLine.indexOf("// line") != -1)
        { //Ignore the line, go to next
          actualLine = actualReader.readLine();
        }
        
        line++;
        
        if (expectedLine == null) 
        {
          break; 
        }
        else if (foundMatch)
        {
          Assert.assertEquals("Failed at:" + line,expectedLine,actualLine);
        }
        else if (expectedLine.equals(actualLine))
        {
          foundMatch = true;
        }
      } 
      while (expectedLine != null);
    }
    catch (Exception e)
    {
      Assert.fail(e.getMessage());
    }
    finally
    {
      closeAsRequired(expectedReader);
      closeAsRequired(actualReader);
    }
    if (!foundMatch) { Assert.fail("Did not find expected lines"); }
  }
  
  @umplesourcefile(line={683},file={"Util_Code.ump"},javaline={155},length={4})
  public static void assertFileContent(File expected, String actual)
  {
    assertFileContent(expected, actual, true);
  }
  
  @umplesourcefile(line={688},file={"Util_Code.ump"},javaline={161},length={38})
  public static void assertFileContent(File expected, String actual, boolean ignoreLineComments)
  {
    
    BufferedReader expectedReader = null;
    BufferedReader actualReader = null;
    
    try
    {
      expectedReader = new BufferedReader(new FileReader(expected));
      actualReader = new BufferedReader(new StringReader(actual));

      String expectedLine = null;
      String actualLine = null;
      
      int line = 0;
      do 
      {
        actualLine = actualReader.readLine();
        if (ignoreLineComments)
        {
	        // HACK: To deal with // line # comments
	        while (actualLine != null && actualLine.indexOf("// line") != -1)
	        { //Ignore the line, go to next
	          actualLine = actualReader.readLine();
	        }
	    }
        expectedLine = expectedReader.readLine();
        
        line++;
        
        // HACK: To deal with umple version issues
        if (expectedLine != null && expectedLine.indexOf("This code was generated using the UMPLE") == -1)
        {
          Assert.assertEquals("Failed at:" + line,expectedLine,actualLine);  
        }
      } 
      while (expectedLine != null && actualLine != null);
    }
    catch (Exception e)
    {
      Assert.fail(e.getMessage());
    }
    finally
    {
      closeAsRequired(expectedReader);
      closeAsRequired(actualReader);
    }    
  }
  
  @umplesourcefile(line={737},file={"Util_Code.ump"},javaline={211},length={14})
  public static void closeAsRequired(BufferedReader reader)
  {
    if (reader != null)
    {
      try
      {
        reader.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  @umplesourcefile(line={752},file={"Util_Code.ump"},javaline={227},length={14})
  public static void closeAsRequired(InputStream reader)
  {
    if (reader != null)
    {
      try
      {
        reader.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  @umplesourcefile(line={767},file={"Util_Code.ump"},javaline={243},length={19})
  public static String rationalize(String rawPath)
  {
    String path = rawPath;
    File f = new File(path);
    if (f.exists())
    {
      return path;
    }
    
    path = "../cruise.umple/" + rawPath;
    f = new File(path);
    if (f.exists())
    {
      return path;
    }

    path = "/h/ralph/umple/trunk/cruise.umple/" + rawPath;
    return path;
  }
  
  @umplesourcefile(line={787},file={"Util_Code.ump"},javaline={264},length={16})
  public static void createFile(String filename, String input)
  {
    File f = new File(filename);
    f.getAbsoluteFile().getParentFile().mkdirs();
    
    try
    {
      BufferedWriter out = new BufferedWriter(new FileWriter(filename));
      out.write(input);
      out.close();
    }
    catch (IOException e)
    {
      throw new RuntimeException("Unable to create file [" + filename + "]", e);
    }
  }
  
  @umplesourcefile(line={804},file={"Util_Code.ump"},javaline={282},length={14})
  public static void destroy(String filename)
  {
    
    File file = new File(filename);
    
    if (file.isDirectory())
    {
      destroy(file);
    }
    else
    {
      file.delete();
    }
  }
  
  @umplesourcefile(line={819},file={"Util_Code.ump"},javaline={298},length={15})
  private static void destroy(File path)
  {
    if( path.exists() ) {
      File[] files = path.listFiles();
      for(int i=0; i<files.length; i++) {
         if(files[i].isDirectory()) {
           destroy(files[i]);
         }
         else {
           files[i].delete();
         }
      }
    }
    path.delete();
  }

}