package UnitTestMetricPlugIn;

import javax.swing.*;
import java.awt.*;

public class FileChooser {

    public static String chooseFile(String dialogTitle) {
        JFrame yourJFrame = new JFrame();

        FileDialog fd = new FileDialog(yourJFrame, dialogTitle, FileDialog.LOAD);

        fd.setVisible(true);
        String classfilename = fd.getDirectory()+fd.getFile();
        if (classfilename == null) {
            System.out.println("You cancelled the choice \n");
        } else {
            System.out.println("You chose " + classfilename);
        }
        return classfilename;
    }
}
