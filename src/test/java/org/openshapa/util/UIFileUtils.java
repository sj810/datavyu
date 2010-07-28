package org.openshapa.util;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import static org.fest.reflect.core.Reflection.method;


import java.io.File;





import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.OpenSHAPAFrameFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.util.Platform;



import org.openshapa.OpenSHAPA;
import org.openshapa.util.FileFilters.CSVFilter;
import org.openshapa.util.FileFilters.MODBFilter;
import org.openshapa.util.FileFilters.OPFFilter;
import org.openshapa.util.FileFilters.SHAPAFilter;
import org.openshapa.views.OpenSHAPAFileChooser;
import org.openshapa.views.discrete.SpreadsheetView;
import org.testng.Assert;


/**
 * Utilities for UI tests.
 */
public final class UIFileUtils {

    /**
     * Empty constructor for utility class.
     */
    private UIFileUtils() {
    }

    /**
     * Loads file after creating a new project.
     * @param file opf file to load
     */
    public static void loadFile(final OpenSHAPAFrameFixture mainFrameFixture, final File file) {

        String ext = getExtension(file);

        // Create a new project, this is for the discard changes dialog.
        if (Platform.isOSX()) {
            mainFrameFixture.pressAndReleaseKey(KeyPressInfo.keyCode(
                    KeyEvent.VK_N).modifiers(InputEvent.META_MASK));
        } else {
            mainFrameFixture.menuItemWithPath("File", "New").click();
        }

        try {
            JOptionPaneFixture warning = mainFrameFixture.optionPane();
            warning.requireTitle("Unsaved changes");
            warning.buttonWithText("OK").click();
        } catch (Exception e) {
            // Do nothing
        }

        // Get New Database dialog
        DialogFixture newProjectDialog = mainFrameFixture.dialog("NewProjectV");

        newProjectDialog.textBox("nameField").enterText("n");

        newProjectDialog.button("okButton").click();

        // Open file
        Assert.assertTrue(file.exists());

        // 1. Load  File
        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);

            if (ext.equalsIgnoreCase("csv")) {
                fc.setFileFilter(new CSVFilter());
            } else if (ext.equalsIgnoreCase("opf")) {
                fc.setFileFilter(new OPFFilter());
            } else if (ext.equalsIgnoreCase("odb")) {
                fc.setFileFilter(new MODBFilter());
            } else if (ext.equalsIgnoreCase("shapa")) {
                fc.setFileFilter(new SHAPAFilter());
            } else {
                Assert.assertTrue(false, "Bad file extension:" + ext);
            }

            fc.setSelectedFile(file);

            method("open").withParameterTypes(OpenSHAPAFileChooser.class).in(
                OpenSHAPA.getView()).invoke(fc);
        } else {
            mainFrameFixture.clickMenuItemWithPath("File", "Open...");

            try {
                JOptionPaneFixture warning = mainFrameFixture.optionPane();
                warning.requireTitle("Unsaved changes");
                warning.buttonWithText("OK").click();
            } catch (Exception e) {
                // Do nothing
            }

            if (ext.equalsIgnoreCase("csv")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    new CSVFilter());
            } else if (ext.equalsIgnoreCase("opf")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    new OPFFilter());
            } else if (ext.equalsIgnoreCase("odb")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    new MODBFilter());
            } else if (ext.equalsIgnoreCase("shapa")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                        new SHAPAFilter());
            } else {
                Assert.assertTrue(false, "Bad file extension:" + ext);
            }

            mainFrameFixture.fileChooser().selectFile(file).approve();
        }
    }

    public static void saveFile(OpenSHAPAFrameFixture mainFrameFixture, File fileToSave) {
        String ext = getExtension(fileToSave);

        if (Platform.isOSX()) {
            OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
            fc.setVisible(false);

            if (ext.equalsIgnoreCase("opf")) {
                fc.setFileFilter(new OPFFilter());
            } else if (ext.equalsIgnoreCase("csv")) {
                fc.setFileFilter(new CSVFilter());
            } else if (ext.equalsIgnoreCase("odb")) {
                fc.setFileFilter(new MODBFilter());
            } else {
                Assert.assertTrue(false, "Bad file extension:" + ext);
            }

            fc.setSelectedFile(fileToSave);

            method("save").withParameterTypes(OpenSHAPAFileChooser.class).in(
                OpenSHAPA.getView()).invoke(fc);
        } else {

            SpreadsheetPanelFixture spreadsheet = mainFrameFixture.getSpreadsheet();
            mainFrameFixture.clickMenuItemWithPath("File", "Save");

            if (ext.equals("opf")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    new OPFFilter());
            } else if (ext.equals("csv")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    new CSVFilter());
            } else if (ext.equals("odb")) {
                mainFrameFixture.fileChooser().component().setFileFilter(
                    new MODBFilter());
            } else {
                Assert.assertTrue(false, "Bad file extension:" + ext);
            }

            mainFrameFixture.fileChooser().selectFile(fileToSave).approve();
        }
    }

    public static String getExtension(File file) {
        String[] fileNameParts = file.getName().split("\\.");
        if (fileNameParts.length > 1) {
            return fileNameParts[fileNameParts.length - 1];
        } else {
            return "";
        }
    }

    
}
