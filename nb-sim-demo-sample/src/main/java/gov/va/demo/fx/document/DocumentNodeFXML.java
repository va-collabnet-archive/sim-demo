/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.fx.document;

import gov.va.demo.dom.DomExpressionAdapter;
import gov.va.demo.nb.sim.jpa.Assertions;
import gov.va.demo.nb.sim.jpa.Documents;
import gov.va.demo.nb.sim.jpa.JpaManager;
import gov.va.sim.impl.expression.Expression;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.util.Exceptions;
import org.xml.sax.SAXException;

/**
 *
 * @author kec
 */
public class DocumentNodeFXML implements Initializable {

    @FXML
    private TableView<DocumentTableWrapper> tableView;
    @FXML
    private WebView documentView;
    @FXML
    private TableColumn patientTC;
    @FXML
    private TableColumn<DocumentTableWrapper, String> patientFirstTC;
    @FXML
    private TableColumn<DocumentTableWrapper, String> patientLastTC;
    @FXML
    private TableColumn<DocumentTableWrapper, String> patientDobTC;
    @FXML
    private TableColumn authorTC;
    @FXML
    private TableColumn<DocumentTableWrapper, String> authorLastTC;
    @FXML
    private TableColumn<DocumentTableWrapper, String> authorFirstTC;
    @FXML
    private TableColumn<DocumentTableWrapper, String> docDateTC;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                patientFirstTC.setCellValueFactory(new Callback<CellDataFeatures<DocumentTableWrapper, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<DocumentTableWrapper, String> p) {
                        // p.getValue() returns the Person instance for a particular TableView row
                        return new ReadOnlyObjectWrapper(p.getValue().getPatientFirstName());
                    }
                });

                patientLastTC.setCellValueFactory(new Callback<CellDataFeatures<DocumentTableWrapper, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<DocumentTableWrapper, String> p) {
                        // p.getValue() returns the Person instance for a particular TableView row
                        return new ReadOnlyObjectWrapper(p.getValue().getPatientLastName());
                    }
                });

                patientDobTC.setCellValueFactory(new Callback<CellDataFeatures<DocumentTableWrapper, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<DocumentTableWrapper, String> p) {
                        // p.getValue() returns the Person instance for a particular TableView row
                        return new ReadOnlyObjectWrapper(p.getValue().getPatientDOB());
                    }
                });

                authorLastTC.setCellValueFactory(new Callback<CellDataFeatures<DocumentTableWrapper, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<DocumentTableWrapper, String> p) {
                        // p.getValue() returns the Person instance for a particular TableView row
                        return new ReadOnlyObjectWrapper(p.getValue().getAuthorLastName());
                    }
                });

                authorFirstTC.setCellValueFactory(new Callback<CellDataFeatures<DocumentTableWrapper, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<DocumentTableWrapper, String> p) {
                        // p.getValue() returns the Person instance for a particular TableView row
                        return new ReadOnlyObjectWrapper(p.getValue().getAuthorFirstName());
                    }
                });

                docDateTC.setCellValueFactory(new Callback<CellDataFeatures<DocumentTableWrapper, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<DocumentTableWrapper, String> p) {
                        // p.getValue() returns the Person instance for a particular TableView row
                        return new ReadOnlyObjectWrapper(p.getValue().getDocTime());
                    }
                });

                // TODO
                EntityManager em = JpaManager.getEntityManager();
                Query getDocumentsQuery = em.createNamedQuery("Documents.findAll");
                List docs = getDocumentsQuery.getResultList();
                ArrayList<DocumentTableWrapper> tableData = new ArrayList<DocumentTableWrapper>(docs.size());
                for (Object d : docs) {
                    tableData.add(new DocumentTableWrapper((Documents) d));
                }

                tableView.getItems().addAll(tableData);

                tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

                    @Override
                    public void changed(ObservableValue observable, Object oldValue,
                            Object newValue) {
                        DocumentTableWrapper selectedDocument = (DocumentTableWrapper) newValue;
                        System.out.println(selectedDocument + " chosen in TableView");
                        StringBuilder sb = new StringBuilder();
                        if (selectedDocument != null) {
                            sb.append("<html>");
                            sb.append("<body>");
                            sb.append("<h1>PATIENT: ");
                            sb.append(selectedDocument.getPatientLastName());
                            sb.append(", ");
                            sb.append(selectedDocument.getPatientFirstName());
                            sb.append("<br> DOB: ");
                            sb.append(selectedDocument.getPatientDOB());
                            sb.append("</h1>");
                            sb.append("<h2>");
                            sb.append("PNCS Document: ");
                            sb.append(selectedDocument.getDocTime());
                            sb.append("<br>Author: ");
                            sb.append(selectedDocument.getAuthorLastName());
                            sb.append(", ");
                            sb.append(selectedDocument.getAuthorFirstName());
                            sb.append("</h2>");


                            for (Assertions a : selectedDocument.getDocument().getAssertionsCollection()) {
                                try {
                                    sb.append("<p>");
                                    Expression exp = DomExpressionAdapter.convertToExpression(a.getDiscernableEnid().getExpression());
                                    
                                    
                                    sb.append(exp.getHtmlFragment(false));
                                    
                                    
                                    sb.append("</p>");
                                } catch (ParserConfigurationException ex) {
                                    Exceptions.printStackTrace(ex);
                                } catch (SAXException ex) {
                                    Exceptions.printStackTrace(ex);
                                } catch (IOException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            }


                            sb.append("</body>");
                            sb.append("</html>");
                        }

                        documentView.getEngine().loadContent(sb.toString());
                    }
                });
            }
        });

    }
}
