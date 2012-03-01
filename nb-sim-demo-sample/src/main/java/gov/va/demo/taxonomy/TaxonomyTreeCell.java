
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.demo.taxonomy;

//~--- non-JDK imports --------------------------------------------------------


import javafx.geometry.Orientation;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import org.ihtsdo.taxonomy.items.Item;

/**
 *
 * @author kec
 */
public class TaxonomyTreeCell extends TreeCell<Item> {
   @Override
   protected void updateItem(Item t, boolean empty) {
      super.updateItem(t, empty);

      if (t != null) {
         setText(t.getText());

         FlowPane    iconPane = new FlowPane(Orientation.HORIZONTAL, 2, 2);
         ImageView[] icons    = new ImageView[0];
         if (t.getIcon() != null) {
             icons    = new ImageView[] { t.getIcon().getImageView() };
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
}
