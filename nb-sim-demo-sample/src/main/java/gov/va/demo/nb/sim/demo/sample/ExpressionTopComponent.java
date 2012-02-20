/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.nb.sim.demo.sample;

import gov.va.demo.dom.DomExpressionAdapter;
import gov.va.demo.expression.ExpressionManager;
import gov.va.demo.fx.ExpressionTreeCell;
import gov.va.demo.fx.TreeHelper;
import gov.va.demo.nb.sim.jpa.Expressions;
import gov.va.demo.nb.sim.jpa.JpaManager;
import gov.va.demo.terminology.TerminologyService;
import gov.va.sim.act.expression.ExpressionComponentBI;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import gov.va.sim.impl.expression.Expression;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.swing.SwingWorker;
import org.ihtsdo.concept.ConceptVersion;
import org.ihtsdo.tk.api.blueprint.ConceptCB;
import org.ihtsdo.tk.api.blueprint.InvalidCAB;
import org.ihtsdo.tk.api.concept.ConceptVersionBI;
import org.w3c.dom.Document;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//gov.va.demo.nb.sim.demo.sample//Expression//EN",
autostore = false)
@TopComponent.Description(preferredID = "ExpressionTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "gov.va.demo.nb.sim.demo.sample.ExpressionTopComponent")
@ActionReference(path = "Menu/Window" /*
 * , position = 333
 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_ExpressionAction",
preferredID = "ExpressionTopComponent")
@Messages({
    "CTL_ExpressionAction=Expression",
    "CTL_ExpressionTopComponent=Expression Window",
    "HINT_ExpressionTopComponent=This is an Expression window"
})
public final class ExpressionTopComponent extends TopComponent {

    private Expression expression;
    private List<Expression> expressionList;
    private Iterator<Expression> expressionItr;
    private File assertionDefFile;
    private Document assertionDoc;
    private TreeView expressionTreeView;

    public File getAssertionDefFile() {
        return assertionDefFile;
    }

    public void setAssertionDefFile(File assertionDefFileParam) {
        this.assertionDefFile = assertionDefFileParam;
        SwingWorker w = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                FileInputStream fis = new FileInputStream(assertionDefFile);
                assertionDoc = builder.parse(fis);
                expressionList = DomExpressionAdapter.convertToExpressionList(assertionDoc);
                expressionItr = expressionList.iterator();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (expressionItr.hasNext()) {
                        processButton.setEnabled(true);
                        fileProgressBar.setValue(0);
                        fileProgressBar.setMaximum(expressionList.size());
                        fileProgressBar.setIndeterminate(false);
                        fileProgressBar.setStringPainted(true);
                        fileProgressBar.setString("0 of " + expressionList.size());
                    }
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        w.execute();
    }

    public ExpressionTopComponent() {
        initComponents();
        setName(Bundle.CTL_ExpressionTopComponent());
        setToolTipText(Bundle.HINT_ExpressionTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                try {
                    URL resource = getClass().getResource("/gov/va/demo/nb/sim/demo/sample/ExpressionTree.fxml");
                    FXMLLoader loader = new FXMLLoader(resource);
                    Parent root = (Parent) loader.load();
                    ExpressionTree expTree = (ExpressionTree) loader.getController();
                    expressionTreeView = expTree.getTreeView();

                    Scene s = new Scene(root);
                    jFXPanel1.setScene(s);
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
            }
        });


    }

    private boolean checkExpressionTable() {
        EntityManager em = JpaManager.getEntityManager();
        boolean inTable = false;
        try {
            Query countEuuidQuery = em.createNamedQuery("Expressions.countEuuid");
            countEuuidQuery.setParameter("euuid", expression.getUuid().toString());
            List obs = countEuuidQuery.getResultList();
            long count = (Long) obs.get(0);
            if (count > 0) {
                inTable = true;
                inTableText.setText("true");
                addToExpressionTableButton.setEnabled(false);
            } else {
                inTableText.setText("false");
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (NoSuchAlgorithmException ex) {
            Exceptions.printStackTrace(ex);
        }
        return inTable;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this
     * code. The content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        processButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        elConceptNid = new javax.swing.JTextField();
        expressionUuidTextField = new javax.swing.JTextField();
        inTableText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        elConceptUuid = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        addToExpressionTableButton = new javax.swing.JButton();
        jFXPanel1 = new javafx.embed.swing.JFXPanel();
        fileProgressBar = new javax.swing.JProgressBar();
        addToELIndexButton = new javax.swing.JButton();

        setBackground(javax.swing.UIManager.getDefaults().getColor("Tree.textBackground"));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(ExpressionTopComponent.class, "ExpressionTopComponent.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(processButton, org.openide.util.NbBundle.getMessage(ExpressionTopComponent.class, "ExpressionTopComponent.processButton.text")); // NOI18N
        processButton.setActionCommand(org.openide.util.NbBundle.getMessage(ExpressionTopComponent.class, "ExpressionTopComponent.processButton.actionCommand")); // NOI18N
        processButton.setEnabled(false);
        processButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextExpressionButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ExpressionTopComponent.class, "ExpressionTopComponent.jLabel2.text")); // NOI18N

        elConceptNid.setEditable(false);
        elConceptNid.setText(org.openide.util.NbBundle.getMessage(ExpressionTopComponent.class, "ExpressionTopComponent.elConceptNid.text")); // NOI18N
        elConceptNid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elConceptNidActionPerformed(evt);
            }
        });

        expressionUuidTextField.setEditable(false);
        expressionUuidTextField.setText(org.openide.util.NbBundle.getMessage(ExpressionTopComponent.class, "ExpressionTopComponent.expressionUuidTextField.text")); // NOI18N
        expressionUuidTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expressionUuidTextFieldActionPerformed(evt);
            }
        });

        inTableText.setEditable(false);
        inTableText.setText(org.openide.util.NbBundle.getMessage(ExpressionTopComponent.class, "ExpressionTopComponent.inTableText.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ExpressionTopComponent.class, "ExpressionTopComponent.jLabel3.text")); // NOI18N

        elConceptUuid.setEditable(false);
        elConceptUuid.setText(org.openide.util.NbBundle.getMessage(ExpressionTopComponent.class, "ExpressionTopComponent.elConceptUuid.text")); // NOI18N
        elConceptUuid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elConceptUuidActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(ExpressionTopComponent.class, "ExpressionTopComponent.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addToExpressionTableButton, org.openide.util.NbBundle.getMessage(ExpressionTopComponent.class, "ExpressionTopComponent.addToExpressionTableButton.text")); // NOI18N
        addToExpressionTableButton.setEnabled(false);
        addToExpressionTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToExpressionTableButtonActionPerformed(evt);
            }
        });

        jFXPanel1.setBackground(new java.awt.Color(255, 255, 0));
        jFXPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jFXPanel1.setMinimumSize(new java.awt.Dimension(257, 166));

        javax.swing.GroupLayout jFXPanel1Layout = new javax.swing.GroupLayout(jFXPanel1);
        jFXPanel1.setLayout(jFXPanel1Layout);
        jFXPanel1Layout.setHorizontalGroup(
            jFXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jFXPanel1Layout.setVerticalGroup(
            jFXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 164, Short.MAX_VALUE)
        );

        fileProgressBar.setIndeterminate(true);

        org.openide.awt.Mnemonics.setLocalizedText(addToELIndexButton, org.openide.util.NbBundle.getMessage(ExpressionTopComponent.class, "ExpressionTopComponent.addToELIndexButton.text")); // NOI18N
        addToELIndexButton.setEnabled(false);
        addToELIndexButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToELIndexButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jFXPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fileProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(5, 5, 5)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel4))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(expressionUuidTextField)
                                        .addComponent(inTableText)))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(4, 4, 4)
                                            .addComponent(jLabel5)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(elConceptUuid, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE))
                                        .addComponent(addToExpressionTableButton, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGap(12, 12, 12)
                                            .addComponent(jLabel2)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(elConceptNid)))
                                    .addGap(0, 0, Short.MAX_VALUE)))))
                    .addComponent(processButton, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addToELIndexButton, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(173, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(processButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFXPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(expressionUuidTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inTableText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addToELIndexButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(elConceptNid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(elConceptUuid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addToExpressionTableButton)
                .addContainerGap(314, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nextExpressionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextExpressionButtonActionPerformed
        try {

            if (expressionItr != null && expressionItr.hasNext()) {
                expression = expressionItr.next();
                expressionUuidTextField.setText(expression.getUuid().toString());
                fileProgressBar.setValue(fileProgressBar.getValue() + 1);
                fileProgressBar.setString(fileProgressBar.getValue() + " of " + expressionList.size());
                addToELIndexButton.setEnabled(false);
                addToExpressionTableButton.setEnabled(false);
                inTableText.setText("");
                elConceptNid.setText("");
                elConceptUuid.setText("");
                updateExpressionTableInfo();
                Integer cnid = ExpressionManager.getCnid(expression);
                if (cnid != Integer.MIN_VALUE) {
                    elConceptNid.setText(cnid.toString());
                    elConceptUuid.setText(ExpressionManager.getElConceptUuid(expression).toString());
                    addToELIndexButton.setEnabled(false);
                    boolean inTable = checkExpressionTable();
                    addToExpressionTableButton.setEnabled(!inTable);

                } else {
                    elConceptNid.setText("Not in index");
                    elConceptUuid.setText("");
                    addToELIndexButton.setEnabled(true);
                }
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            TreeItem rootItem = TreeHelper.makeTreeItems(expression, true);

                            expressionTreeView.setCellFactory(new Callback<TreeView<ExpressionComponentBI>, TreeCell<ExpressionComponentBI>>() {

                                @Override
                                public ExpressionTreeCell call(TreeView<ExpressionComponentBI> list) {
                                    return new ExpressionTreeCell();
                                }
                            });
                            expressionTreeView.setRoot(rootItem);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                });
            } else {
                addToELIndexButton.setEnabled(false);
                addToExpressionTableButton.setEnabled(false);
                processButton.setEnabled(false);
                expressionUuidTextField.setText("");
                inTableText.setText("");
                elConceptNid.setText("");
                elConceptUuid.setText("");
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        expressionTreeView.setRoot(null);
                    }
                });
            }

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_nextExpressionButtonActionPerformed

    private void elConceptNidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elConceptNidActionPerformed
    }//GEN-LAST:event_elConceptNidActionPerformed

    private void expressionUuidTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expressionUuidTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_expressionUuidTextFieldActionPerformed

    private void elConceptUuidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elConceptUuidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_elConceptUuidActionPerformed

    private void updateExpressionTableInfo() {
        if (expression != null) {
            checkExpressionTable();
        }

    }

    private void addToExpressionTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToExpressionTableButtonActionPerformed
        if (expression != null) {
            EntityManager em = JpaManager.getEntityManager();

            try {

                EntityTransaction entr = em.getTransaction();
                entr.begin();
                Expressions expressionObj = new Expressions();
                expressionObj.setCnid(ExpressionManager.getCnid(expression));
                expressionObj.setEuuid(expression.getUuid().toString());
                expressionObj.setExpression(expression.getXml());
                em.persist(expressionObj);
                entr.commit();

                updateExpressionTableInfo();
                addToExpressionTableButton.setEnabled(false);

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (NoSuchAlgorithmException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_addToExpressionTableButtonActionPerformed

    private void addToELIndexButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToELIndexButtonActionPerformed
        try {
            ConceptCB blueprint = ExpressionManager.getBlueprint(expression);
            TerminologyService.getBuilder().construct(blueprint);
            TerminologyService.commit();
            addToELIndexButton.setEnabled(false);
            ConceptVersionBI newConcept = TerminologyService.getSnapshot().getConceptVersion(blueprint.getComponentUuid());

            elConceptNid.setText("" + newConcept.getNid());
            elConceptUuid.setText(newConcept.getPrimUuid().toString());
            System.out.println(newConcept.toLongString());
            addToExpressionTableButton.setEnabled(true);
        } catch (InvalidCAB ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_addToELIndexButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addToELIndexButton;
    private javax.swing.JButton addToExpressionTableButton;
    private javax.swing.JTextField elConceptNid;
    private javax.swing.JTextField elConceptUuid;
    private javax.swing.JTextField expressionUuidTextField;
    private javax.swing.JProgressBar fileProgressBar;
    private javax.swing.JTextField inTableText;
    private javafx.embed.swing.JFXPanel jFXPanel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JButton processButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
