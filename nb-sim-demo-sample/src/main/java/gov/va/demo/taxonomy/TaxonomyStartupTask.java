
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.demo.taxonomy;

//~--- non-JDK imports --------------------------------------------------------

import gov.va.demo.startup.Installer;
import gov.va.demo.terminology.TerminologyService;

import javafx.concurrent.Task;

import javafx.scene.paint.Color;

import org.ihtsdo.taxonomy.items.TaxonomyItemSetup;
import org.ihtsdo.taxonomy.model.ChildItemFilterBI;
import org.ihtsdo.taxonomy.model.TaxonomyModel;
import org.ihtsdo.tk.api.NidList;
import org.ihtsdo.tk.api.NidListBI;
import org.ihtsdo.tk.binding.Taxonomies;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashMap;

/**
 *
 * @author kec
 */
public class TaxonomyStartupTask extends Task<TaxonomyModel> {
   @Override
   protected TaxonomyModel call() throws Exception {
      Installer.dbReadyLatch.await();

      NidListBI roots = new NidList();

      roots.add(Taxonomies.SNOMED.getLenient().getNid());
      roots.add(Taxonomies.REFSET_AUX.getLenient().getNid());
      roots.add(Taxonomies.WB_AUX.getLenient().getNid());

      TaxonomyItemSetup renderer =
         new TaxonomyItemSetup(TerminologyService.getSnapshot().getViewCoordinate(),
                               TerminologyService.getBuilder().getEditCoordinate(),
                               new HashMap<Integer, Color>());
      ChildItemFilterBI childNodeFilter = null;
      TaxonomyModel     model           = new TaxonomyModel(TerminologyService.getSnapshot(), roots, renderer,
                               childNodeFilter);

      model.getRoot().setExpanded(true);

      return model;
   }
}
