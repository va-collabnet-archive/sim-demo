/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.fx.expression;

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
public class ExpressionHelper {
    public static TreeItem<ExpressionComponentBI> makeTreeItems(ExpressionBI expression, boolean expanded) throws IOException {
        ExpressionNodeBI focus = expression.getFocus();
        TreeItem<ExpressionComponentBI> root = new TreeItem<ExpressionComponentBI>(focus);
        root.setExpanded(expanded);
        addChildren(root, expanded);
        
        
        return root;
    } 
    
    private static void addChildren(TreeItem<ExpressionComponentBI> parentItem, boolean expanded) throws IOException {
        ExpressionNodeBI parentExpNode = (ExpressionNodeBI) parentItem.getValue();
        for (ExpressionRelBI rel: parentExpNode.getAllRels()) {
            TreeItem<ExpressionComponentBI> relItem = new TreeItem<ExpressionComponentBI>(rel);
            parentItem.getChildren().add(relItem);
            TreeItem<ExpressionComponentBI> destItem = new TreeItem<ExpressionComponentBI>(rel.getDestination());
            relItem.getChildren().add(destItem);
            relItem.setExpanded(expanded);
            destItem.setExpanded(expanded);
            addChildren(destItem, expanded);
        }
    }
}
