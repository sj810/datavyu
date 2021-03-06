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
package org.datavyu.views.discrete.datavalues;

import com.usermetrix.jclient.Logger;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.text.JTextComponent;

import org.datavyu.views.discrete.EditorComponent;

import com.usermetrix.jclient.UserMetrix;
import javax.swing.undo.UndoableEdit;
import org.datavyu.Datavyu;

import org.datavyu.models.db.MatrixValue;
import org.datavyu.models.db.NominalValue;
import org.datavyu.models.db.TextValue;
import org.datavyu.models.db.Value;

/**
 * DataValueEditor - abstract class extending EditorComponent. Adds
 * functionality specific to DataValues being edited.
 */
public abstract class DataValueEditor extends EditorComponent {

    /** The value that this view represents. */
    private Value model = null;
    
    /** The specific type of model this view represents. */
    private Class modelType = null;

    /** Text when editor gained focus (became current editor). */
    private String textOnFocus;

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(DataValueEditor.class);   

    /**
     * Constructor.
     * 
     * @param tc JTextComponent this editor works with.
     * @param Value The value this data value editor manipulates
     */
    public DataValueEditor(final JTextComponent tc,
                           final Value value) {
        super(tc);
        
        // So far all DataValueEditors are editable
        setEditable(true);
        model = value;

        if (value instanceof TextValue) {
            modelType = TextValue.class;
        } else if (value instanceof NominalValue) {
            modelType = NominalValue.class;
        } else {
            modelType = MatrixValue.class;
        }

        resetText(model.toString());
    }

    private void updateModelText() {
        if (!modelType.equals(MatrixValue.class)) {
            if (getText() == null || getText().equals("")) {
                model.clear();
            } else {
                model.set(getText());
            }
        }
    }

    // *************************************************************************
    // FocusListener Overrides
    // *************************************************************************
    @Override
    public void focusGained(final FocusEvent fe) {        
        textOnFocus = getText();
    }

    @Override
    public void focusLost(final FocusEvent fe) {
        super.focusLost(fe);
        if (!getText().equals(textOnFocus)) {
/*  
            //Q: How can I get the Cell reference from the value
            UndoableEdit edit = new ChangeValCellEdit(cell);                        
            // notify the listeners
            Datavyu.getView().getUndoSupport().postEdit(edit);
*/
        }   
    }

    // *************************************************************************
    // KeyListener Overrides
    // *************************************************************************
    @Override
    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_BACK_SPACE:
            if (!model.isEmpty()) {
                removeBehindCaret();
                updateModelText();
            }
            e.consume();
            break;
        case KeyEvent.VK_DELETE:
            if (!model.isEmpty()) {
                removeAheadOfCaret();
                updateModelText();
            }
            e.consume();
            break;

        case KeyEvent.VK_LEFT:
            int selectStart = getSelectionStart();
            int selectEnd = getSelectionEnd();

            // Move caret to the left.
            int c = Math.max(0, getCaretPosition() - 1);
            setCaretPosition(c);

            // If after the move, we have a character to the left is
            // preserved character we need to skip one before passing
            // the key event down to skip again (effectively skipping
            // the preserved character).
            int b = Math.max(0, getCaretPosition());
            c = Math.max(0, getCaretPosition() - 1);
            if (isPreserved(getText().charAt(b))
                    || isPreserved(getText().charAt(c))) {
                setCaretPosition(Math.max(0, getCaretPosition() - 1));
            }
            e.consume();

            // If the user is holding down shift - alter the selection as
            // well as the caret position.
            if (e.getModifiers() == InputEvent.SHIFT_MASK) {
                // Shrink selection left - removed entire selection.
                if (getCaretPosition() == selectStart) {
                    select(selectStart, selectStart);
                    // Grow selection left.
                } else if (getCaretPosition() < selectStart) {
                    select(selectEnd, getCaretPosition());
                    // Shrink selection left.
                } else {
                    select(selectStart, getCaretPosition());
                }
            }

            break;

        case KeyEvent.VK_RIGHT:
            selectStart = getSelectionStart();
            selectEnd = getSelectionEnd();

            // Move caret to the right.
            c = Math.min(getText().length(), getCaretPosition() + 1);
            setCaretPosition(c);

            // If after the move, we have a character to the right that
            // is a preserved character, we need to skip one before
            // passing the key event down to skip again (effectively
            // skipping the preserved character)
            b = Math.min(getText().length() - 1, getCaretPosition());
            c = Math.min(getText().length() - 1, getCaretPosition() + 1);
            if (c < getText().length()
                    && (isPreserved(getText().charAt(c)) || isPreserved(getText()
                            .charAt(b)))) {
                setCaretPosition(Math.min(getText().length() - 1,
                        getCaretPosition() + 1));
            }
            e.consume();

            // If the user is holding down shift - alter the selection as
            // well as the caret position.
            if (e.getModifiers() == InputEvent.SHIFT_MASK) {
                // Shrink selection right - removed entire selection.
                if (getCaretPosition() == selectEnd) {
                    select(selectEnd, selectEnd);
                    // Grow selection right.
                } else if (getCaretPosition() > selectEnd) {
                    select(selectStart, getCaretPosition());
                    // Shrink select right.
                } else {
                    select(getCaretPosition(), selectEnd);
                }
            }

            break;

        default:
            break;
        }
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        if (getText().length() == 0) {
            setText(model.toString());
            selectAll();
        }
    }

    /**
     * Determine if the editor tracker permits sub selections within the
     * component.
     * 
     * @return True if permitted to perform sub selection, false otherwise.
     */
    @Override
    public boolean canSubSelect() {
        return (!model.isEmpty());
    }

    /**
     * Override selection to catch if the value is null.
     * 
     * @param startClick Start character of the selection.
     * @param endClick End character of the selection.
     */
    @Override
    public final void select(final int startClick, final int endClick) {
        if (model.isEmpty()) {
            selectAll();
        } else {
            super.select(startClick, endClick);
        }
    }
}
