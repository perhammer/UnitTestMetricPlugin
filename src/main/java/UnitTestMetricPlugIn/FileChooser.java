package UnitTestMetricPlugIn;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;

public class FileChooser {

    public static String chooseFile(String dialogTitle) {
        return chooseFile(dialogTitle, null);
    }

    public static String chooseFile(String dialogTitle, final String extension) {
        JFrame yourJFrame = new JFrame();

        FileDialog fd = new FileDialog(yourJFrame, dialogTitle, FileDialog.LOAD);

        if (null!=extension) {
            fd.setFile("*."+extension); // supposed to work on Windows
            fd.setFilenameFilter(new FilenameFilter() { // supposed to work on macOS
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith("."+extension);
                }
            });
        }

        fd.setVisible(true);
        String classfilename = fd.getDirectory()+fd.getFile();
        if (classfilename == null) {
            System.out.println("You cancelled the choice \n");
        } else {
            System.out.println("You chose " + classfilename);
        }
        yourJFrame.dispose();
        return classfilename;
    }
}
