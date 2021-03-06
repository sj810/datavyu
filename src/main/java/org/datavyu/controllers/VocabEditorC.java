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

import javax.swing.JFrame;
import org.datavyu.Datavyu;
import org.datavyu.views.VocabEditorV;

/**
 * A controller for invoking the vocab editor.
 */
public class VocabEditorC {

    /**
     * Constructor.
     */
    public VocabEditorC() {
        // Create the view, register this controller with it and display it.
        JFrame mainFrame = Datavyu.getApplication().getMainFrame();
        VocabEditorV view = new VocabEditorV(mainFrame, false);
        
        /*
        try {
            Datavyu.getProjectController().getLegacyDB().getDatabase().registerVocabListListener(view);
        } catch (SystemErrorException ex) {
            Logger.getLogger(VocabEditorC.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        Datavyu.getApplication().show(view);
    }
}
