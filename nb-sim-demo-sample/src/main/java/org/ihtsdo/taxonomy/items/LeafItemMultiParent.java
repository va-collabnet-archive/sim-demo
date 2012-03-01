
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy.items;

//~--- JDK imports ------------------------------------------------------------

import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * @author kec
 */
public class LeafItemMultiParent extends LeafItem {
   public ConcurrentSkipListSet<Long> extraParents          = new ConcurrentSkipListSet<Long>();
   boolean                            secondaryParentOpened = false;

   //~--- constructors --------------------------------------------------------

   public LeafItemMultiParent(int cnid, int parentNid, long parentNodeId) {
      super(cnid, parentNid, parentNodeId);
   }

   public LeafItemMultiParent(InternalItemMultiParent another) {
      super(another);
      this.secondaryParentOpened = another.secondaryParentOpened;
      this.extraParents = another.extraParents;
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
   public boolean hasExtraParents() {
      return true;
   }

   @Override
   public boolean isSecondaryParentOpened() {
      return secondaryParentOpened;
   }

   //~--- set methods ---------------------------------------------------------

   @Override
   public void setSecondaryParentOpened(boolean secondaryParentOpened) {
      this.secondaryParentOpened = secondaryParentOpened;
   }
}
