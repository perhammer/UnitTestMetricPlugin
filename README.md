# MSCITTool

To Test a unit test class with ByteCodeReader add the unit tests .class and .java files to the tool's project folder. 
Run ToolMain as main method. Using the first filedialog select your .class file, with the next filedialog select your .java file. 
Main issue with running the program is a null pointer exception is thrown when the .class and .java files aren't somewhere in the tool's project folder. 

Info on the tested unit test class is outputted in two ways; via the console and in a file called UnitTestMetricsInfo.html which can be found after running 
the program in the project folder. 

Maven plugin still in progress. Can run the plugin from command line using;
mvn UnitTestMetricPlugIn:UnitTestMetricPlugIn-Maven-Plugin:1.0-SNAPSHOT:metrictest
Trying to execute plugin will result in the output "A Code inspection tool for unit test classes"

Can run from command line using mvn exec:java -Dexec.mainClass="UnitTestMetricPlugIn.ToolMain"

