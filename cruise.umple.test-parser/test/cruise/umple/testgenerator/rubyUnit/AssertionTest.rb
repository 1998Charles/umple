# EXPERIMENTAL CODE - NON COMPILEABLE VERSION OF Umple xUnit Model
#PLEASE DO NOT EDIT THIS CODE
#This code was generated using the UMPLE @UMPLE_VERSION@ modeling language!



require 'test/unit'

######---- Tests for  Assertion.ump ----#####

module AssertionModule

  class AssertionTest  
     
     
    def setup
        #Setup Method
    end  

     
    def teardown
      #Tear Down
    end
     
  def someTest < Test::Unitt::TestCase            
            p1 = Person.new("John","123","someAddrss")      
            assert(p1.get_id!=nil)

  
  end
end 


