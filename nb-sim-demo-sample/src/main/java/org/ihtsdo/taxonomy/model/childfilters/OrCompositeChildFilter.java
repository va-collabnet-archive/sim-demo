
/*
* To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.ihtsdo.taxonomy.model.childfilters;

//~--- non-JDK imports --------------------------------------------------------

import org.ihtsdo.taxonomy.model.ChildItemFilterBI;
import org.ihtsdo.tk.api.concept.ConceptVersionBI;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kec
 */
public class OrCompositeChildFilter implements ChildItemFilterBI {
   List<ChildItemFilterBI> filterList = new ArrayList<ChildItemFilterBI>();

   //~--- methods -------------------------------------------------------------

   @Override
   public boolean pass(ConceptVersionBI parent, ConceptVersionBI possibleChild) throws Exception {
      if (filterList.isEmpty()) {
         return true;
      }

      for (ChildItemFilterBI filter : filterList) {
         if (filter.pass(parent, possibleChild)) {
            return true;
         }
      }

      return false;
   }

   //~--- get methods ---------------------------------------------------------

   public List<ChildItemFilterBI> getFilterList() {
      return filterList;
   }
}
