package UnitTestMetricPlugIn;

import java.io.FileReader;
import java.io.IOException;

public class JavaFileAnalyser {

    public static void analyseJavaFile(String nameofjavafile) {
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
            } finally {
                // close the input file assuming it was opened successfully
                if (reader != null) reader.close();
            }
        } catch (IOException e) {
            System.out.println("Error opening file");
        }

        //Creates unittest string from string builder builder
        String unittest = builder.toString();

        /*creates array for each test method, uses the
         * number of test methods in test class
         */
        String methodtext[];// = new String[numberofmethod * 2]; //Can pass in the number of methods
        methodtext = unittest.split("@Test");

    }
}
