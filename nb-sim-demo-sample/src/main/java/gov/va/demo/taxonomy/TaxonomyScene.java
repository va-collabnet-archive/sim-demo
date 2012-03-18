/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.taxonomy;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

/**
 *
 * @author kec
 */
public class TaxonomyScene implements Initializable {
    
    @FXML
    private TreeView treeView;
    
    @FXML
    private VBox conceptVBox;

    public VBox getConceptVBox() {
        return conceptVBox;
    }

    public TreeView getTreeView() {
        return treeView;
    }
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Added tree: " + treeView);
    }    
    
    public void resetRoot(ActionEvent ac) {
        System.out.println("ResetRoot: " + ac);
        TreeItem item = treeView.getRoot();
        treeView.setRoot(null);
        treeView.setRoot(item);
        ac.consume();
    }
}
