
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy.items;

//~--- JDK imports ------------------------------------------------------------

import java.util.Collection;

/**
 *
 * @author kec
 */
public class LeafItem extends Item {
   public LeafItem(Item node) {
      super(node);
   }
   public LeafItem(int cnid, int parentNid, long parentNodeId) {
      super(cnid, parentNid, parentNodeId);
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public boolean addChild(Item child) {
      throw new UnsupportedOperationException("Leaf nodes can't have children. ");
   }

   @Override
   public void addExtraParent(Item extraParent) {
      throw new UnsupportedOperationException("Node can't have extra parent. ");
   }

   @Override
   public boolean childrenAreSet() {
      return true;
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public Collection<Long> getChildrenNodeIds() {
      return empty;
   }

   @Override
   public Collection<Long> getExtraParents() {
      return empty;
   }

   @Override
   public Item getFinalNode() {
      return this;
   }

   @Override
   public boolean hasExtraParents() {
      return false;
   }

   @Override
   public boolean isLeaf() {
      return true;
   }

   @Override
   public boolean isSecondaryParentOpened() {
      return false;
   }

   //~--- set methods ---------------------------------------------------------

   @Override
   public void setSecondaryParentOpened(boolean secondaryParentOpened) {

      //
   }
}
