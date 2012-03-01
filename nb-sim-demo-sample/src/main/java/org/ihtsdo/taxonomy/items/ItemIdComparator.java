/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.taxonomy.items;

import java.util.Comparator;
import org.ihtsdo.taxonomy.ItemStore;

/**
 *
 * @author kec
 */
public class ItemIdComparator implements Comparator<Long> {

    ItemStore  nodeStore;

    public ItemIdComparator(ItemStore store) {
        this.nodeStore = store;
    }
    
    @Override
    public int compare(Long nId1, Long nId2) {
        if (nId1.equals(nId2)) {
            return 0;
        }
        Item n1 = nodeStore.get(nId1);
        Item n2 = nodeStore.get(nId2);
        int maxIndex = Math.min(n1.nodesToCompare.length, n2.nodesToCompare.length);
        for (int i = 0; i < maxIndex; i++) {
            if (n1.nodesToCompare[i] == Long.MAX_VALUE && n2.nodesToCompare[i] == Long.MAX_VALUE) {
                // different concepts, but identical text. Return difference of concept ids. 
                return n1.getCnid() - n2.getCnid();
            }
            if (n1.nodesToCompare[i] == Long.MAX_VALUE) {
                return 1;
            }
            if (n2.nodesToCompare[i] == Long.MAX_VALUE) {
                return -1;
            }
            Item nc1 = nodeStore.get(n1.nodesToCompare[i]);
            Item nc2 = nodeStore.get(n2.nodesToCompare[i]);
            int comparison = nc1.sortComparable.compareTo(nc2.sortComparable);
            if (comparison != 0) {
                return comparison;
            }
        }
        if (n1.nodesToCompare.length > n2.nodesToCompare.length) {
            return -1;
        }
        return 1;
    }
    
}
