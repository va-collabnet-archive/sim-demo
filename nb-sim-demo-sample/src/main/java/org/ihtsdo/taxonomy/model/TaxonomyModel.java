
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy.model;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.taxonomy.ItemStore;
import org.ihtsdo.taxonomy.items.Item;
import org.ihtsdo.taxonomy.items.LeafItem;
import org.ihtsdo.taxonomy.items.RootItem;
import org.ihtsdo.taxonomy.items.TaxonomyItemSetup;
import org.ihtsdo.tk.api.NidListBI;
import org.ihtsdo.tk.api.TerminologySnapshotDI;

import org.openide.util.Exceptions;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.Iterator;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.TreeItem;
import org.ihtsdo.taxonomy.items.*;

/**
 *
 * @author kec
 */
public class TaxonomyModel implements EventHandler<TreeItem.TreeModificationEvent<Item>> {
   public static TaxonomyModel singleton;

   //~--- fields --------------------------------------------------------------

   private int                     nextChildIndex = 0;
   protected ItemStore             nodeStore      = new ItemStore();
   private Iterator<Long>          childItr;
   private Item                    lastParentNode;
   protected ItemFactory           nodeFactory;
   private TaxonomyItemWrapper     rootNode;
   protected TerminologySnapshotDI ts;

   //~--- constructors --------------------------------------------------------

   public TaxonomyModel(TerminologySnapshotDI ts, NidListBI roots, TaxonomyItemSetup renderer,
                        ChildItemFilterBI childNodeFilter)
           throws IOException, Exception {
      this.ts     = ts;
      TaxonomyModel.singleton = this;
      nodeFactory = new ItemFactory(this, renderer, childNodeFilter);
      rootNode    = new TaxonomyItemWrapper(new RootItem(nodeFactory.getNodeComparator()), this);
      nodeStore.add(rootNode.getValue());

      for (int cnid : roots.getListArray()) {
         Item child = nodeFactory.makeNode(cnid, rootNode.getValue());

         renderer.setupTaxonomyNode(child, ts.getConceptForNid(cnid));
         rootNode.getValue().addChild(child);
         rootNode.getChildren().add(new TaxonomyItemWrapper(child, this));
      }

      rootNode.getValue().setText("root");
      rootNode.addEventHandler(TaxonomyItemWrapper.<Item>branchCollapsedEvent(), this);
      rootNode.addEventHandler(TaxonomyItemWrapper.<Item>branchExpandedEvent(), this);
   }

    @Override
    public void handle(TreeItem.TreeModificationEvent<Item> t) {
        TaxonomyItemWrapper item = (TaxonomyItemWrapper) t.getTreeItem();
        if (t.wasCollapsed()) {
            t.consume();
        } else if (t.wasExpanded()) {
            getNodeFactory().makeChildNodes(item);
            t.consume();
        }
    }

   //~--- get methods ---------------------------------------------------------

   public Item getChild(Object parent, int index) {
      if (index < 0) {
         System.out.println("Bad Index: " + index);
         index = 0;
      }

      Item parentNode = (Item) parent;

      if (lastParentNode == parentNode) {
         if (nextChildIndex == index) {
            if (childItr != null) {
               return getNextNode();
            }
         }
      }

      lastParentNode = parentNode;
      nextChildIndex = index;
      childItr       = parentNode.getChildrenNodeIds().iterator();

      for (int i = 0; i < index; i++) {
         if (childItr.hasNext()) {
            childItr.next();
         } else {
            return null;
         }
      }

      return getNextNode();
   }

   public int getChildCount(Object parent) {
      if (((Item) parent).getChildrenNodeIds() == null) {
         return 0;
      }

      return ((Item) parent).getChildrenNodeIds().size();
   }

   public static int getCnid(long nodeId) {
      return (int) (nodeId >>> 32);
   }

   public int getIndexOfChild(Object parent, Object child) {
      Iterator<Long> itr = ((Item) parent).getChildrenNodeIds().iterator();

      for (int i = 0; itr.hasNext(); i++) {
         Long ni = itr.next();

         if ((ni == ((Item) child).nodeId)) {
            return i;
         }
      }

      return -1;
   }

   private Item getNextNode() {
      nextChildIndex++;

      if (childItr.hasNext()) {
         Long nsi   = childItr.next();
         Item child = nodeStore.get(nsi);

         if (child != null) {
            return child;
         }

         try {
            return nodeFactory.makeNode(getCnid(nsi), lastParentNode);
         } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
         }
      }

      return null;
   }

   public ItemFactory getNodeFactory() {
      return nodeFactory;
   }

   public static long getNodeId(int cnid, int parentNid) {
      long nodeId = cnid;

      nodeId = nodeId & 0x00000000FFFFFFFFL;

      long nid1Long = parentNid;

      nid1Long = nid1Long & 0x00000000FFFFFFFFL;
      nodeId   = nodeId << 32;
      nodeId   = nodeId | nid1Long;

      return nodeId;
   }

   public ItemStore getNodeStore() {
      return nodeStore;
   }

   public Item getParent(Item node) {
      return nodeStore.get(node.parentNodeId);
   }

   public static int getParentNid(long nodeId) {
      return (int) nodeId;
   }

   public Item[] getPathToRoot(Item aNode) {
      return getPathToRoot(aNode, 0);
   }

   protected Item[] getPathToRoot(Item aNode, int depth) {
      Item[] retNodes;

      if (aNode == null) {
         if (depth == 0) {
            return null;
         } else {
            retNodes = new Item[depth];
         }
      } else {
         depth++;

         if (aNode == rootNode.getValue()) {
            retNodes = new Item[depth];
         } else {
            retNodes = getPathToRoot(nodeStore.get(aNode.parentNodeId), depth);
         }

         retNodes[retNodes.length - depth] = aNode;
      }

      return retNodes;
   }

   public TaxonomyItemWrapper getRoot() {
      return rootNode;
   }

   public TerminologySnapshotDI getTs() {
      return ts;
   }

   public boolean isLeaf(Object node) {
      if (node instanceof LeafItem) {
         return true;
      }

      Item taxonomyNode = (Item) node;

      if ((taxonomyNode == null) || taxonomyNode.isLeaf()) {
         return true;
      }

      return false;
   }

   //~--- set methods ---------------------------------------------------------

   public void setTs(TerminologySnapshotDI snapshot) {
      this.ts = snapshot;
   }
}
