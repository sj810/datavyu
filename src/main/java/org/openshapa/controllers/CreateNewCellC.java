package org.openshapa.controllers;


import org.openshapa.OpenSHAPA;

import org.openshapa.models.db.DataCell;
import org.openshapa.models.db.DataColumn;
import org.openshapa.models.db.Database;
import org.openshapa.models.db.MatrixVocabElement;
import org.openshapa.models.db.SystemErrorException;
import org.openshapa.models.db.TimeStamp;

import org.openshapa.util.Constants;

import com.usermetrix.jclient.UserMetrix;

import java.util.ArrayList;
import java.util.Vector;

import org.jdesktop.swingworker.SwingWorker;

import org.openshapa.views.discrete.SpreadsheetPanel;


/**
 * Controller for creating new cell.
 */
public final class CreateNewCellC extends SwingWorker<Object, String> {

    /** The logger for this class. */
    private UserMetrix logger = UserMetrix.getInstance(CreateNewCellC.class);

    /** The model (the database) for this controller. */
    private Database model;

    /** Onset time to use for the new cells or -1 if onset not specified. */
    private long onset;

    /** List of selected columns that we are creating cells within. */
    private Vector<DataColumn> selectedColumns;

    /** List of select cells that we are creating cells beneath. */
    private Vector<DataCell> selectedCells;

    /**
     * Default constructor.
     */
    public CreateNewCellC() {
        initalise(-1);
    }

    /**
     * Constructor - creates new controller.
     *
     * @param milliseconds The milliseconds to use for the onset for the new
     * cell.
     */
    public CreateNewCellC(final long milliseconds) {
        initalise(milliseconds);
    }

    /**
     * Initalises the create new cell controller.
     *
     * @param milliseconds The onset time in milliseconds to use for the new
     * cells.
     */
    private void initalise(final long milliseconds) {

        // The spreadsheet is the view for this controller.
        SpreadsheetPanel view = (SpreadsheetPanel) OpenSHAPA.getApplication()
            .getMainView().getComponent();

        selectedCells = view.getSelectedCells();
        selectedColumns = view.getSelectedCols();
        model = OpenSHAPA.getProjectController().getDB();
        onset = milliseconds;
    }

    /**
     * The task to perform in the background.
     *
     * Create a new cell with given onset. Has the following scenarios:
     *
     * Situation 1: Spreadsheet has one or more selected columns For each
     * selected column do Create a new cell with the supplied onset and
     * insert into db.
     *
     * Situation 2: Spreadsheet has one or more selected cells For each
     * selected cell do Create a new cell with the selected cell onset and
     * offset and insert into the db.
     *
     * Situation 3: User has set focus on a particular cell in the spreadsheet
     * - the caret is or has been in one of the editable parts of a spreadsheet
     * cell. First check this request has not come from the video controller.
     * For the focussed cell do Create a new cell with the focussed cell onset
     * and offset and insert into the db.
     *
     * Situation 4: Request has come from the video controller and there is no
     * currently selected column. Create a new cell in the same column as the
     * last created cell or the last focussed cell.
     */
    @Override protected Object doInBackground() {

        try {

            // BugzID:758 - Before creating a new cell and setting onset. We
            // need the last created cell and need to set the previous cells
            // offset... But only if it is not 0.
            final long lastCreatedCellId = OpenSHAPA.getProjectController()
                .getLastCreatedCellId();

            if (lastCreatedCellId != 0) {
                DataCell dc = (DataCell) model.getCell(lastCreatedCellId);

                // BugzID:1285 - Only update the last created cell if it is in
                // the same column as the newly created cell.
                ArrayList<Long> matchingColumns = new ArrayList<Long>();

                for (DataColumn col : selectedColumns) {
                    matchingColumns.add(col.getID());
                }

                if (matchingColumns.size() == 0) {
                    matchingColumns.add(OpenSHAPA.getProjectController()
                        .getLastCreatedColId());
                }

                for (Long colID : matchingColumns) {

                    if (colID == dc.getItsColID()) {
                        TimeStamp ts = dc.getOffset();

                        if (ts.getTime() == 0) {
                            ts.setTime(Math.max(0, (onset - 1)));
                            dc.setOffset(ts);
                            model.replaceCell(dc);
                        }
                    }
                }
            }


            // if not coming from video controller (milliseconds < 0) allow
            // multiple adds
            boolean multiadd = (onset < 0);

            if (onset < 0) {
                onset = 0;
            }

            long cellID = 0;

            boolean newcelladded = false;

            // check for Situation 1: one or more selected columns
            for (DataColumn col : selectedColumns) {
                MatrixVocabElement mve = model.getMatrixVE(col.getItsMveID());
                DataCell cell = new DataCell(col.getDB(), col.getID(),
                        mve.getID());
                cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, onset));

                if (onset > 0) {
                    cellID = model.appendCell(cell);
                    OpenSHAPA.getProjectController().setLastCreatedCellId(
                        cellID);
                } else {
                    cellID = model.insertdCell(cell, 1);
                    OpenSHAPA.getProjectController().setLastCreatedCellId(
                        cellID);
                }

                OpenSHAPA.getProjectController().setLastCreatedColId(
                    col.getID());
                newcelladded = true;

                if (!multiadd) {
                    break;
                }
            }

            if (!newcelladded) {

                // else check for Situation 2: one or more selected cells
                for (DataCell cell : selectedCells) {

                    // reget the selected cell from the database using its id
                    // in case a previous insert has changed its ordinal.
                    // recasting to DataCell without checking as the iterator
                    // only returns DataCells (no ref cells allowed so far)
                    DataCell dc = (DataCell) model.getCell(cell.getID());
                    DataCell newCell = new DataCell(model, dc.getItsColID(),
                            dc.getItsMveID());

                    if (multiadd) {
                        newCell.setOnset(dc.getOnset());
                        newCell.setOffset(dc.getOffset());
                        cellID = model.insertdCell(newCell, dc.getOrd() + 1);
                        OpenSHAPA.getProjectController().setLastCreatedCellId(
                            cellID);
                    } else {
                        newCell.setOnset(new TimeStamp(
                                Constants.TICKS_PER_SECOND, onset));
                        cellID = model.appendCell(newCell);
                        OpenSHAPA.getProjectController().setLastCreatedCellId(
                            cellID);
                    }

                    OpenSHAPA.getProjectController().setLastCreatedColId(
                        newCell.getItsColID());
                    newcelladded = true;

                    if (!multiadd) {
                        break;
                    }
                }
            }

            if (!newcelladded && multiadd) {

                // else check for Situation 3: User is or was editing an
                // existing cell and has requested a new cell
                if (OpenSHAPA.getProjectController().getLastSelectedCellId()
                        != 0) {
                    DataCell dc = (DataCell) model.getCell(OpenSHAPA
                            .getProjectController().getLastSelectedCellId());
                    DataCell cell = new DataCell(model, dc.getItsColID(),
                            dc.getItsMveID());
                    cell.setOnset(dc.getOnset());
                    cell.setOffset(dc.getOffset());
                    cellID = model.insertdCell(cell, dc.getOrd() + 1);
                    OpenSHAPA.getProjectController().setLastCreatedCellId(
                        cellID);
                    OpenSHAPA.getProjectController().setLastCreatedColId(
                        cell.getItsColID());
                    newcelladded = true;
                }
            }

            if (!newcelladded) {
                // else go with Situation 4: Video controller requested
                // - create in the same column as the last created cell or
                // the last focused cell.

                // BugzID:779 - Check for presence of columns, else return
                if (model.getDataColumns().size() == 0) {
                    return null;
                }

                if (OpenSHAPA.getProjectController().getLastCreatedColId()
                        == 0) {
                    OpenSHAPA.getProjectController().setLastCreatedColId(
                        model.getDataColumns().get(0).getID());
                }

                // would throw by now if no columns exist
                DataColumn col = model.getDataColumn(OpenSHAPA
                        .getProjectController().getLastCreatedColId());

                DataCell cell = new DataCell(col.getDB(), col.getID(),
                        col.getItsMveID());
                cell.setOnset(new TimeStamp(Constants.TICKS_PER_SECOND, onset));
                cellID = model.appendCell(cell);
                OpenSHAPA.getProjectController().setLastCreatedCellId(cellID);
            }
        } catch (SystemErrorException se) {
            logger.error("Unable to create new cell.", se);
        }

        return null;
    }
}
