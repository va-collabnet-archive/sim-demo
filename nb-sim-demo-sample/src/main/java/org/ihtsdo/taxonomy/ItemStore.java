
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.taxonomy.items.Item;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author kec
 */
public class ItemStore {
   private static Collection<Long> empty = Collections.unmodifiableCollection(new ArrayList<Long>());

   //~--- fields --------------------------------------------------------------

   protected ConcurrentHashMap<Long, Item>        nodeMap        = new ConcurrentHashMap<Long, Item>();
   protected ConcurrentHashMap<Integer, Collection<Long>> nidToNodeIdMap = new ConcurrentHashMap<Integer,
                                                                              Collection<Long>>();

   //~--- methods -------------------------------------------------------------

   public void add(Item node) {
      nodeMap.put(node.getNodeId(), node);

      CopyOnWriteArraySet<Long> nodeIdSet = new CopyOnWriteArraySet<Long>();

      nodeIdSet.add(node.nodeId);

      Collection<Long> oldSet = nidToNodeIdMap.putIfAbsent(node.getCnid(), nodeIdSet);

      if (oldSet != null) {
         oldSet.add(node.nodeId);
      }
   }

   public boolean containsKey(Long nodeId) {
      return nodeMap.containsKey(nodeId);
   }

   public void remove(Long nodeId) {
      Item node = nodeMap.get(nodeId);

      if (node != null) {
         Collection<Long> nodeIds = nidToNodeIdMap.get(node.getCnid());

         if (nodeIds != null) {
            nodeIds.remove(nodeId);

            if (nodeIds.isEmpty()) {
               nidToNodeIdMap.remove(node.getCnid());
            }
         }
      }
   }

   //~--- get methods ---------------------------------------------------------

   public Item get(Long nodeId) {
      return nodeMap.get(nodeId);
   }

   public Collection<Long> getNodeIdsForConcept(int cnid) {
      Collection<Long> nodeIds = nidToNodeIdMap.get(cnid);

      if (nodeIds != null) {
         return nodeIds;
      }

      return empty;
   }
}
