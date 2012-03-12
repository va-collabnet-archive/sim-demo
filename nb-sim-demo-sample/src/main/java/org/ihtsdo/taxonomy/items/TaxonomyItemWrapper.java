/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.taxonomy.items;

import javafx.scene.control.TreeItem;

/**
 *
 * @author kec
 */
public class TaxonomyItemWrapper extends TreeItem<Item> {

    public TaxonomyItemWrapper(Item t) {
        super(t);
    }

    @Override
    public boolean isLeaf() {
        return getValue().isLeaf();
    }

 }
