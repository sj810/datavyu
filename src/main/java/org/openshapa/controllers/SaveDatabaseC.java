package org.openshapa.controllers;

import org.openshapa.OpenSHAPA;
import org.openshapa.db.DataCell;
import org.openshapa.db.DataColumn;
import org.openshapa.db.Database;
import org.openshapa.db.SystemErrorException;
import org.openshapa.util.FileFilters.CSVFilter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.openshapa.db.FormalArgument;
import org.openshapa.db.MatrixVocabElement;
import org.openshapa.db.MatrixVocabElement.MatrixType;

/**
 * Controller for saving the database to disk.
 */
public final class SaveDatabaseC {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(SaveDatabaseC.class);

    /**
     * Constructor.
     *
     * @param destinationFile The destination to use when saving the CSV file.
     * @param fileFilter The selected filter to use when saving the file.
     */
    public SaveDatabaseC(final String destinationFile,
                         final FileFilter fileFilter) {

        // BugzID:541 - Don't append ".csv" if the path already contains it.
        String outputFile = destinationFile.toLowerCase();
        if (!outputFile.contains(".csv")) {
            outputFile = destinationFile.concat(".csv");
        }

        if (fileFilter.getClass() == CSVFilter.class) {
            // BugzID:449 - Set filename in spreadsheet window and database to
            // be the same as the file specified.
            try {
                String dbName = new File(outputFile).getName();
                dbName = dbName.substring(0, dbName.lastIndexOf('.'));
                OpenSHAPA.getDatabase().setName(dbName);

                // Update the name of the window to include the name we just
                // set in the database.
                JFrame mainFrame = OpenSHAPA.getApplication()
                                   .getMainFrame();
                ResourceMap rMap = OpenSHAPA.getApplication()
                                   .getContext()
                                   .getResourceMap(OpenSHAPA.class);

                mainFrame.setTitle(rMap.getString("Application.title")
                               + " - " + OpenSHAPA.getDatabase().getName());
            } catch (SystemErrorException se) {
                logger.error("Can't set db name to specified file.", se);
            }

            saveAsCSV(outputFile);
        }
    }

    /**
     * Saves the database to the specified destination in a CSV format.
     *
     * @param outFile The path of the file to use when writing to disk.
     *
     * Changes: Replace call to vocabElement.getFormalArg() with call
     *          to vocabElement.getFormalArgCopy().
     */
    public void saveAsCSV(final String outFile) {
        Database db = OpenSHAPA.getDatabase();

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
            Vector<Long> colIds = db.getColOrderVector();

            //Vector<DataColumn> cols = db.getDataColumns();
            for (int i = 0; i < colIds.size(); i++) {
                DataColumn dc = db.getDataColumn(colIds.get(i));
                boolean isMatrix = false;

                out.write(dc.getName() + " (" + dc.getItsMveType() + ")");

                // If we a matrix type - we need to dump the formal args.
                MatrixVocabElement mve = db.getMatrixVE(dc.getItsMveID());
                if (dc.getItsMveType() == MatrixType.MATRIX) {
                    isMatrix = true;
                    out.write("-");
                    for (int j = 0; j < mve.getNumFormalArgs(); j++) {
                        FormalArgument fa = mve.getFormalArgCopy(j);
                        String name = fa.getFargName()
                                   .substring(1, fa.getFargName().length() - 1);
                        out.write(name + "|" + fa.getFargType().toString());

                        if (j < mve.getNumFormalArgs() - 1) {
                            out.write(",");
                        }
                    }
                }

                out.newLine();
                for (int j = 1; j <= dc.getNumCells(); j++) {
                    DataCell c = (DataCell) dc.getDB().getCell(dc.getID(), j);
                    out.write(c.getOnset().toString());
                    out.write(",");
                    out.write(c.getOffset().toString());
                    out.write(",");
                    String value = c.getVal().toString();
                    String result = new String();

                    // BugzID: 637 - We now insert an escape character when
                    // exporting as CSV.
                    for (int n = 0; n < value.length(); n++) {
                        if (value.charAt(n) == '\\') {
                            result = result.concat("\\\\");
                        } else if (value.charAt(n) == ',') {
                            result = result.concat("\\,");
                        } else {
                            result += value.charAt(n);
                        }
                    }

                    if (!isMatrix) {
                        result = result.substring(1, result.length() - 1);
                    }
                    out.write(result);
                    out.newLine();
                }
            }
            out.close();

        } catch (IOException e) {
            logger.error("unable to save database as CSV file", e);
        } catch (SystemErrorException se) {
            logger.error("Unable to save database as CSV file", se);
        }
    }
}
