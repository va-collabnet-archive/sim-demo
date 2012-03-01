/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.taxonomy.items;

import javafx.scene.control.TreeItem;
import org.ihtsdo.taxonomy.model.TaxonomyModel;

/**
 *
 * @author kec
 */
public class TaxonomyItemWrapper extends TreeItem<Item> {
    
    private TaxonomyModel model;

    public TaxonomyItemWrapper(Item item, TaxonomyModel model) {
        super(item);
        this.model = model;

    }

    @Override
    public boolean isLeaf() {
        return getValue().isLeaf();
    }

 }
