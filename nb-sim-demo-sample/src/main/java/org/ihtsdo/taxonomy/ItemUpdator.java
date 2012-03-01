
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy;

//~--- non-JDK imports --------------------------------------------------------


import gov.va.demo.taxonomy.TaxonomyTreeCell;
import org.ihtsdo.taxonomy.model.TaxonomyModel;
import org.ihtsdo.taxonomy.items.Item;
import org.ihtsdo.tk.api.*;
import org.ihtsdo.tk.api.ContradictionException;
import org.ihtsdo.tk.api.concept.ConceptVersionBI;
import org.ihtsdo.tk.api.coordinate.ViewCoordinate;
import org.ihtsdo.tk.api.relationship.RelationshipVersionBI;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingWorker;
import org.ihtsdo.taxonomy.items.TaxonomyItemSetup;
import org.openide.util.Exceptions;

/**
 *
 * @author kec
 */
public class ItemUpdator extends SwingWorker<Object, PublishRecord> implements ProcessUnfetchedConceptDataBI {
   private ConcurrentHashMap<Integer, ConcurrentSkipListSet<UpdateNodeBI>> nodesToChange =
      new ConcurrentHashMap<Integer, ConcurrentSkipListSet<UpdateNodeBI>>();
   private final HashSet<Integer>     changedConcepts                   = new HashSet<Integer>();
   private final HashSet<Integer>     referencedConceptsOfChangedRefexs = new HashSet<Integer>();
   private AtomicInteger              updateNodeId                      = new AtomicInteger();
   private final NidBitSetBI        conceptsToRetrieve;
   private CountDownLatch             latch;
   private final TaxonomyModel        model;
   private final HashSet<Integer>     noTaxonomyChange;
   private final TaxonomyItemSetup  renderer;
   private long                       sequence;
   private final TerminologySnapshotDI   ts;
   private final ViewCoordinate       vc;

   //~--- constructors --------------------------------------------------------

   public ItemUpdator(TaxonomyModel model, long sequence, Set<Integer> originsOfChangedRels,
                      Set<Integer> destinationsOfChangedRels,
                      Set<Integer> referencedComponentsOfChangedRefexs, Set<Integer> changedComponents,
                      TaxonomyItemSetup renderer)
           throws IOException {
      this.ts                 = model.getTs();
      this.model              = model;
      this.sequence           = sequence;
      this.conceptsToRetrieve = ts.getEmptyNidSet();
      this.renderer           = renderer;
      this.vc                 = renderer.getViewCoordinate();

      for (Integer nid : changedComponents) {
         int cNid = ts.getConceptNidForNid(nid);

         this.changedConcepts.add(cNid);
      }

      for (Integer nid : referencedComponentsOfChangedRefexs) {
         int cNid = ts.getConceptNidForNid(nid);

         this.referencedConceptsOfChangedRefexs.add(cNid);
      }

      noTaxonomyChange = new HashSet(changedConcepts);
      noTaxonomyChange.addAll(referencedConceptsOfChangedRefexs);
      noTaxonomyChange.removeAll(destinationsOfChangedRels);
      noTaxonomyChange.removeAll(originsOfChangedRels);

      //
      for (Integer cNid : originsOfChangedRels) {
         this.conceptsToRetrieve.setMember(cNid);
      }

      for (Integer cNid : destinationsOfChangedRels) {
         this.conceptsToRetrieve.setMember(cNid);
      }

      HashSet<Integer> originsAndDestinationsChanged = new HashSet<Integer>(originsOfChangedRels);

      originsAndDestinationsChanged.retainAll(destinationsOfChangedRels);
      originsOfChangedRels.removeAll(originsAndDestinationsChanged);
      destinationsOfChangedRels.removeAll(originsAndDestinationsChanged);

      for (int cNid : noTaxonomyChange) {
         Collection<Long> nodeIds = model.getNodeStore().getNodeIdsForConcept(cNid);

         if (nodeIds != null) {
            for (Long nodeId : nodeIds) {
               Item currentNode = model.getNodeStore().nodeMap.get(nodeId);

               if (currentNode != null) {
                  if (!nodesToChange.containsKey(cNid)) {
                     nodesToChange.put(cNid, new ConcurrentSkipListSet<UpdateNodeBI>());
                  }

                  this.conceptsToRetrieve.setMember(cNid);
                  nodesToChange.get(cNid).add(new NoTaxonomyChangeNodeUpdate(currentNode));
               }
            }
         }
      }

      for (int cNid : originsOfChangedRels) {
         Collection<Long> nodeIds = model.getNodeStore().getNodeIdsForConcept(cNid);

         if (nodeIds != null) {
            for (Long nodeId : nodeIds) {
               Item currentNode = model.getNodeStore().nodeMap.get(nodeId);

               if (currentNode != null) {
                  ParentChangeNodeUpdate pcnu = new ParentChangeNodeUpdate(currentNode);

                  this.conceptsToRetrieve.setMember(cNid);

                  if (!nodesToChange.containsKey(cNid)) {
                     nodesToChange.put(cNid, new ConcurrentSkipListSet<UpdateNodeBI>());
                  }

                  nodesToChange.get(cNid).add(pcnu);
               }
            }
         }
      }

      for (int cNid : destinationsOfChangedRels) {
         Collection<Long> nodeIds = model.getNodeStore().getNodeIdsForConcept(cNid);

         if (nodeIds != null) {
            for (Long nodeId : nodeIds) {
               Item currentNode = model.getNodeStore().nodeMap.get(nodeId);

               if (currentNode != null) {
                  this.conceptsToRetrieve.setMember(cNid);

                  int[]                          possibleChildren     = ts.getPossibleChildren(cNid);
                  ConcurrentSkipListSet<Integer> possibleChildrenCSLS = new ConcurrentSkipListSet<Integer>();

                  for (int pcNid : possibleChildren) {
                     possibleChildrenCSLS.add(pcNid);
                  }

                  ChildChangeNodeUpdate ccnu = new ChildChangeNodeUpdate(currentNode, possibleChildrenCSLS);

                  if (!nodesToChange.containsKey(cNid)) {
                     nodesToChange.put(cNid, new ConcurrentSkipListSet<UpdateNodeBI>());
                  }

                  nodesToChange.get(cNid).add(ccnu);

                  for (int pcNid : possibleChildren) {
                     if (!nodesToChange.containsKey(pcNid)) {
                        nodesToChange.put(pcNid, new ConcurrentSkipListSet<UpdateNodeBI>());
                     }

                     this.conceptsToRetrieve.setMember(pcNid);
                     nodesToChange.get(pcNid).add(ccnu);
                  }
               }
            }
         }
      }

      for (int cNid : originsAndDestinationsChanged) {
         Collection<Long> nodeIds = model.getNodeStore().getNodeIdsForConcept(cNid);

         if (nodeIds != null) {
            for (Long nodeId : nodeIds) {
               Item currentNode = model.getNodeStore().nodeMap.get(nodeId);

               if (currentNode != null) {
                  this.conceptsToRetrieve.setMember(cNid);

                  int[]                          possibleChildren     = ts.getPossibleChildren(cNid);
                  ConcurrentSkipListSet<Integer> possibleChildrenCSLS = new ConcurrentSkipListSet<Integer>();

                  for (int pcNid : possibleChildren) {
                     possibleChildrenCSLS.add(pcNid);
                  }

                  ParentAndChildChangeNodeUpdate paccnu = new ParentAndChildChangeNodeUpdate(currentNode,
                                                             possibleChildrenCSLS);

                  if (!nodesToChange.containsKey(cNid)) {
                     nodesToChange.put(cNid, new ConcurrentSkipListSet<UpdateNodeBI>());
                  }

                  nodesToChange.get(cNid).add(paccnu);

                  for (int pcNid : possibleChildren) {
                     if (!nodesToChange.containsKey(pcNid)) {
                        nodesToChange.put(pcNid, new ConcurrentSkipListSet<UpdateNodeBI>());
                     }

                     nodesToChange.get(cNid).add(paccnu);
                  }
               }
            }
         }
      }

      latch = new CountDownLatch(conceptsToRetrieve.cardinality());
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public boolean continueWork() {
      return true;
   }

   @Override
   protected Object doInBackground() throws Exception {
      if (conceptsToRetrieve.cardinality() > 100) {
         ts.iterateConceptDataInParallel(this);
      } else {
         NidBitSetItrBI          cnidItr = conceptsToRetrieve.iterator();

         while (cnidItr.next()) {
            processConcept(ts.getConceptForNid(cnidItr.nid()));
         }
      }

      latch.await();

      return true;
   }

   @Override
   protected void done() {
      try {
         get();
      } catch (Exception ex) {
         Exceptions.printStackTrace(ex);
      }
   }

   private void processConcept(ConceptVersionBI concept) throws Exception {
      int cNid = concept.getNid();

      if (nodesToChange.containsKey(cNid)) {
         for (UpdateNodeBI un : nodesToChange.get(cNid)) {
            un.update(concept);
         }
      }

      latch.countDown();
   }

   @Override
   public void processUnfetchedConceptData(int cNid, ConceptFetcherBI fetcher) throws Exception {
      if (conceptsToRetrieve.isMember(cNid)) {
         processConcept(fetcher.fetch(vc));
      }
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public NidBitSetBI getNidSet() throws IOException {
      return conceptsToRetrieve;
   }

   //~--- inner interfaces ----------------------------------------------------

   private static interface UpdateNodeBI {
      public void update(ConceptVersionBI cv);
   }


   //~--- inner classes -------------------------------------------------------

   private class ChildChangeNodeUpdate extends UpdateNode implements UpdateNodeBI {
      Item                        newNode  = null;
      ConcurrentSkipListSet<Item> children = new ConcurrentSkipListSet<Item>();
      Item                        currentNode;
      ConcurrentSkipListSet<Integer>      possibleChildren;

      //~--- constructors -----------------------------------------------------

      public ChildChangeNodeUpdate(Item currentNode,
                                   ConcurrentSkipListSet<Integer> possibleChildren) {
         this.currentNode      = currentNode;
         this.possibleChildren = possibleChildren;
      }

      //~--- methods ----------------------------------------------------------

      @Override
      public void update(ConceptVersionBI cv) {
         try {
            if (cv.getNid() == currentNode.getCnid()) {
               newNode = model.getNodeFactory().makeNode(cv, currentNode.getParentNid(),
                       model.getNodeStore().get(currentNode.parentNodeId));
            } else {
               for (RelationshipVersionBI rel : cv.getRelsOutgoingActiveIsa()) {
                  if (rel.getDestinationNid() == currentNode.getCnid()) {
                     Item childNode = model.getNodeFactory().makeNode(cv, currentNode.getCnid(),
                                                 currentNode);

                     children.add(childNode);

                     break;
                  }
               }

               possibleChildren.remove(cv.getNid());
            }

            if (possibleChildren.isEmpty() && (newNode != null)) {
               for (Item child : children) {
                  newNode.addChild(child);
               }

               PublishRecord pr = new PublishRecord(newNode, PublishRecord.UpdateType.CHILD_CHANGE);

               publish(pr);
            }
         } catch (ContradictionException ex) {
            Exceptions.printStackTrace(ex);
         } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
         }
      }
   }


   private class NoTaxonomyChangeNodeUpdate extends UpdateNode implements UpdateNodeBI {
      Item currentNode;

      //~--- constructors -----------------------------------------------------

      public NoTaxonomyChangeNodeUpdate(Item currentNode) {
         this.currentNode = currentNode;
      }

      //~--- methods ----------------------------------------------------------

      @Override
      public void update(ConceptVersionBI cv) {
         try {
            renderer.setupTaxonomyNode(currentNode, cv);

            PublishRecord pr = new PublishRecord(currentNode, PublishRecord.UpdateType.NO_TAXONOMY_CHANGE);

            publish(pr);
         } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
         }
      }
   }


   private class ParentAndChildChangeNodeUpdate extends UpdateNode implements UpdateNodeBI {
     Item                        newNode  = null;
      ConcurrentSkipListSet<Item> children = new ConcurrentSkipListSet<Item>();
      Item                        currentNode;
      ConcurrentSkipListSet<Integer>      possibleChildren;

      //~--- constructors -----------------------------------------------------

      public ParentAndChildChangeNodeUpdate(Item currentNode,
              ConcurrentSkipListSet<Integer> possibleChildren) {
         this.currentNode      = currentNode;
         this.possibleChildren = possibleChildren;
      }

      //~--- methods ----------------------------------------------------------

      @Override
      public void update(ConceptVersionBI cv) {
         try {
            if (cv.getNid() == currentNode.getCnid()) {
               newNode = model.getNodeFactory().makeNode(cv, currentNode.getParentNid(),
                       model.getNodeStore().get(currentNode.parentNodeId));
            } else {
               for (RelationshipVersionBI rel : cv.getRelsOutgoingActiveIsa()) {
                  if (rel.getDestinationNid() == currentNode.getCnid()) {
                     Item childNode = model.getNodeFactory().makeNode(cv, currentNode.getCnid(),
                                                 currentNode);

                     currentNode.addChild(childNode);

                     break;
                  }
               }

               possibleChildren.remove(cv.getNid());
            }

            if (possibleChildren.isEmpty() && (newNode != null)) {
               for (Item child : children) {
                  newNode.addChild(child);
               }

               PublishRecord pr = new PublishRecord(newNode, PublishRecord.UpdateType.EXTRA_PARENT_AND_CHILD_CHANGE);

               publish(pr);
            }
         } catch (ContradictionException ex) {
            Exceptions.printStackTrace(ex);
         } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
         }
      }
   }


   private class ParentChangeNodeUpdate extends UpdateNode implements UpdateNodeBI {
      Item currentNode;

      //~--- constructors -----------------------------------------------------

      public ParentChangeNodeUpdate(Item currentNode) {
         this.currentNode = currentNode;
      }

      //~--- methods ----------------------------------------------------------

      @Override
      public void update(ConceptVersionBI cv) {
         try {
            Item newNode = model.getNodeFactory().makeNode(cv, currentNode.getParentNid(),
                                      model.getNodeStore().get(currentNode.parentNodeId));

            renderer.setupTaxonomyNode(newNode, cv);

            PublishRecord pr = new PublishRecord(newNode, PublishRecord.UpdateType.EXTRA_PARENT_CHANGE);

            publish(pr);
         } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
         }
      }
   }


   private abstract class UpdateNode implements Comparable<UpdateNode> {
      private int updateNodeId = ItemUpdator.this.updateNodeId.getAndIncrement();

      //~--- methods ----------------------------------------------------------

      @Override
      public int compareTo(UpdateNode o) {
         return this.updateNodeId - o.updateNodeId;
      }
   }
}
