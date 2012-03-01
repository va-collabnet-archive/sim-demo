
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy.path;

//~--- non-JDK imports --------------------------------------------------------

import gov.va.demo.taxonomy.FutureHelper;

import org.ihtsdo.taxonomy.model.ItemFactory;
import org.ihtsdo.taxonomy.model.NodePath;
import org.ihtsdo.taxonomy.model.TaxonomyModel;
import org.ihtsdo.taxonomy.items.Item;
import org.ihtsdo.tk.api.concept.ConceptChronicleBI;
import org.ihtsdo.tk.api.concept.ConceptVersionBI;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.Collection;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author kec
 */
public class PathExpander /* implements Runnable */ {
//   ConceptVersionBI focus;
//   TaxonomyModel    model;
//   TaxonomyTree     tree;
//
//   //~--- constructors --------------------------------------------------------
//
//   public PathExpander(TaxonomyTree tree, ConceptChronicleBI concept)
//           throws IOException {
//      this.tree   = tree;
//      this.model  = (TaxonomyModel) tree.getModel();
//      this.focus  = model.getTs().getConceptVersion(concept.getNid());
//   }
//
//   //~--- methods -------------------------------------------------------------
//
//   @Override
//   public void run() {
//      try {
//         Collection<List<Integer>> nidPaths     = focus.getNidPathsToRoot();
//         List<Integer>             shortestPath = null;
//
//         for (List<Integer> nidPath : nidPaths) {
//            if (shortestPath == null) {
//               shortestPath = nidPath;
//            } else {
//               if (shortestPath.size() > nidPath.size()) {
//                  shortestPath = nidPath;
//               }
//            }
//         }
//
//         Item parent = model.getRoot();
//
//         for (int i = shortestPath.size() - 1; i > -1; i--) {
//            Item node = model.getNodeFactory().makeNode(shortestPath.get(i), parent);
//
//            parent = node;
//         }
//
//         Item        focusNode = model.getNodeFactory().makeNode(focus.getNid(), parent);
//         PathSegmentExpander expander  = new PathSegmentExpander(tree.getNodeFactory(),
//                                            NodePath.getTreePath(model, focusNode), 1);
//
//         FutureHelper.addFuture(ItemFactory.pathExpanderExecutors.submit(expander));
//      } catch (Exception ex) {
//         Exceptions.printStackTrace(ex);
//      }
//   }
}
