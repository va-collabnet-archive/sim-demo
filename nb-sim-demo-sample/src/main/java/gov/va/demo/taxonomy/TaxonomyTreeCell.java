
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.taxonomy;

//~--- non-JDK imports --------------------------------------------------------
import gov.va.demo.terminology.TerminologyService;
import java.io.IOException;
import javafx.event.EventHandler;

import javafx.geometry.Orientation;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import org.ihtsdo.taxonomy.items.Item;
import org.ihtsdo.taxonomy.items.TaxonomyItemWrapper;
import org.ihtsdo.taxonomy.model.TaxonomyModel;
import org.ihtsdo.tk.api.concept.ConceptVersionBI;
import org.openide.util.Exceptions;

/**
 *
 * @author kec
 */
public class TaxonomyTreeCell extends TreeCell<Item> {

    @Override
    protected void updateItem(Item t, boolean empty) {
        super.updateItem(t, empty);

        if (t != null) {
            final TaxonomyItemWrapper treeItem = (TaxonomyItemWrapper) getTreeItem();
            Item item = treeItem.getValue();

            if (treeItem.isExpanded()) {
                ImageView iv = Icons.TAXONOMY_CLOSE.getImageView();
                BorderPane borderPane = new BorderPane();
                borderPane.setCenter(iv);
                borderPane.setRight(new Label(" "));
                setDisclosureNode(borderPane);
            } else {
                ImageView iv = Icons.TAXONOMY_OPEN.getImageView();
                BorderPane borderPane = new BorderPane();
                borderPane.setCenter(iv);
                borderPane.setRight(new Label(" "));
                setDisclosureNode(borderPane);
            }

            setText(t.getText());

            FlowPane iconPane = new FlowPane(Orientation.HORIZONTAL, 2, 2);
            ImageView[] icons = new ImageView[0];

            if (t.getIcon() != null) {
                icons = new ImageView[]{t.getIcon().getImageView()};
                if (item.hasExtraParents()) {
                    icons[0].addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

                        @Override
                        public void handle(MouseEvent e) {
                            ProgressIndicator p1 = new ProgressIndicator();
                            p1.setPrefSize(16, 16);
                            setDisclosureNode(p1);
                            try {
                                openOrCloseParent(TaxonomyModel.singleton, treeItem);
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    });
                }
            }

            iconPane.getChildren().addAll(icons);

            double prefWrapLength = 0;

            for (ImageView icon : icons) {
                prefWrapLength = prefWrapLength + icon.getImage().getWidth();
                prefWrapLength = prefWrapLength + iconPane.getHgap();
            }

            iconPane.setPrefWrapLength(prefWrapLength);
            this.setGraphic(iconPane);
            this.setContentDisplay(ContentDisplay.LEFT);
        }
    }

    private void openOrCloseParent(TaxonomyModel model, TaxonomyItemWrapper nodeWrapper)
            throws IOException {
        Item node = nodeWrapper.getValue();
        boolean addNodes = !node.isSecondaryParentOpened();

        node.setSecondaryParentOpened(addNodes);

        ConceptVersionBI nodeConcept = TerminologyService.getStore().getConceptVersion(model.getTs().getViewCoordinate(), node.getCnid());

        model.getRenderer().setupTaxonomyNode(node, nodeConcept);
        nodeWrapper.setValue(null);
        nodeWrapper.setValue(node);

//      TaxonomyNode parentNode = model.getParent(node);
//
//      if ((parentNode != null)) {
//         if (addNodes) {
//            try {
//               addAllParentsAsExtra(nodeConcept, node);
//               parentNode.getChildren().addAll(node.getExtraParents());
//            } catch (Exception e) {
//               Exceptions.printStackTrace(e);
//            }
//         } else {    // remove nodes
//            removeAllExtraParents(model, node);
//            ace.getAceFrameConfig().getParentExpandedNodes().remove(node.getCnid());
//         }
//
//         model.treeStructureChanged(NodePath.getTreePath(model, parentNode));
//      }
    }
//   private void removeAllExtraParents(TaxonomyModel model, TaxonomyItemWrapper nodeWrapper) {
//      if (node != null) {
//         TaxonomyNode parentNode = model.getParent(node);
//
//         for (Long extraParentNodeId : node.getExtraParents()) {
//            removeAllExtraParents(model, model.getNodeStore().get(extraParentNodeId));
//            parentNode.getChildren().remove(extraParentNodeId);
//         }
//
//         model.treeStructureChanged(NodePath.getTreePath(model, node));
//      }
//   }
//   private void addAllParentsAsExtra(ConceptVersionBI nodeConcept, TaxonomyItemWrapper nodeWrapper)
//           throws ContradictionException, IOException {
//      node.getExtraParents().clear();
//      if (node.getParentNid() != Integer.MAX_VALUE) {    // test if root
//         for (ConceptVersionBI parent : nodeConcept.getRelsOutgoingDestinationsActiveIsa()) {
//            if (parent.getNid() != node.getParentNid()) {
//               TaxonomyNode extraParentNode = null;
//               long[]       nodesToCompare  = new long[node.getNodesToCompare().length + 1];
//
//               System.arraycopy(node.getNodesToCompare(), 0, nodesToCompare, 0,
//                                node.getNodesToCompare().length);
//               nodesToCompare[node.getNodesToCompare().length] = Long.MAX_VALUE;
//
//               if (parent.getRelsOutgoingActiveIsa().isEmpty()) {
//                  extraParentNode = new SecondaryParentNodeRoot(parent.getNid(), nodeConcept.getNid(),
//                          node.parentNodeId, nodesToCompare);
//               } else {
//                  extraParentNode = new SecondaryParentNode(parent.getNid(), nodeConcept.getNid(),
//                          node.parentNodeId, nodesToCompare);
//               }
//
//               helper.getNodeStore().add(extraParentNode);
//               extraParentNode.setParentDepth(node.getParentDepth() + 1);
//               helper.getRenderer().setupTaxonomyNode(extraParentNode, parent);
//               node.addExtraParent(extraParentNode);
//            }
//         }
//      }
//   }
}
