/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.nb.sim.demo.sample;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;

/**
 *
 * @author kec
 */
public class ExpressionTree implements Initializable {
    
    @FXML
    private TreeView treeView;

    public TreeView getTreeView() {
        return treeView;
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Added tree: " + treeView);
    }    
}
