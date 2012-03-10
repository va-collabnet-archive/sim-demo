
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy.model;

//~--- non-JDK imports --------------------------------------------------------

import gov.va.demo.taxonomy.FutureHelper;
import gov.va.demo.terminology.TerminologyService;

import javafx.concurrent.Task;

import org.ihtsdo.helper.thread.NamedThreadFactory;
import org.ihtsdo.taxonomy.items.*;
import org.ihtsdo.taxonomy.items.InternalItem;
import org.ihtsdo.taxonomy.items.InternalItemMultiParent;
import org.ihtsdo.taxonomy.items.Item;
import org.ihtsdo.taxonomy.items.ItemIdComparator;
import org.ihtsdo.taxonomy.items.LeafItem;
import org.ihtsdo.taxonomy.items.LeafItemMultiParent;
import org.ihtsdo.tk.api.*;
import org.ihtsdo.tk.api.ConceptFetcherBI;
import org.ihtsdo.tk.api.ContradictionException;
import org.ihtsdo.tk.api.NidBitSetBI;
import org.ihtsdo.tk.api.ProcessUnfetchedConceptDataBI;
import org.ihtsdo.tk.api.concept.ConceptVersionBI;
import org.ihtsdo.tk.api.relationship.RelationshipVersionBI;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kec
 */
public class ItemFactory {
   private static final ThreadGroup nodeFactoryThreadGroup = new ThreadGroup("ItemFactory ");
   public static ExecutorService    taxonomyExecutors      = Executors.newFixedThreadPool(Math.max(4,
                                                        Runtime.getRuntime().availableProcessors()
                                                        + 1), new NamedThreadFactory(nodeFactoryThreadGroup,
                                                           "Taxonomy "));
   public static ExecutorService pathExpanderExecutors =
      Executors.newFixedThreadPool(Math.max(4, Runtime.getRuntime().availableProcessors() + 1),
                                   new NamedThreadFactory(nodeFactoryThreadGroup, "PathExpander "));
   public static ExecutorService childFinderExecutors =
      Executors.newFixedThreadPool(Math.max(4, Runtime.getRuntime().availableProcessors() + 1),
                                   new NamedThreadFactory(nodeFactoryThreadGroup, "ChildFinder "));

   //~--- fields --------------------------------------------------------------

   private ConcurrentSkipListMap<Long, MakeChildNodesTask> childWorkerMap = new ConcurrentSkipListMap<Long,
                                                                               MakeChildNodesTask>();
   private ChildItemFilterBI    childNodeFilter;
   private final ItemComparator itemComparator;
   private ItemIdComparator     itemIdComparator;
   private TaxonomyModel        model;
   private TaxonomyItemSetup    renderer;

   //~--- constructors --------------------------------------------------------

   public ItemFactory(TaxonomyModel model, TaxonomyItemSetup renderer, ChildItemFilterBI childNodeFilter) {
      this.model            = model;
      this.renderer         = renderer;
      this.childNodeFilter  = childNodeFilter;
      this.itemIdComparator = new ItemIdComparator(model.nodeStore);
      this.itemComparator   = new ItemComparator();
   }

   //~--- methods -------------------------------------------------------------

   public static void close() {
      try {
         pathExpanderExecutors.shutdown();
         pathExpanderExecutors.awaitTermination(2, TimeUnit.MINUTES);
      } catch (InterruptedException ex) {
         Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
      }

      try {
         taxonomyExecutors.shutdown();
         taxonomyExecutors.awaitTermination(2, TimeUnit.MINUTES);
      } catch (InterruptedException ex) {
         Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
      }

      try {
         childFinderExecutors.shutdown();
         childFinderExecutors.awaitTermination(2, TimeUnit.MINUTES);
      } catch (InterruptedException ex) {
         Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public void collapseNode(Item node) {
      CollapseHandler collapser = new CollapseHandler(node);
      FutureHelper.addFuture(childFinderExecutors.submit(collapser));
   }

       private class CollapseHandler implements Callable<Void> {
        
        Item collapsingNode;

        //~--- constructors -----------------------------------------------------
        public CollapseHandler(Item collapsingNode) {
            this.collapsingNode = collapsingNode;
        }

        //~--- methods ----------------------------------------------------------
 
        @Override
        public Void call() throws Exception {
            Item latestCollapsingNode = model.nodeStore.get(collapsingNode.nodeId);
            
            if (latestCollapsingNode == null) {
                latestCollapsingNode = collapsingNode;
            }
            
            for (Long nodeId : latestCollapsingNode.getChildrenNodeIds()) {
                Item childNode = model.nodeStore.get(nodeId);
                
                if ((childNode != null) && !childNode.getChildrenNodeIds().isEmpty()) {
                    removeDescendents(childNode);
                }
            }
            collapsingNode.clearChildren();
            return null;
        }
    }
   public void makeChildNodes(TaxonomyItemWrapper parentItemWrapper) {
      Item parentNode = parentItemWrapper.getValue();

      if ((parentNode == null) || parentNode.isSecondaryParentNode()) {
         return;
      }

      MakeChildNodesTask mcnw = childWorkerMap.get(parentNode.nodeId);

      if (mcnw == null) {
         mcnw = new MakeChildNodesTask(parentItemWrapper, childNodeFilter);
         childWorkerMap.put(parentNode.nodeId, mcnw);
         FutureHelper.addFuture(taxonomyExecutors.submit(mcnw));
      }
   }

   public Item makeNode(int nid, Item parentNode) throws IOException, Exception {
      if (model.nodeStore.containsKey(TaxonomyModel.getNodeId(nid, parentNode.getCnid()))) {
         return model.nodeStore.get(TaxonomyModel.getNodeId(nid, parentNode.getCnid()));
      }

      ConceptVersionBI nodeConcept = TerminologyService.getSnapshot().getConceptVersion(nid);
      Item             node        = makeNodeFromScratch(nodeConcept, parentNode);

      parentNode.addChild(node);

      return node;
   }

   public Item makeNode(ConceptVersionBI nodeConcept, int parentCnid, Item parentNode)
           throws ContradictionException, IOException {
      if (TerminologyService.getSnapshot().getPossibleChildren(nodeConcept.getNid()).length == 0) {
         boolean multiParent = false;

         for (RelationshipVersionBI isaRel : nodeConcept.getRelsOutgoingActiveIsa()) {
            if ((isaRel == null) || (parentNode == null)) {
               continue;
            } else if (isaRel.getDestinationNid() != parentNode.getCnid()) {
               multiParent = true;

               break;
            }
         }

         if (multiParent) {
            LeafItemMultiParent node = new LeafItemMultiParent(nodeConcept.getNid(), parentCnid,
                                          parentNode.nodeId);

            renderer.setupTaxonomyNode(node, nodeConcept);
            model.nodeStore.add(node);

            return node;
         } else {
            LeafItem node = new LeafItem(nodeConcept.getNid(), parentCnid, parentNode.nodeId);

            renderer.setupTaxonomyNode(node, nodeConcept);
            model.nodeStore.add(node);

            return node;
         }
      }

      InternalItemMultiParent node = new InternalItemMultiParent(nodeConcept.getNid(), parentCnid,
                                        parentNode.nodeId, itemIdComparator);

      node.setIsLeaf(nodeConcept.isLeaf());
      setExtraParents(nodeConcept, node);
      renderer.setupTaxonomyNode(node, nodeConcept);
      model.nodeStore.add(node);

      // makeChildNodes(node);
      return node;
   }

   private Item makeNodeFromScratch(ConceptVersionBI nodeConcept, Item parentNode)
           throws Exception, IOException, ContradictionException {
      Long nodeId       = TaxonomyModel.getNodeId(nodeConcept.getNid(), parentNode.getCnid());
      Item existingNode = model.nodeStore.get(nodeId);

      if (existingNode != null) {
         return existingNode;
      }

      return makeNode(nodeConcept, parentNode.getCnid(), parentNode);
   }

   public void removeDescendents(Item parent) {
      if (!parent.isLeaf()) {
         ((InternalItem) parent).setChildrenAreSet(false);

         for (Long nodeId : parent.getChildrenNodeIds()) {
            MakeChildNodesTask worker = childWorkerMap.remove(nodeId);

            if (worker != null) {
               worker.cancel();
            }

            Item childNode = model.nodeStore.get(nodeId);

            if ((childNode != null) && (childNode.nodeId != parent.nodeId)) {
               removeDescendents(childNode);
               model.nodeStore.remove(nodeId);
            }
         }

         ((InternalItem) parent).clearChildren();
      }
   }

   //~--- get methods ---------------------------------------------------------

   public ItemIdComparator getNodeComparator() {
      return itemIdComparator;
   }

   //~--- set methods ---------------------------------------------------------

   private void setExtraParents(ConceptVersionBI nodeConcept, Item node)
           throws ContradictionException, IOException {
      if (node.getParentNid() != Integer.MAX_VALUE) {    // test if root
         for (ConceptVersionBI parent : nodeConcept.getRelsOutgoingDestinationsActiveIsa()) {
            if (parent.getNid() != node.getParentNid()) {
               node.setHasExtraParents(true);

               return;
            }
         }
      }
   }

   //~--- inner classes -------------------------------------------------------

   private class ChildFinder implements ProcessUnfetchedConceptDataBI, Callable<Object> {
      LinkedBlockingQueue<Item> childNodes = new LinkedBlockingQueue<Item>();
      ChildItemFilterBI         childFilter;
      NidBitSetBI               dataSet;
      ConceptVersionBI          parent;
      Item                      parentNode;
      MakeChildNodesTask                    task;

      //~--- constructors -----------------------------------------------------

      public ChildFinder(ConceptVersionBI parent, Item parentNode, MakeChildNodesTask task,
                         ChildItemFilterBI childFilter)
              throws IOException {
         this.task      = task;
         this.parent      = parent;
         this.parentNode  = parentNode;
         this.dataSet     = model.ts.getEmptyNidSet();
         this.childFilter = childFilter;

         for (int cnid : model.ts.getPossibleChildren(parentNode.getCnid())) {
            this.dataSet.setMember(cnid);
         }
      }

      //~--- methods ----------------------------------------------------------

      @Override
      public Object call() throws Exception {
         try {
            if (dataSet.cardinality() < 24) {
               NidBitSetItrBI itr = dataSet.iterator();

               while (itr.next()) {
                  processPossibleChild(TerminologyService.getSnapshot().getConceptVersion(itr.nid()));
               }
            } else {
               model.ts.iterateConceptDataInParallel(this);
            }

            return null;
         } finally {
            dataSet = null;
            childNodes.put(parentNode);
         }
      }

      @Override
      public boolean continueWork() {
         return task.continueWork();
      }

      private void processPossibleChild(ConceptVersionBI possibleChild) throws Exception {
         if (possibleChild.isChildOf(parent)) {
            if ((childFilter == null) || childFilter.pass(parent, possibleChild)) {
               Item childNode = makeNodeFromScratch(possibleChild, parentNode);

               if (parentNode.isLeaf()) {
                  Item oldParent = parentNode;

                  parentNode = new InternalItem(parentNode.getCnid(), parentNode.getParentNid(),
                                                parentNode.parentNodeId, itemIdComparator);
                  model.nodeStore.remove(oldParent.getNodeId());
                  model.nodeStore.add(parentNode);
                  renderer.setupTaxonomyNode(parentNode, parent);
               }

               if (parentNode.addChild(childNode) && (childNode != null)) {
                  childNodes.put(childNode);
               }
            }
         }
      }

      @Override
      public void processUnfetchedConceptData(int cNid, ConceptFetcherBI fetcher) throws Exception {
         if (dataSet.isMember(cNid)) {
            ConceptVersionBI possibleChild =
               fetcher.fetch(TerminologyService.getSnapshot().getViewCoordinate());

            processPossibleChild(possibleChild);
         }
      }

      //~--- get methods ------------------------------------------------------

      @Override
      public NidBitSetBI getNidSet() throws IOException {
         return dataSet;
      }
   }


   private class ItemComparator implements Comparator<TaxonomyItemWrapper> {
      @Override
      public int compare(TaxonomyItemWrapper o1, TaxonomyItemWrapper o2) {
         return itemIdComparator.compare(o1.getValue().nodeId, o2.getValue().nodeId);
      }
   }


   private class MakeChildNodesTask extends Task<Void> {
      Set<Item>           childNodes   = new HashSet<Item>();
      boolean             continueWork = true;
      ChildItemFilterBI   childFilter;
      TaxonomyItemWrapper parentItemWrapper;

      //~--- constructors -----------------------------------------------------

      public MakeChildNodesTask(TaxonomyItemWrapper parentNodeWrapper, ChildItemFilterBI childFilter) {
         this.parentItemWrapper = parentNodeWrapper;
         this.childFilter       = childFilter;
      }

      //~--- methods ----------------------------------------------------------

      @Override
      protected Void call() throws Exception {
         Item parentItem = parentItemWrapper.getValue();

         try {
            ConceptVersionBI parent =
               TerminologyService.getSnapshot().getConceptVersion(parentItem.getCnid());
            ChildFinder                    dataFinder     = new ChildFinder(parent, parentItemWrapper.getValue(), this, childFilter);
            Future<Object>                 finderFuture   = childFinderExecutors.submit(dataFinder);
            ArrayList<TaxonomyItemWrapper> nodesToPublish = new ArrayList<TaxonomyItemWrapper>();
            Item                           childNode      = dataFinder.childNodes.take();

            while (childNode.getNodeId() != parentItem.nodeId) {
               nodesToPublish.add(new TaxonomyItemWrapper(childNode, model));
               childNode = dataFinder.childNodes.take();
            }

            finderFuture.get();
            Collections.sort(nodesToPublish, itemComparator);
            parentItemWrapper.getChildren().addAll(nodesToPublish);

            if (parentItem instanceof InternalItem) {
               ((InternalItem) parentItem).setChildrenAreSet(true);
            }

            Item finalItem = parentItem.getFinalNode();

            parentItemWrapper.setValue(finalItem);
            model.nodeStore.add(finalItem);

            return null;
         } finally {
            childWorkerMap.remove(parentItem.nodeId, this);
         }
      }

      @Override
      protected void cancelled() {
         continueWork = false;
      }

      @Override
      protected void failed() {
         continueWork = false;
      }

      //~--- get methods ------------------------------------------------------

      public boolean continueWork() {
         return continueWork;
      }
   }
}
