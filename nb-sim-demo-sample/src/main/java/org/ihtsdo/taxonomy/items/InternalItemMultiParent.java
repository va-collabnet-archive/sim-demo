
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy.items;

//~--- JDK imports ------------------------------------------------------------

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * @author kec
 */
public class InternalItemMultiParent extends InternalItem {
   public ConcurrentSkipListSet<Long> extraParents          = new ConcurrentSkipListSet<Long>();
   boolean                            hasExtraParents       = false;
   boolean                            secondaryParentOpened = false;
   boolean                            isLeaf                = false;

   //~--- constructors --------------------------------------------------------

   public InternalItemMultiParent(int cnid, int parentNid, long parentNodeId, Comparator nodeComparator) {
      super(cnid, parentNid, parentNodeId, nodeComparator);
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public void addExtraParent(Item extraParent) {
      this.extraParents.add(extraParent.nodeId);
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public ConcurrentSkipListSet<Long> getExtraParents() {
      return extraParents;
   }

   @Override
   public Item getFinalNode() {
      if (!this.hasExtraParents) {
         if (this.isLeaf) {
            return new LeafItem(this);
         } else {
            if (isSecondaryParentNode()) {
               return new LeafItem(this);
            } else {
               return new InternalItem(this);
            }
         }
      }

      if (this.isLeaf) {
         return new LeafItemMultiParent(this);
      }

      return this;
   }

   @Override
   public boolean hasExtraParents() {
      return hasExtraParents;
   }

   @Override
   public boolean isLeaf() {
      return isLeaf;
   }

   @Override
   public boolean isSecondaryParentOpened() {
      return secondaryParentOpened;
   }

   //~--- set methods ---------------------------------------------------------

   @Override
   public void setHasExtraParents(boolean hasExtraParents) {
      this.hasExtraParents = hasExtraParents;
   }

   public void setIsLeaf(boolean isLeaf) {
      this.isLeaf = isLeaf;
   }

   @Override
   public void setSecondaryParentOpened(boolean secondaryParentOpened) {
      this.secondaryParentOpened = secondaryParentOpened;
   }
}
