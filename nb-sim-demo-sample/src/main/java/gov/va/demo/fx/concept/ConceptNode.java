
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.demo.fx.concept;

//~--- non-JDK imports --------------------------------------------------------

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

//~--- JDK imports ------------------------------------------------------------

import java.net.URL;

import java.util.ResourceBundle;

/**
 *
 * @author kec
 */
public class ConceptNode implements Initializable {
   @FXML
   private TitledPane attributesPane;
   @FXML
   private TitledPane definitionPane;
   @FXML
   private TitledPane descriptionsPane;

   //~--- methods -------------------------------------------------------------

   @Override
   public void initialize(URL url, ResourceBundle rb) {
      System.out.println("ConceptNode url: " + url);
      attributesPane.setText("This is a special concept...");
      definitionPane.setContent(new Label("definition"));
      descriptionsPane.setContent(new Label("descriptions"));
   }

   //~--- get methods ---------------------------------------------------------

   public TitledPane getAttributesPane() {
      return attributesPane;
   }

   public TitledPane getDefinitionPane() {
      return definitionPane;
   }

   public TitledPane getDescriptionsPane() {
      return descriptionsPane;
   }
}
