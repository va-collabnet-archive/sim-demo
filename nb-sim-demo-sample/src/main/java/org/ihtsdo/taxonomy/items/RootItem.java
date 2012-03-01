
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy.items;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.taxonomy.model.TaxonomyModel;

//~--- JDK imports ------------------------------------------------------------

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * @author kec
 */
public class RootItem extends Item {
   public ConcurrentSkipListSet<Long>          childrenNodeIds;
   public ConcurrentSkipListMap<Integer, Long> nidNodeIdMap = new ConcurrentSkipListMap<Integer, Long>();
   boolean                                     isLeaf       = false;

   //~--- constructors --------------------------------------------------------

   public RootItem(Comparator c) {
      super(Integer.MAX_VALUE, Integer.MAX_VALUE,
            TaxonomyModel.getNodeId(Integer.MIN_VALUE, Integer.MIN_VALUE));
      childrenNodeIds     = new ConcurrentSkipListSet<Long>(c);
   }

   //~--- methods -------------------------------------------------------------
   @Override
   public boolean addChild(Item child) {
      int cnid = child.getCnid();

      if (!this.nidNodeIdMap.containsKey(cnid)) {
         this.childrenNodeIds.add(child.nodeId);
         this.nidNodeIdMap.put(child.getCnid(), child.nodeId);

         return true;
      }

      return false;
   }

   @Override
   public void addExtraParent(Item extraParent) {
      throw new UnsupportedOperationException("Not supported.");
   }

   @Override
   public boolean childrenAreSet() {
      return true;
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public Collection<Long> getChildrenNodeIds() {
      return childrenNodeIds;
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
      return false;
   }

    @Override
    public boolean isSecondaryParentOpened() {
        return false;
    }

    @Override
    public void setSecondaryParentOpened(boolean secondaryParentOpened) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
