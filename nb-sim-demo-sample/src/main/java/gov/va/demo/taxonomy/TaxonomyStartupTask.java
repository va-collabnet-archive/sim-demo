
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.demo.taxonomy;

//~--- non-JDK imports --------------------------------------------------------

import gov.va.demo.startup.Installer;
import gov.va.demo.terminology.TerminologyService;
import java.util.HashMap;

import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import org.ihtsdo.taxonomy.ItemStore;
import org.ihtsdo.taxonomy.items.ItemIdComparator;
import org.ihtsdo.taxonomy.items.Item;
import org.ihtsdo.taxonomy.items.TaxonomyItemSetup;
import org.ihtsdo.taxonomy.model.ChildItemFilterBI;
import org.ihtsdo.taxonomy.model.ItemFactory;
import org.ihtsdo.taxonomy.model.TaxonomyModel;
import org.ihtsdo.tk.api.NidList;
import org.ihtsdo.tk.api.NidListBI;
import org.ihtsdo.tk.binding.Taxonomies;

/**
 *
 * @author kec
 */
public class TaxonomyStartupTask extends Task<TaxonomyStartupTask> {
   ItemStore store = new ItemStore();
   ItemFactory factory; 
   TreeItem<Item> treeRoot;
   ItemIdComparator comparator = new ItemIdComparator(store);
   TaxonomyModel model;
   
   @Override
   protected TaxonomyStartupTask call() throws Exception {
      Installer.dbReadyLatch.await();


      NidListBI roots = new NidList();
      roots.add(Taxonomies.SNOMED.getLenient().getNid());
      roots.add(Taxonomies.REFSET_AUX.getLenient().getNid());
      roots.add(Taxonomies.WB_AUX.getLenient().getNid());
      TaxonomyItemSetup renderer = new TaxonomyItemSetup(TerminologyService.getSnapshot().getViewCoordinate(), 
            TerminologyService.getBuilder().getEditCoordinate(), 
            new HashMap<Integer, Color>());
      ChildItemFilterBI childNodeFilter = null;
      
      model = new TaxonomyModel(TerminologyService.getSnapshot(), roots, renderer,
                        childNodeFilter);
      
      
      treeRoot = model.getRoot();
      
      factory = new ItemFactory(model, renderer, childNodeFilter);
      treeRoot.setExpanded(true);
      return this;
   }
}
