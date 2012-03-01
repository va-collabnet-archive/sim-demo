
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.taxonomy.items.Item;

/**
 *
 * @author kec
 */
public class PublishRecord {
   Item publishedNode;
   UpdateType   updateType;

   //~--- constant enums ------------------------------------------------------

   public enum UpdateType { NO_TAXONOMY_CHANGE, CHILD_CHANGE, EXTRA_PARENT_CHANGE,
                            EXTRA_PARENT_AND_CHILD_CHANGE }

   ;

   //~--- constructors --------------------------------------------------------

   public PublishRecord(Item publishedNode, UpdateType updateType) {
      this.publishedNode = publishedNode;
      this.updateType    = updateType;
   }

   //~--- get methods ---------------------------------------------------------

   public Item getPublishedNode() {
      return publishedNode;
   }

   public UpdateType getUpdateType() {
      return updateType;
   }
}
