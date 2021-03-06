
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy.items;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.tk.hash.Hashcode;

/**
 *
 * @author kec
 */
public class SecondaryParentItemRoot extends LeafItem {
   int parentDepth;
   int parentNid;

   //~--- constructors --------------------------------------------------------

   public SecondaryParentItemRoot(int cnid, int parentNid, long parentNodeId, long[] lineage) {
      super(cnid, Hashcode.computeLong(lineage), parentNodeId);
      this.parentNid = parentNid;
      lineage[lineage.length - 2] = nodeId;
      setNodesToCompare(lineage);
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public int getParentDepth() {
      return parentDepth;
   }

   @Override
   public int getParentNid() {
      return parentNid;
   }

   @Override
   public boolean isSecondaryParentNode() {
      return true;
   }

   //~--- set methods ---------------------------------------------------------

   @Override
   public void setParentDepth(int parentDepth) {
      this.parentDepth = parentDepth;
   }
   
       @Override
    public void setSecondaryParentOpened(boolean secondaryParentOpened) {
        // nothing to open;
    }

}
