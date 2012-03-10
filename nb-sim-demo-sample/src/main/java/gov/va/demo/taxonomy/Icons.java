/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.taxonomy;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author kec
 */
public enum Icons {
    
        PRIMITIVE_SINGLE_PARENT(setupImage("/fugue/16x16/icons-shadowless/navigation-nowhere-button-white.png")),
        PRIMITIVE_MULTI_PARENT_CLOSED(setupImage("/fugue/16x16/icons-shadowless/navigation-090-button-white.png")),
        PRIMITIVE_MULTI_PARENT_OPEN(setupImage("/fugue/16x16/icons-shadowless/navigation-045-button-white.png")),
        DEFINED_SINGLE_PARENT(setupImage("/fugue/16x16/icons-shadowless/navigation-nowhere-2.png")),
        DEFINED_MULTI_PARENT_CLOSED(setupImage("/fugue/16x16/icons-shadowless/navigation-090.png")),
        DEFINED_MULTI_PARENT_OPEN(setupImage("/fugue/16x16/icons-shadowless/navigation-045.png")),
        ROOT(setupImage("/fugue/16x16/icons-shadowless/node.png")),
        
        GREEN_TICK(setupImage("/fugue/16x16/icons-shadowless/tick.png")),
        RED_X(setupImage("/fugue/16x16/icons-shadowless/cross.png")),
 
        TAXONOMY_OPEN(setupImage("/fugue/16x16/icons-shadowless/plus-small.png")),
        TAXONOMY_CLOSE(setupImage("/fugue/16x16/icons-shadowless/minus-small.png")),
        
        
        ;
        private Image icon;

        //~--- constructors -----------------------------------------------------
        private Icons(Image icon) {
            this.icon = icon;
        }
        
    public ImageView getImageView() {
        ImageView iv = new ImageView();
        iv.setImage(icon);
        return iv;
    }
        
    private static Image setupImage(String image) {
        Image icon = new Image(Icons.class.getResourceAsStream(image));
        return icon;
    }

}