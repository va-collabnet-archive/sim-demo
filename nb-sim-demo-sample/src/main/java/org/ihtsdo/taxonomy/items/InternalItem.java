
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy.items;

//~--- JDK imports ------------------------------------------------------------

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * @author kec
 */
public class InternalItem extends Item {
   public ConcurrentSkipListMap<Integer, Long> nidNodeMap     = new ConcurrentSkipListMap<Integer, Long>();
   boolean                                     childrenAreSet = false;
   private ConcurrentSkipListSet<Long>         childrenNodeIds;

   //~--- constructors --------------------------------------------------------

   public InternalItem(int cnid, int parentNid, long parentNodeId, Comparator comparator) {
      super(cnid, parentNid, parentNodeId);
      childrenNodeIds = new ConcurrentSkipListSet<Long>(comparator);
   }


   public InternalItem(InternalItemMultiParent another) {
      super(another);
      childrenNodeIds = another.getChildrenNodeIds();
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public boolean addChild(Item child) {
      Integer cnid = child.getCnid();

      if (!this.nidNodeMap.containsKey(cnid)) {
         this.childrenNodeIds.add(child.nodeId);
         this.nidNodeMap.put(cnid, child.nodeId);

         return true;
      }

      return false;
   }

   @Override
   public void addExtraParent(Item extraParent) {
      throw new UnsupportedOperationException("Node can't have extra parents");
   }

   @Override
   public boolean childrenAreSet() {
      return childrenAreSet;
   }

   public void clearChildren() {
      nidNodeMap.clear();
      childrenNodeIds.clear();
      childrenAreSet = false;
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public ConcurrentSkipListSet<Long> getChildrenNodeIds() {
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

   //~--- set methods ---------------------------------------------------------

   public void setChildrenAreSet(boolean childrenAreSet) {
      this.childrenAreSet = childrenAreSet;
   }

   @Override
   public void setSecondaryParentOpened(boolean secondaryParentOpened) {

      //
   }
}
