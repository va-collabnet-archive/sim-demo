/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.fx;

import gov.va.sim.act.expression.ExpressionBI;
import gov.va.sim.act.expression.ExpressionComponentBI;
import gov.va.sim.act.expression.ExpressionRelBI;
import gov.va.sim.act.expression.node.ExpressionNodeBI;
import java.io.IOException;
import javafx.scene.control.TreeItem;

/**
 *
 * @author kec
 */
public class TreeHelper {
    public static TreeItem<ExpressionComponentBI> makeTreeItems(ExpressionBI expression) throws IOException {
        ExpressionNodeBI focus = expression.getFocus();
        TreeItem<ExpressionComponentBI> root = new TreeItem<ExpressionComponentBI>(focus);
        addChildren(root);
        
        
        return root;
    } 
    
    private static void addChildren(TreeItem<ExpressionComponentBI> parentItem) throws IOException {
        ExpressionNodeBI parentExpNode = (ExpressionNodeBI) parentItem.getValue();
        for (ExpressionRelBI rel: parentExpNode.getAllRels()) {
            TreeItem<ExpressionComponentBI> relItem = new TreeItem<ExpressionComponentBI>(rel);
            parentItem.getChildren().add(relItem);
            TreeItem<ExpressionComponentBI> destItem = new TreeItem<ExpressionComponentBI>(rel.getDestination());
            addChildren(destItem);
        }
    }
}
