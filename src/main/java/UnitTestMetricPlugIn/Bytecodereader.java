package UnitTestMetricPlugIn;
import java.awt.FileDialog;

import java.awt.Desktop; 

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.bcel.classfile.*;

import java.util.regex.*;

import javax.swing.JFrame;

public class Bytecodereader{
	/*Alternative to tool file reader class for analysis of .java files 
	 * Class uses apache's BCEL library for analysis of .class files 
	 * bytecode analysis versus sourcecode analysis 
	 * Same library used by FindBugs
	 */
			public Bytecodereader() {
			classfilechoice();
			
		}		
	
	public void classfilechoice() {
		//Method to let the user chose there .class and .java file 
		JFrame yourJFrame = new JFrame();
		
		FileDialog fd = new FileDialog(yourJFrame, "Choose your .class file \n", FileDialog.LOAD);
	
		fd.setVisible(true);
		String classfilename = fd.getFile();
		if (classfilename == null) {
		  System.out.println("You cancelled the choice \n");
		}
		else {
		  System.out.println("You chose " + classfilename);
		}
		

		
		FileDialog fdjava = new FileDialog(yourJFrame, "Choose your .java file \n\n", FileDialog.LOAD);
	
		fdjava.setVisible(true);
		String javafilename = fdjava.getFile();
		if(javafilename == null) {
			System.out.print("You cancelled the choice \n");
		}
		else {
			System.out.print("You chose " + javafilename + "\n");
		}

		codereader(classfilename, javafilename);
		
	}
	public void codereader(String nameofclassfile, String nameofjavafile) {
		
		JavaClass TestClass = null;
		ClassParser parser = new ClassParser(nameofclassfile);
		
		int numberofmethod = 0;
		
		try {
			TestClass = parser.parse();
		} catch (ClassFormatException e) {
			System.out.println("Wrong format");
		} catch (IOException e) {
			System.out.println("Class not found");
		}
		
		for(Method method:TestClass.getMethods()) {
			numberofmethod++; 
		}
		
		String[] TestMethods = new String[numberofmethod * 2]; 
		/*Double the number of classes in the program
		 * if it's set as class number calls array out of bounds exception with number of classes as the amount out of bounds 
		 */
		
		//Makes String builder 
		StringBuilder builder = new StringBuilder();
		//Reads in unit test java file
		FileReader reader = null;
		
		try {
			try {
				//Reads in unit class java file 
				reader = new FileReader(nameofjavafile); 
				// flag indicating whether finished reading
				boolean done = false;
				while (!done) {
                 // read a character, represented by an int
				        int next = reader.read();
				        // -1 represents end of file
					if (next == -1)
					        done = true;
					else {
					        //convert to character
					        char c = (char) next;
					        
					        //Adds character to builder string 
					        builder.append("" + c);
					        
					}
				}
			}
			finally {
				// close the input file assuming it was opened successfully
				if (reader != null) reader.close();
			}
		}
		catch (IOException e) {
			System.out.println("Error opening file");
		}
		
		//Creates unittest string from string builder builder 
		String unittest = builder.toString();
		
		/*creates array for each test method, uses the 
		 * number of test methods in test class 
		 */
		String methodtext[] = new String[numberofmethod * 2]; //Can pass in the number of methods 
		methodtext = unittest.split("@Test");
		
		String Testclassname = TestClass.getClassName();
		
		StringBuilder unittestbuilder = new StringBuilder();
		String infoforunittest = "<h1> Information on " + Testclassname +"<h1>";
		unittestbuilder.append(infoforunittest); 
		String methodnameinfo = "";
	
		int index = 0; 

		/*Prints out info for each method 
		 * Need to figure how to use this information with the metrics, need to understand how comments, asserts, class and method invocations are represented 
		 * Invokespecial = making of a new object/ invoking a class 
		 * Invokevirtual = invoking of a method 
		 * Invokestatic = assert statement 
		 * Doesn't have any obvious indicator for comments however this is the metric that have most concerns about the usefulness of 
		 * Could add metric for structure = Arrange, Act, Assert = Invoke special - bipush / invokevirtual - invokestatic 
		 * Represents test mocking as invokestatic = same as assert statements for test double creation, test stubs and test spies.
		 * Mockito = invoke interface. TestStub = OngoingStubbing, TestSpies = Verification both twice for one instance. Test Doubles = also invokestatic. Each instance of a test
		 * double also counts as a method call. 
		 * Method calls + ClassCalls = need to add count of unique method/class calls 
		 */
		for (Method method:TestClass.getMethods()) {
			
			String methodname = method.getName();
			Code methodcontent = null;
			
			if(methodname.contains("init>")) {
				index++;
			} else {
			methodcontent = method.getCode();
			TestMethods[index] = "" + methodcontent;
			System.out.print("In method " + methodname + ": \n");
			methodnameinfo = "<p> In method " + methodname + "<div>";
			unittestbuilder.append(methodnameinfo);
		
			//Linecount(methodtext[index], unittestbuilder); 
			//Linecount metric is causing array out of bounds exceptions solution being worked on
			AssertPerMethod(TestMethods[index], unittestbuilder);
			ClassCallsPerMethod(TestMethods[index], unittestbuilder);
			MethodCallsPerMethod(TestMethods[index], unittestbuilder);
			Mocking(TestMethods[index], unittestbuilder);
			badsmells(TestMethods[index], methodname, unittestbuilder);
			Structure(TestMethods[index], methodname, unittestbuilder); 
			Testname(methodname, unittestbuilder); 
			
			index++;
			}
		}
		
		File unitteststats = new File(Testclassname + "UnitTestMetricsInfo.html");
		
		try {
			//Writes html code to .html file
			BufferedWriter statswriter = new BufferedWriter(new FileWriter(unitteststats));		
			String unitteststattext = unittestbuilder.toString();
			statswriter.write(unitteststattext);
			statswriter.close();
			//Open html file after closing the writer 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }
        
        Desktop desktop = Desktop.getDesktop();
        try {
			desktop.open(unitteststats);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public int AssertPerMethod(String methods, StringBuilder build) {
		/*
		 * Checks for assert 
		 */
		
		int currentclass = 0;
		int index = 0;
		int assertions = 0;
		
		while(index <= methods.length() - 10) { //Have to minus 10 to stop String index out of bounds exception 
			char currentletter = methods.charAt(index);
			char secondletter = methods.charAt(index + 1);
			char thirdletter = methods.charAt(index + 2);
			char forthletter = methods.charAt(index + 3);
			char fifthletter = methods.charAt(index + 4);
			char sixthletter = methods.charAt(index + 5);

			if(currentletter == 'a' && secondletter == 's' && thirdletter == 's' && forthletter == 'e' && fifthletter == 'r' && sixthletter == 't') {
				assertions++;
				index++;
			} else {
						index++;
					}
	}
		System.out.print("The number of assertions is " + assertions + "\n");
		String assertoutput = "The number of assertions is " + assertions + "<div>";
		build.append(assertoutput);
		assertions(assertions, build);
		return assertions;
	}
	public boolean assertions(int numberofasserts, StringBuilder buildstr) {
		boolean tomanyassertions = false;
		boolean notenoughassertions = false; 
		
		if(numberofasserts <= 0) {
			notenoughassertions = true;
			System.out.print("Test method may not have enough assert statements \n");
			String assertions = "Test method may not have enough assert statements <div>";
			buildstr.append(assertions);
		} else if (numberofasserts >= 5) {
			tomanyassertions = true; 
			System.out.print("The test method may have to many assertions");
			String assertions = "Test method may have to many assertions <div>";
			buildstr.append(assertions); 
		}
		return tomanyassertions;
	}
	public int ClassCallsPerMethod(String methods, StringBuilder build) {
	//Checks for invoke special 
		int index = 0;
		int classcalls = 0;
		
		while(index <= methods.length() - 10) { //Have to minus 10 to stop String index out of bounds exception 
			char nextcurrentletter = methods.charAt(index);
			char nextsecondletter = methods.charAt(index + 1);
			char nextthirdletter = methods.charAt(index + 2);
			char nextforthletter = methods.charAt(index + 3);
			char nextfifthletter = methods.charAt(index + 4);
			char nextsixthletter = methods.charAt(index + 5);
			char nextseventhletter = methods.charAt(index + 6);
			
			if(nextcurrentletter == 's' && nextsecondletter == 'p' && nextthirdletter == 'e' && nextforthletter == 'c' && nextfifthletter == 'i' && nextsixthletter == 'a' && nextseventhletter == 'l') {
				classcalls++;
				index++;
			} else 
				index++; 
		}
		System.out.print("The number of class calls is " + classcalls + "\n");
		String classout = "The number of class calls is " + classcalls + "<div>";
		build.append(classout); 
		classcalls(classcalls, build);
		return classcalls;
	}
	public boolean classcalls(int numberofclasscalls, StringBuilder buildstr) {
		boolean tomanyclasscalls = false;
		boolean tolittleclasscalls = false;
		if(numberofclasscalls >= 5) {
			System.out.print("The test method may have to many class calls \n");
			tomanyclasscalls = true; 
			String classnum = "The test method may have to many class calls <div>";
			buildstr.append(classnum);
		} else if(numberofclasscalls <= 0) {
			System.out.print("The test method may not have enough class calls \n");
			tolittleclasscalls = true;
			String classnum = "The test method may not have enough class calls <div>"; 
			buildstr.append(classnum); 
		}
	
		return tomanyclasscalls;
	}
	
	public int MethodCallsPerMethod(String methods, StringBuilder build) {
		//Checks for invoke virtual 
		int index = 0; 
		int methodcalls = 0;
		
		while(index <= methods.length() - 10) { //Have to minus 10 to stop String index out of bounds exception 
			char nextcurrentletter = methods.charAt(index);
			char nextsecondletter = methods.charAt(index + 1);
			char nextthirdletter = methods.charAt(index + 2);
			char nextforthletter = methods.charAt(index + 3);
			char nextfifthletter = methods.charAt(index + 4);
			char nextsixthletter = methods.charAt(index + 5);
			char nextseventhletter = methods.charAt(index + 6);
			
			if(nextcurrentletter == 'v' && nextsecondletter == 'i' && nextthirdletter == 'r' && nextforthletter == 't' && nextfifthletter == 'u' && nextsixthletter == 'a' && nextseventhletter == 'l') {
				methodcalls++;
				index++;
			} else
				index++; 
		}
		System.out.print("The number of method calls is " + methodcalls + "\n");
		String methodout = "The number of method calls is " + methodcalls + "<div>";
		build.append(methodout);
		Methodcalls(methodcalls, build);
		return methodcalls;
	}
	public boolean Methodcalls(int numberofmethodcalls, StringBuilder buildstr) {
		boolean tomanymethodcalls = false;
		boolean tolittlemethodcalls = false;
		if(numberofmethodcalls >= 5) {
			System.out.print("The test method may have to many method calls \n");
			tomanymethodcalls = true; 
			String methodnum = "The test method may have to many method calls <div>"; 
			buildstr.append(methodnum);
		} else if(numberofmethodcalls <= 0) {
			System.out.print("The test method may not have enough method calls \n");
			tolittlemethodcalls = true;
			String methodnum = "The test method may not have enough method calls <div>";
			buildstr.append(methodnum);
		}
		return tomanymethodcalls;
	}
	
	public int TestStubs(String methods, StringBuilder buildstr) {
		//Looks for Ongoingstubbing, for one instance of a teststub = two instances of ongoingstubbing 
		//TestStub = invokestatic org.mockito.Mockito.when(java.lang.Object) : org.mockito.stubbing.OngoingStubbing [55] 
		int teststubs = 0;
		int index = 0; 
		
		while(index <= methods.length() - 10) { 
			char currentletter = methods.charAt(index);
			char secondletter = methods.charAt(index + 1);
			char thirdletter = methods.charAt(index + 2);
			char forthletter = methods.charAt(index + 3);
			if(currentletter == 'w' && secondletter == 'h' && thirdletter == 'e' && forthletter == 'n') {
				teststubs++;
				index++;
			} else
				index++;
		}
		
		System.out.print("The number of test stubs is " + teststubs + "\n");
		String teststubsout = "The number of test stubs is " + teststubs + "<div>"; 
		buildstr.append(teststubsout);
		teststubcount(teststubs, buildstr);
		return teststubs; 
	}
	public boolean teststubcount(int numberofteststubs, StringBuilder strbuild) {
		boolean tomanyteststubs = false;
		
		if(numberofteststubs >= 5) {
			System.out.print("Method may have to many test stubs \n");
			String stubnum = "Method may have to many test stubs <div>"; 
			tomanyteststubs = true;
		}
		
		return tomanyteststubs; 
	}
	public int TestSpies(String methods, StringBuilder buildstr) {
		//Looks for Verification, for one instance of a testspy = two instances of ongoingstubbing 
		//invokestatic org.mockito.Mockito.times(int) : org.mockito.verification.VerificationMode [66]
		//invokestatic org.mockito.Mockito.verify(java.lang.Object, org.mockito.verification.VerificationMode) : java.lang.Object [70]
		int testspies = 0;
		int index = 0; 
		
		while(index <= methods.length() - 10) { 
			char currentletter = methods.charAt(index);
			char secondletter = methods.charAt(index + 1);
			char thirdletter = methods.charAt(index + 2);
			char forthletter = methods.charAt(index + 3);
			char fifthletter = methods.charAt(index + 4);
			char sixthletter = methods.charAt(index + 5);
			if(currentletter == 'v' && secondletter == 'e' && thirdletter == 'r' && forthletter == 'i' && fifthletter == 'f' && sixthletter == 'y') {
				testspies++;
				index++;
			} else 
				index++;
		}
		
		System.out.print("The number of test spies is " + testspies + "\n");
		String testspiesout = "The number of test spies is " + testspies + "<div>";
		buildstr.append(testspiesout); 
		testspiescount(testspies, buildstr);
		return testspies; 
	}
	public boolean testspiescount(int numberoftestspies, StringBuilder strbuild) {
		boolean tomanytestspies = false;
		
		if(numberoftestspies >= 5) {
			System.out.print("Method may have to many test spies \n");
			String testspiesnum = "Method may have to many test spies <div>"; 
			tomanytestspies = true;
		}
		return tomanytestspies;
	}
	public int TestDoubles(String methods, StringBuilder buildstr) {
		//If testmethod contains mocking, makes method count complex as each instance of a testdouble will invoke a method 
		//Test double = invokestatic org.mockito.Mockito.mock(java.lang.Class) : java.lang.Object [49]
		//Looks for Mockito.mock
		int testdouble = 0;
		int index = 0;
		
		while(index <= methods.length() - 13) { 
			char currentletter = methods.charAt(index);
			char secondletter = methods.charAt(index + 1);
			char thirdletter = methods.charAt(index + 2);
			char forthletter = methods.charAt(index + 3);
			char fifthletter = methods.charAt(index + 4); 
			char sixthletter = methods.charAt(index + 5);
			char seventhletter = methods.charAt(index + 6);
			char eightletter = methods.charAt(index + 7);
			char nineletter = methods.charAt(index + 8);
			char tenletter = methods.charAt(index + 9);
			char elevenletter = methods.charAt(index + 10);
			char twelveletter = methods.charAt(index + 11);
			if(currentletter == 'M' && secondletter == 'o' && thirdletter == 'c' && forthletter == 'k' && fifthletter == 'i' && sixthletter == 't' && seventhletter == 'o' && eightletter == '.' && nineletter == 'm' && tenletter == 'o' && elevenletter == 'c' && twelveletter == 'k') {
				testdouble++;
				index++;
			} else 
				index++; 
		}
			
		System.out.print( "The number of test doubles is " + testdouble + "\n");
		String testdoublesout = "The number of test doubles is " + testdouble + "<div>"; 
		buildstr.append(testdoublesout); 
		testdoublescount(testdouble, buildstr); 
		return testdouble; 
	}
	public boolean testdoublescount(int numberoftestdoubles, StringBuilder strbuild) {
		boolean tomanytestdoubles = false;
		
		if(numberoftestdoubles >= 5) {
			System.out.print("Method may have to many test doubles \n");
			String doublenum = "Method may have to many test doubles <div>"; 
			strbuild.append(doublenum); 
			tomanytestdoubles = true;
		}
		return tomanytestdoubles;
	}
	
	public boolean Mocking(String methods, StringBuilder build) {
		//Searches for invoke interface org.mockito 
		//Only runs mocking metrics against methods/test classes with no mocking 
		boolean mocking = false;
		
		mocking = methods.contains("org.mockito"); 
		
		if(mocking == true) {
			TestDoubles(methods, build);
			TestSpies(methods, build);
			TestStubs(methods, build);
			return mocking; 
		} else {
		return mocking;
		}
	}
	public boolean badsmells(String methods, String nameofmethod, StringBuilder build) {
		//Looks for specific bad smells within the test method
		//Specifically looks for Thread.sleep and System.out 
		boolean foundthreadsleep = false;
		boolean foundsystemout = false;
		boolean foundtimeout = false; //Not yet implemented 
		boolean foundtimeordate = false; //Not yet implemented 
		boolean foundlogic = false;
		
		boolean badsmells = false; 
		String badsmell = "";
		
		foundthreadsleep = methods.contains(".sleep");
		
		foundsystemout = methods.contains("System.out");
		
		foundlogic = methods.contains("if") || methods.contains("for") || methods.contains("while") || methods.contains("switch"); 
		
		if(foundthreadsleep == true) {
			badsmell = "thread.sleep";
		}
		
		if(foundsystemout == true) {
			badsmell = badsmell + " System.out ";
		}
		
		if(foundlogic == true) {
			badsmell = badsmell + " logic (if, for, while, switch) "; 
		}
		
		if(foundthreadsleep == true || foundsystemout == true || foundlogic == true) {
		badsmells = true; 
		System.out.print("Method " + nameofmethod + " contains a bad smell: " + badsmell + "\n");
		String badsmellout = "The Method contains a bad smell: " + badsmell + "<div>";
		build.append(badsmellout); 
		return true;
		} else {
			return badsmells; 
		}
	}
	public boolean Structure(String methods, String nameofmethod, StringBuilder build) {
		boolean Structure = false;
		
		int specialindex = methods.indexOf("invokespecial"); 
		int virtualindex = methods.indexOf("invokevirtual");
		int staticindex = methods.indexOf("assert");
		
		if(specialindex < virtualindex && virtualindex < staticindex) {
			Structure = true;
			System.out.print("Method " + nameofmethod + " matches the recommended triple A structure \n");
			String structureout = "The Method matches the recommended triple a structure <div>"; 
			build.append(structureout); 
		} else {
			Structure = false;
			System.out.print("Method " + nameofmethod + " doesn't match the recommended triple A structure \n");
			String structureout = "The Method doesn't match the recommended triple A structure <div>";
			build.append(structureout); 
		}
		return Structure;
	}
	public int Linecount(String textmethod, StringBuilder build) {
		//Passes in name of the test class to make a filereader object to read the java version 
		int linecount = 0; 
		String[] lines = textmethod.split("\r\n|\r|\n");
		linecount = lines.length;
		System.out.print("The method has " + linecount + " lines \n");
		String linecountoutput = "The method has " + linecount + " lines"; 
		build.append(linecountoutput);
		
		if(linecount <= 5) {
			System.out.print("Method may be to short \n");
			String numlinecount = "Method may be to short <div>";
			build.append(numlinecount);
		} else if (linecount >= 50) {
			System.out.print("Method may be to long \n");
			String numlinecount = "Method may be to long <div>";
			build.append(numlinecount); 
		} else {
		return linecount;
		}
		return linecount;
	}
	public boolean Testname(String nameofmethod, StringBuilder build) {
		//Checks if testname differs from other test names in class 
		//Not yet implemented 
		boolean unusualtestname = false;
		
		Pattern testnamepattern = Pattern.compile("\\s+"); //Pattern is not case sensitive
		
		Matcher matcher = testnamepattern.matcher(""); //Can add in to the brackets a regular sequence for an example test name 
		//Would mean having to define an ideal test name. 
		//Originally was to check if the test names differed from one and other, this may not be best way to do this 
		//Need to establish a pattern from the existing test names and then test the names against this pattern 
		//Or test the names against popular junit naming conventions 
		
		boolean multipleands = false; 
		boolean longshorttestname = false;
		
		boolean shouldcontain = false;
		boolean testcontain = false;
		boolean namingconvention = false;
		
		boolean worryingtestname = false; 
		
		String wronglength = "";
		String tomanyands = "";
		String conventionofnaming = "";
		String namepattern = ""; 
		
		shouldcontain = nameofmethod.contains("should"); //Want to make sure this is case sensitive 
		testcontain = nameofmethod.contains("test"); //Want to make sure this is case sensitive 
		
		if(shouldcontain == true || testcontain == true) {
			namingconvention = true;
		} else {
			conventionofnaming = " method name doesn't contain an instance of test or should";
			namingconvention = false;
		}
	
		//Checks if testname if unusually long or short
		int namelength = nameofmethod.length(); 
		if(namelength <= 10 || namelength >= 50) {
			wronglength = " method name is less than 10 characters or greater then 50 characters"; 
			longshorttestname = true;
		} else {
			longshorttestname = false;
		}
		
		//Checks if testname has over two ands 
		int index = 0;
		int ands = 0;	
		
		while(index <= nameofmethod.length() - 3) {
			char currentchar = nameofmethod.charAt(index);
			char secondchar = nameofmethod.charAt(index + 1);
			char thirdchar = nameofmethod.charAt(index + 2);
			if(currentchar == 'a' && secondchar == 'n' && thirdchar == 'd') {
				ands++;
				index++;
			} else {
				index++;
			}
		}
		if(ands >= 3) {
			tomanyands = " method name has over three instances of and";
			multipleands = true; 
		} else {
			multipleands = false;
		}
		
		//Returns true if one of the previous booleans is found be true 
		if(unusualtestname == true || longshorttestname == true || multipleands == true || namingconvention == false) {
			worryingtestname = true;
			System.out.print("There is a potential issue with the name of this test method:" + tomanyands + wronglength + conventionofnaming + "\n\n");
			String testnameout = "There is a potential issue with the name of this test method: " + tomanyands + wronglength + conventionofnaming + "<div><div><div><div><div><div><div><div><div>";
			build.append(testnameout);
		} else {
			System.out.print("There is no indication of a potential issue with the name of this testmethod \n\n");
			String testnameout = "There is no indication of a potential issues with the name of this testmethod <div><div><div><div><div><div><div><div><div>"; 
			build.append(testnameout); 
			worryingtestname = false;
		
		
		}
		return worryingtestname;
	}

	public int externalR(String methods) {
		//Counts number of references to external resources such as databases 
		//Need to add reference to an external resource to the posttest class 
		int externalr = 0;
		return externalr; 
	}
	public int Uniquemethods(String methods, int methodcount, int index) {
		//Counts number of unique methods invoked 
		//Method invocation representation = invokevirtual PostOfficeGUI.Service(int, int, int, int, int, int) : java.lang.String [22]
		//Create an array of referenced classnames = compare them to one and other 
		int uniquemethods = 0;
		String[] methodname = new String[methodcount * 2];
		methodname = methods.split("invokevirtual");
		
		return uniquemethods;
	}
	public int UniqueClass(String methods, int methodcount, int index) {
		//Counts number of unique classes invoked 
		//Class invocation representation = invokespecial PostOfficeGUI() [19]
		//Create array of referenced methodnames = compare them to one and other 
		int uniqueclass = 0;
		String[] classname = new String[methodcount * 2];
		classname = methods.split("invokespecial");
	
		return uniqueclass;
	}

	public int executionrate(String methods) {
		//finds execution rate of each test method 
		int executionrate = 0;
		return executionrate;
	}
}
