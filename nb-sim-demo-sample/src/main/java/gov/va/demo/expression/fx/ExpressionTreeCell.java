/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.expression.fx;

import gov.va.sim.act.expression.ExpressionComponentBI;
import java.io.IOException;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import org.openide.util.Exceptions;

/**
 *
 * @author kec
 */
public class ExpressionTreeCell extends TreeCell<ExpressionComponentBI> {

    private boolean preferred = true;

    @Override
    protected void updateItem(ExpressionComponentBI t, boolean bln) {
        super.updateItem(t, bln);
        if (t != null) {
            try {
                //navigation-090-button-white.png
                Image navPrimImage = new Image(getClass().getResourceAsStream("/fugue/16x16/icons/navigation-090-button-white.png"));
                ImageView navPrimImageView = new ImageView();
                navPrimImageView.setImage(navPrimImage);

                Image navDefinedImage = new Image(getClass().getResourceAsStream("/fugue/16x16/icons/navigation-090.png"));
                ImageView navDefinedImageView = new ImageView();
                navDefinedImageView.setImage(navDefinedImage);

                Image acornImage = new Image(getClass().getResourceAsStream("/fugue/16x16/icons/acorn.png"));
                ImageView acornImageView = new ImageView();
                acornImageView.setImage(acornImage);
                Label acornLabel = new Label("", acornImageView);
                Tooltip tooltip = new Tooltip();
                tooltip.setText("Acorn");
//                acornLabel.setTooltip(tooltip);
//                acornLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//                    @Override
//                    public void handle(MouseEvent t) {
//                        final Stage stage = new Stage();
//                        //create root node of scene, i.e. group
//                        Group rootGroup = new Group();
//                        //create scene with set width, height and color
//                        Scene scene = new Scene(rootGroup, 200, 200, Color.WHITESMOKE);
//                        //set scene to stage
//                        stage.setScene(scene);
//                        //center stage on screen
//                        stage.centerOnScreen();
//                        //show the stage
//                        stage.show();
//                        //add some node to scene
//                        Text text = new Text(20, 110, "Acorn");
//                        text.setFill(Color.DODGERBLUE);
//                        text.setEffect(new Lighting());
//                        text.setFont(Font.font(Font.getDefault().getFamily(), 50));
//                        //add text to the main root group
//                        rootGroup.getChildren().add(text);
//                    }
//                });

                double maxWidth = 0;
                int hGap = 1;
                maxWidth = maxWidth + navPrimImage.getWidth() + (hGap * 2);
                maxWidth = maxWidth + navDefinedImage.getWidth() + (hGap * 2);
                maxWidth = maxWidth + acornImageView.getImage().getWidth() + (hGap * 2);

                FlowPane iconPane = new FlowPane();
                iconPane.setMaxWidth(maxWidth);
                iconPane.setHgap(hGap);
                iconPane.getChildren().addAll(navPrimImageView, navDefinedImageView, acornLabel);
                this.setGraphic(iconPane);
                this.setContentDisplay(ContentDisplay.LEFT);
                if (preferred) {
                    setText(t.getPreferredText());
                } else {
                    setText(t.getFullySpecifiedText());
                }
            } catch (IOException iOException) {
                setText(iOException.getLocalizedMessage());
                Exceptions.printStackTrace(iOException);
            }
        } else {
            setGraphic(null);
        }
    }
}
