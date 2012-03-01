
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy.model;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.taxonomy.model.TaxonomyModel;
import org.ihtsdo.taxonomy.items.RootItem;
import org.ihtsdo.taxonomy.items.Item;

//~--- JDK imports ------------------------------------------------------------

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.tree.TreePath;

/**
 *
 * @author kec
 */
@Deprecated
public class NodePath {
   public static TreePath getTreePath(TaxonomyModel model, Item node) {
      if (node instanceof RootItem) {
         return new TreePath(node);
      }

      LinkedList<Long> idList = new LinkedList<Long>();

      idList.addLast(node.nodeId);

      Item parentNode = model.getNodeStore().get(node.parentNodeId);

      while (parentNode != null) {
         idList.addLast(parentNode.nodeId);

         if ((model.getNodeStore().get(parentNode.parentNodeId) != null)
                 && (parentNode.nodeId != model.getNodeStore().get(parentNode.parentNodeId).nodeId)) {
            parentNode = model.getNodeStore().get(parentNode.parentNodeId);
         } else {
            parentNode = null;
         }
      }

      Item[] nodes   = new Item[idList.size()];
      int            index   = 0;
      Iterator<Long> descItr = idList.descendingIterator();

      while (descItr.hasNext()) {
         nodes[index++] = model.getNodeStore().get(descItr.next());
      }

      return new TreePath(nodes);
   }
}
