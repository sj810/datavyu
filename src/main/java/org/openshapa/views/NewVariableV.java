package org.openshapa.views;

import org.openshapa.OpenSHAPA;
import org.openshapa.db.DataColumn;
import org.openshapa.db.Database;
import org.openshapa.db.LogicErrorException;
import org.openshapa.db.MatrixVocabElement;
import org.openshapa.db.SystemErrorException;
import org.apache.log4j.Logger;
import org.openshapa.db.Column;
import org.openshapa.db.NominalFormalArg;
import org.openshapa.views.discrete.SpreadsheetPanel;

/**
 * The dialog for users to add new variables to the spreadsheet.
 */
public final class NewVariableV extends OpenSHAPADialog {

    /** The database to add the new variable too. */
    private Database model;

    /** The logger for this class. */
    private static Logger logger = Logger.getLogger(NewVariableV.class);

    /**
     * Constructor, creates a new form to create a new variable.
     *
     * @param parent The parent of this form.
     * @param modal Should the dialog be modal or not?
     */
    public NewVariableV(final java.awt.Frame parent,
                        final boolean modal) {
        super(parent, modal);
        initComponents();
        setName(this.getClass().getSimpleName());

        model = OpenSHAPA.getDB();

        // init button group
        buttonGroup1.add(textTypeButton);
        buttonGroup1.add(nominalTypeButton);
        buttonGroup1.add(predicateTypeButton);
        buttonGroup1.add(matrixTypeButton);
        buttonGroup1.add(integerTypeButton);
        buttonGroup1.add(floatTypeButton);

        this.getRootPane().setDefaultButton(okButton);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        textTypeButton = new javax.swing.JRadioButton();
        nominalTypeButton = new javax.swing.JRadioButton();
        predicateTypeButton = new javax.swing.JRadioButton();
        matrixTypeButton = new javax.swing.JRadioButton();
        integerTypeButton = new javax.swing.JRadioButton();
        floatTypeButton = new javax.swing.JRadioButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/openshapa/views/resources/NewVariableV"); // NOI18N
        setTitle(bundle.getString("title.text")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);

        jLabel1.setText(bundle.getString("jLabel2.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(bundle.getString("nameField.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        nameField.setName("nameField"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Type:"));
        jPanel1.setName("jPanel1"); // NOI18N

        textTypeButton.setSelected(true);
        textTypeButton.setLabel(bundle.getString("textTypeButton.text")); // NOI18N
        textTypeButton.setName("textTypeButton"); // NOI18N

        nominalTypeButton.setLabel(bundle.getString("nominalTypeButton.text")); // NOI18N
        nominalTypeButton.setName("nominalTypeButton"); // NOI18N

        predicateTypeButton.setLabel(bundle.getString("predicateTypeButton.text")); // NOI18N
        predicateTypeButton.setName("predicateTypeButton"); // NOI18N

        matrixTypeButton.setLabel(bundle.getString("matrixTypeButton.text")); // NOI18N
        matrixTypeButton.setName("matrixTypeButton"); // NOI18N

        integerTypeButton.setLabel(bundle.getString("integerTypeButton.text")); // NOI18N
        integerTypeButton.setName("integerTypeButton"); // NOI18N

        floatTypeButton.setLabel(bundle.getString("floatTypeButton.text")); // NOI18N
        floatTypeButton.setName("floatTypeButton"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(textTypeButton)
                    .add(nominalTypeButton))
                .add(16, 16, 16)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(matrixTypeButton)
                    .add(predicateTypeButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(integerTypeButton)
                    .add(floatTypeButton))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(textTypeButton)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(integerTypeButton)
                            .add(predicateTypeButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(floatTypeButton)
                            .add(matrixTypeButton)
                            .add(nominalTypeButton))))
                .addContainerGap())
        );

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(org.openshapa.OpenSHAPA.class).getContext().getResourceMap(NewVariableV.class);
        okButton.setText(resourceMap.getString("okButton.text")); // NOI18N
        okButton.setName("okButton"); // NOI18N
        okButton.setPreferredSize(new java.awt.Dimension(65, 23));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setLabel(bundle.getString("cancelButton.text")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(layout.createSequentialGroup()
                        .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(nameField))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(nameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cancelButton)
                            .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The action to invoke when the user selects the ok button.
     *
     * @param evt The event that triggered this action.
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        try {
            Column.isValidColumnName(OpenSHAPA.getDB(),
                                     this.getVariableName());
            DataColumn dc = new DataColumn(model,
                                           this.getVariableName(),
                                           this.getVariableType());
            long id = model.addColumn(dc);
            dc = model.getDataColumn(id);

            // If the column is a matrix - default to a single nominal variable
            // rather than untyped.
            if (matrixTypeButton.isSelected()) {
                MatrixVocabElement mve = model.getMatrixVE(dc.getItsMveID());
                mve.deleteFormalArg(0);
                mve.appendFormalArg(new NominalFormalArg(model, "<arg0>"));
                model.replaceMatrixVE(mve);
            }

            // Display any changes.
            SpreadsheetPanel view = (SpreadsheetPanel) OpenSHAPA
                                                       .getApplication()
                                                       .getMainView()
                                                       .getComponent();
            view.relayoutCells();

            this.dispose();
            this.finalize();

        // Whoops, user has done something strange - show warning dialog.
        } catch (LogicErrorException fe) {
            OpenSHAPA.getApplication().showWarningDialog(fe);

        // Whoops, programmer has done something strange - show error
        // message.
        } catch (SystemErrorException e) {
            logger.error("Unable to add variable to database", e);
            OpenSHAPA.getApplication().showErrorDialog();

        // Whoops, unable to destroy dialog correctly.
        } catch (Throwable e) {
            logger.error("Unable to release window NewVariableV.", e);
        }
}//GEN-LAST:event_okButtonActionPerformed

    /**
     * The action to invoke when the user selects the cancel button.
     *
     * @param evt The event that triggered this action.
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        try {
            this.dispose();
            this.finalize();

        // Whoops, unable to destroy dialog correctly.
        } catch (Throwable e) {
            logger.error("Unable to release window NewVariableV.", e);
        }
}//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * @return The name of the new variable the user has specified.
     */
    public String getVariableName() {
        return nameField.getText();
    }

    /**
     * @return The type of variable the user has selected to use.
     */
    public MatrixVocabElement.MatrixType getVariableType() {
        if (textTypeButton.isSelected()) {
            return MatrixVocabElement.MatrixType.TEXT;
        } else if (nominalTypeButton.isSelected()) {
            return MatrixVocabElement.MatrixType.NOMINAL;
        } else if (predicateTypeButton.isSelected()) {
            return MatrixVocabElement.MatrixType.PREDICATE;
        } else if (matrixTypeButton.isSelected()) {
            return MatrixVocabElement.MatrixType.MATRIX;
        } else if (integerTypeButton.isSelected()) {
            return MatrixVocabElement.MatrixType.INTEGER;
        } else if (floatTypeButton.isSelected()) {
            return MatrixVocabElement.MatrixType.FLOAT;
        } else {
            return MatrixVocabElement.MatrixType.UNDEFINED;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton floatTypeButton;
    private javax.swing.JRadioButton integerTypeButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton matrixTypeButton;
    private javax.swing.JTextField nameField;
    private javax.swing.JRadioButton nominalTypeButton;
    private javax.swing.JButton okButton;
    private javax.swing.JRadioButton predicateTypeButton;
    private javax.swing.JRadioButton textTypeButton;
    // End of variables declaration//GEN-END:variables
}
