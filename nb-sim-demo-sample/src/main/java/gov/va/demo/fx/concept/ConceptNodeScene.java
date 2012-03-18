/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.fx.concept;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import org.ihtsdo.taxonomy.items.Item;
import org.ihtsdo.taxonomy.items.TaxonomyItemWrapper;
import org.ihtsdo.taxonomy.items.TaxonomyRootItemWrapper;
import org.ihtsdo.tk.api.ContradictionException;
import org.ihtsdo.tk.api.TerminologySnapshotDI;
import org.ihtsdo.tk.api.concept.ConceptVersionBI;
import org.ihtsdo.tk.api.description.DescriptionVersionBI;
import org.ihtsdo.tk.api.relationship.RelationshipVersionBI;
import org.openide.util.Exceptions;

/**
 *
 * @author kec
 */
public class ConceptNodeScene extends Parent implements ChangeListener<TaxonomyItemWrapper> {
    ConceptNode conceptNode;
    Parent root;
    TreeView taxonomyTree;
    
    public ConceptNodeScene(TreeView taxonomyTree) {
        this.taxonomyTree = taxonomyTree;
        this.taxonomyTree.getSelectionModel().selectedItemProperty().addListener(this);
        URL resource = getClass().getResource("/gov/va/demo/fx/concept/ConceptNode.fxml");
        FXMLLoader loader = new FXMLLoader(resource);
        try {
            root = (Parent) loader.load();
            conceptNode = (ConceptNode) loader.getController();
            getChildren().add(root);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void changed(ObservableValue<? extends TaxonomyItemWrapper> ov, TaxonomyItemWrapper t, TaxonomyItemWrapper t1) {
        if (t1 != null) {
            try {
                Item taxonomyItem = t1.getValue();
                TaxonomyRootItemWrapper rootItemWrapper = (TaxonomyRootItemWrapper) taxonomyTree.getRoot();
                TerminologySnapshotDI ts = rootItemWrapper.getModel().getTs();
                ConceptVersionBI cv = ts.getConceptVersion(taxonomyItem.getCnid());
                conceptNode.getAttributesPane().setText(cv.getPreferredDescription().getText());
                VBox descVBox = new VBox(6);
                List<Node> descLabels = new ArrayList<Node>();
                for (DescriptionVersionBI dv: cv.getDescsActive()) {
                    ConceptVersionBI descType = ts.getConceptVersion(dv.getTypeNid());
                    Label descLabel = new Label(dv.getText() + " " + 
                            descType.getPreferredDescription().getText() + " " + dv.getLang());
                    descLabel.setWrapText(true);
                    descLabels.add(descLabel);
                }
                descVBox.getChildren().setAll(descLabels);
                conceptNode.getDescriptionsPane().setContent(descVBox);
                
                
                VBox relVBox = new VBox(4);
                GridPane definitionPane = new GridPane();
                List<Node> relLabels = new ArrayList<Node>();
                
                int row = 0;
                
                for (RelationshipVersionBI rv: cv.getRelsOutgoingActive()) {
                    ConceptVersionBI relType = ts.getConceptVersion(rv.getTypeNid());
                    ConceptVersionBI relTarget = ts.getConceptVersion(rv.getDestinationNid());
                    Label relLabel = new Label(relType.getPreferredDescription().getText() + " " + 
                            relTarget.getPreferredDescription().getText());
                    relLabel.setWrapText(true);
                    relLabels.add(relLabel);
                    GridPane relGrid = new GridPane();
                    definitionPane.add(relGrid, 0, row);
                    // arrow b4 group
                    Polyline arrowB4Group = new Polyline(0,8,8,8);
                    arrowB4Group.minWidth(16);
                    arrowB4Group.minHeight(16);
                    relGrid.add(arrowB4Group, 0, 0);
                    // group
                    Circle circle1 = new Circle(11, Color.RED);
                    relGrid.add(circle1, 1, 0);
                    // arrow b4 type
                    Polyline arrowB4Type = new Polyline(0,8,8,8);
                    arrowB4Type.minWidth(16);
                    arrowB4Type.minHeight(16);
                    relGrid.add(arrowB4Type, 2, 0);
                    // type
                    Rectangle type = new Rectangle(60,21);
                    type.setArcWidth(20);
                    type.setArcHeight(20);
                    relGrid.add(type, 3, 0);
                    // arrow b4 destination
                    Polyline arrowB4Dest = new Polyline(0,8,8,8);
                    arrowB4Dest.minWidth(16);
                    arrowB4Dest.minHeight(16);
                    relGrid.add(arrowB4Dest, 4, 0);
                    // destination
                    Rectangle destination = new Rectangle(60,21);
                    relGrid.add(destination, 5, 0);
                    row++;
                }
                relLabels.add(definitionPane);
                relVBox.getChildren().setAll(relLabels);
                conceptNode.getDefinitionPane().setContent(relVBox);
            } catch (ContradictionException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
}
