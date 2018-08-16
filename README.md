# MSCITTool

To Test a unit test class with ByteCodeReader add the unit tests .class and .java files to the tool's project folder. 
Run ToolMain as main method. Using the first filedialog select your .class file, with the next filedialog select your .java file. 
Main issue with running the program is a null pointer exception is thrown when the .class and .java files aren't somewhere in the tool's project folder. 

Info on the tested unit test class is outputted in two ways; via the console and in a file called UnitTestMetricsInfo.html which can be found after running 
the program in the project folder. 

Maven plugin still in progress. Can run the plugin from command line using;
mvn UnitTestMetricPlugIn:UnitTestMetricPlugIn-Maven-Plugin:1.0-SNAPSHOT:metrictesting
Trying to execute plugin will result in the output "Code inspection for unit test classes"

Can run from command line using mvn exec:java -Dexec.mainClass="UnitTestMetricPlugIn.ToolMain"


Current implemented metrics 

Assertions per method; checks for at least one and no more than five assertions 

Class calls per method; checks for at least one and no more than five class calls 

Method calls per methods; checks for at least one and no more than five method calls 

Structure; checks test method matches AAA structure 

TestName; checks for should or test in name, checks no more than three ands and that the name is no less than 10 letters and no longer than 50 letters 

Badsmells; checks for system.out and thread.sleep 

Line count; checks for no less than five and no longer than 50

Test doubles; checks for no more than five test doubles 

Test spies; checks for no more than five test spies 

Test stubs; checks for no more than five test stubs 

