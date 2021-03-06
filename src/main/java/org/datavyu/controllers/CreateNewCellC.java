/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.datavyu.controllers;

import com.usermetrix.jclient.Logger;


import org.datavyu.Datavyu;


import org.datavyu.util.ArrayDirection;

import org.datavyu.views.discrete.SpreadsheetPanel;

import com.usermetrix.jclient.UserMetrix;
import java.util.List;
import javax.swing.undo.UndoableEdit;
import org.datavyu.models.db.Cell;
import org.datavyu.models.db.Datastore;
import org.datavyu.models.db.Variable;
import org.datavyu.undoableedits.AddCellEdit;


/**
 * Controller for creating new cell.
 */
public final class CreateNewCellC {

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(CreateNewCellC.class);

    /** The view (the spreadsheet) for this controller. */
    private SpreadsheetPanel view;

    /** The model (the database) for this controller. */
    private Datastore model;

    /**
     * Default constructor.
     */
    public CreateNewCellC() {
        // The spreadsheet is the view for this controller.
        view = (SpreadsheetPanel) Datavyu.getView().getComponent();
        model = Datavyu.getProjectController().getDB();
    }

    /**
     * Inserts a cell into the end of the supplied variable.
     *
     * @param v The variable that we want to add a cell too.
     *
     * @return The cell that was just inserted.
     */
    public Cell createCell(final Variable v) {
        LOGGER.event("create cell in selected column");

        // perform the operation
        List<Cell> cells = v.getCellsTemporally();

        long newOnset = 0;
        newOnset = Datavyu.getDataController().getCurrentTime();

        Cell newCell = v.createCell();
        newCell.setOnset(newOnset);
        Datavyu.getProjectController().setLastCreatedCell(newCell);
        Datavyu.getProjectController().setLastCreatedVariable(v);

        return newCell;
    }

    /**
     * Create a default cell at the end of the nominated variable.
     *
     * @param v The variable we are adding a cell to the end of.
     */
    public void createDefaultCell(final Variable v) {
        model.deselectAll();
        createCell(v);

        // record the effect
        UndoableEdit edit = new AddCellEdit(v.getName());

        // Display any changes.
        Datavyu.getView().getComponent().revalidate();
        // notify the listeners
        Datavyu.getView().getUndoSupport().postEdit(edit);
    }

    /**
     * Create a default cell - at the end of the selected variables.
     */
    public void createDefaultCell() {
        Cell newCell = null;
        Variable v = Datavyu.getProjectController().getLastCreatedVariable();
        if (v != null) {
            newCell = createCell(v);

            // record the effect
            UndoableEdit edit = new AddCellEdit(v.getName());
            Datavyu.getView().getComponent().revalidate();
            Datavyu.getView().getUndoSupport().postEdit(edit);
        }

        if (newCell != null) {
            model.deselectAll();
            newCell.setHighlighted(true);
        }
    }

    /**
     * Create New Cell Controller - creates new cells in columns adjacent to the
     * supplied cells. If no column is adjacent in the specified direction, no
     * cell will be created.
     *
     * @param sourceCells The list of source cells that we wish to create cells
     * adjacent too.
     * @param direction The direction in which we wish to create adjacent cells.
     */
    public CreateNewCellC(final List<Cell> sourceCells,
                          final ArrayDirection direction) {
        view = (SpreadsheetPanel) Datavyu.getView().getComponent();
        model = Datavyu.getProjectController().getDB();

        Cell newCell = null;

        LOGGER.event("create adjacent cells:" + direction);

        // Get the column that is the parent of the source cell.
        for (Cell sourceCell : sourceCells) {

            Variable sourceColumn = model.getVariable(sourceCell);
            //long sourceColumn = sourceCell.getItsColID();
            //Vector<Long> columnOrder = modelAsLegacyDB().getColOrderVector();

            for (int i = 0; i < model.getAllVariables().size(); i++) {

                // Found the source column in the order column.
                if (model.getAllVariables().get(i).equals(sourceColumn)) {
                    i = i + direction.getModifier();

                    // Only create the cell if a valid column exists.
                    if ((i >= 0) && (i < model.getAllVariables().size())) {
                        Variable var = model.getAllVariables().get(i);
                        newCell = var.createCell();
                        newCell.setOnset(sourceCell.getOnset());
                        newCell.setOffset(sourceCell.getOffset());
                        Datavyu.getProjectController().setLastCreatedCell(newCell);

                        // Add the undoable action
                        UndoableEdit edit = new AddCellEdit(var.getName());

                        // notify the listeners
                        Datavyu.getView().getUndoSupport().postEdit(edit);
                        break;
                    }

                    break;
                }
            }
        }

        model.deselectAll();
        newCell.setHighlighted(true);
    }

    /**
     * Constructor - creates new controller.
     *
     * @param milliseconds The milliseconds to use for the onset for the new cell.
     */
    public CreateNewCellC(final long milliseconds) {
        // The spreadsheet is the view for this controller.
        view = (SpreadsheetPanel) Datavyu.getView().getComponent();
        model = Datavyu.getProjectController().getDB();

        // BugzID:758 - Before creating a new cell and setting onset. We need
        // the last created cell and need to set the previous cells offset...
        // But only if it is not 0.
        Cell lastCreatedCell = Datavyu.getProjectController().getLastCreatedCell();

        if (lastCreatedCell != null) {
            // BugzID:1285 - Only update the last created cell if it is in
            // the same column as the newly created cell.
            for (Variable var : model.getSelectedVariables()) {
                if (var.contains(lastCreatedCell)) {
                    lastCreatedCell.setOffset(Math.max(0, (milliseconds - 1)));
                }
            }

            Variable lastCreated = Datavyu.getProjectController().getLastCreatedVariable();
            if (model.getSelectedVariables().isEmpty() && lastCreated != null) {
                if (lastCreated.contains(lastCreatedCell)) {
                    lastCreatedCell.setOffset(Math.max(0, (milliseconds - 1)));
                }
            }
        }

        // Create the new cell.
        createNewCell(milliseconds);
    }

    /**
     * Create a new cell with given onset. Currently just appends to the
     * selected column or the column that last had a cell added to it.
     *
     * @param milliseconds The number of milliseconds since the origin of the
     * spreadsheet to create a new cell from.
     */
    public void createNewCell(final long milliseconds) {

        /*
         * Concept of operation: Creating a new cell.
         *
         * Situation 1: Spreadsheet has one or more selected columns For each
         * selected column do Create a new cell with the supplied onset and
         * insert into db.
         *
         * Situation 2: Spreadsheet has one or more selected cells For each
         * selected cell do Create a new cell with the selected cell onset and
         * offset and insert into the db.
         *
         * Situation 3: User has set focus on a particular cell in the
         * spreadsheet - the caret is or has been in one of the editable parts
         * of a spreadsheet cell. First check this request has not come from the
         * video controller. For the focussed cell do Create a new cell with the
         * focussed cell onset and offset and insert into the db.
         *
         * Situation 4: Request has come from the video controller and there is
         * no currently selected column. Create a new cell in the same column as
         * the last created cell or the last focussed cell.
         */
        long onset = milliseconds;

        // If not coming from video controller (milliseconds < 0) allow
        // multiple adds
        boolean multiadd = (milliseconds < 0);

        if (milliseconds < 0) {
            onset = 0;
        }

        Cell newCell = null;
        boolean newcelladded = false;

        // check for Situation 1: one or more selected columns
        model = Datavyu.getProjectController().getDB();

        for (Variable var : model.getSelectedVariables()) {
            LOGGER.event("create cell in selected column");
            newCell = var.createCell();
            newCell.setOnset(onset);
            Datavyu.getProjectController().setLastCreatedCell(newCell);
            Datavyu.getProjectController().setLastCreatedVariable(var);

            // Add the undoable action
            UndoableEdit edit = new AddCellEdit(var.getName());
            Datavyu.getView().getUndoSupport().postEdit(edit);

            newcelladded = true;

            if (!multiadd) {
                break;
            }
        }

        if (!newcelladded) {
            for (Cell cell : model.getSelectedCells()) {
                LOGGER.event("create cell below selected cell");

                // reget the selected cell from the database using its id
                // in case a previous insert has changed its ordinal.
                // recasting to DataCell without checking as the iterator
                // only returns DataCells (no ref cells allowed so far)
                Variable var = model.getVariable(cell);
                newCell = var.createCell();
                newCell.setOnset(onset);
                Datavyu.getProjectController().setLastCreatedCell(newCell);
                Datavyu.getProjectController().setLastCreatedVariable(var);

                // Add the undoable action
                UndoableEdit edit = new AddCellEdit(var.getName());
                Datavyu.getView().getUndoSupport().postEdit(edit);

                newcelladded = true;

                if (!multiadd) {
                    break;
                }
            }
        }

        // else check for Situation 3: User is or was editing an existing cell
        // and has requested a new cell
        if (!newcelladded && multiadd) {
            if (Datavyu.getProjectController().getLastSelectedCell() != null) {
                LOGGER.event("create cell while editing existing cell");
                Variable var = model.getVariable(Datavyu.getProjectController().getLastCreatedCell());
                if (var != null) {
                    newCell = var.createCell();
                    newCell.setOnset(onset);
                    Datavyu.getProjectController().setLastCreatedCell(newCell);
                    Datavyu.getProjectController().setLastCreatedVariable(var);

                    // Add the undoable action
                    UndoableEdit edit = new AddCellEdit(var.getName());
                    Datavyu.getView().getUndoSupport().postEdit(edit);

                    newcelladded = true;
                }
            }
        }

        // else go with Situation 4: Video controller requested - create in the
        // same column as the last created cell or the last focused cell.
        if (!newcelladded) {
            LOGGER.event("create cell in same location as last created cell");

            // BugzID:779 - Check for presence of columns, else return
            if (model.getAllVariables().isEmpty()) {
                return;
            }

            if (Datavyu.getProjectController().getLastCreatedVariable() == null) {
                Datavyu.getProjectController().setLastCreatedVariable(model.getAllVariables().get(0));
            }

            newCell = Datavyu.getProjectController().getLastCreatedVariable().createCell();
            newCell.setOnset(onset);
            Datavyu.getProjectController().setLastCreatedCell(newCell);

            // Add the undoable action
            UndoableEdit edit = new AddCellEdit(Datavyu.getProjectController().getLastCreatedVariable().getName());
            Datavyu.getView().getUndoSupport().postEdit(edit);
        }

        model.deselectAll();
        newCell.setHighlighted(true);
    }
}
