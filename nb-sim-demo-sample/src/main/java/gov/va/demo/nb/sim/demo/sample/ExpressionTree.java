/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.nb.sim.demo.sample;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;

/**
 *
 * @author kec
 */
public class ExpressionTree implements Initializable {
    
    public static List<TreeView> trees = new ArrayList<TreeView>();
    
    @FXML
    private TreeView treeView;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        trees.add(treeView);
        System.out.println("Added tree: " + treeView);
    }    
}
