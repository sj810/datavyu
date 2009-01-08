package au.com.nicta.openshapa.views;

import java.io.IOException;
import java.io.PipedInputStream;
import org.apache.log4j.Logger;

/**
 * The dialog for the scripting console. Renders output from scripts that the
 * user has invoked.
 *
 * @author cfreeman
 */
public class ScriptOutput extends OpenSHAPADialog {

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(ListVariables.class);

    /**
     * Constructor
     *
     * @param parent The parent of this dialog.
     * @param modal Is the scripting console modal or not?
     * @param scriptOutput The stream containing scripting data to be outputed
     * to the console.
     */
    public ScriptOutput(final java.awt.Frame parent,
                        final boolean modal,
                        final PipedInputStream scriptOutput) {
        super(parent, modal);
        initComponents();
        setName(this.getClass().getSimpleName());

        new ReaderThread(scriptOutput).start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        console = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        console.setColumns(20);
        console.setRows(5);
        console.setName("console"); // NOI18N
        jScrollPane1.setViewportView(console);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea console;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    /**
     * Seperate thread for polling the incoming data from the scripting engine.
     * The data from the scripting engine gets placed directly into the
     * consoleOutput
     */
    class ReaderThread extends Thread {
        /** The output from the scripting engine. */
        PipedInputStream output;

        /**
         * Constructor.
         *
         * @param scriptOutput The stream containing output from the scripting
         * engine.
         */
        ReaderThread(final PipedInputStream scriptOutput) {
            output = scriptOutput;
        }

        /**
         * The method to invoke when the thread is started.
         */
        @Override
        public void run() {
            final byte[] buf = new byte[1024];
            try {
                while(true) {
                    final int len = output.read(buf);
                    if (len > 0) {
                        console.append(new String(buf, 0, len));
                        // Make sure the last line is always visible
                        console.setCaretPosition(console.getDocument()
                                                        .getLength());
                    }
                }
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
}