
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.ihtsdo.taxonomy.items;

//~--- non-JDK imports --------------------------------------------------------


import org.ihtsdo.taxonomy.model.TaxonomyModel;

/**
 *
 * @author kec
 */
public class TaxonomyRootItemWrapper extends TaxonomyItemWrapper {
   private TaxonomyModel model;

   //~--- constructors --------------------------------------------------------

   public TaxonomyRootItemWrapper(Item t, TaxonomyModel model) {
      super(t);
      this.model = model;
   }

   //~--- get methods ---------------------------------------------------------

   public TaxonomyModel getModel() {
      return model;
   }
}
