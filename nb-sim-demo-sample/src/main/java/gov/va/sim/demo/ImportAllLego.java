
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.sim.demo;

//~--- non-JDK imports --------------------------------------------------------

import gov.va.demo.dom.DomExpressionAdapter;
import gov.va.demo.dom.PncsLegoMapGenerator;
import gov.va.demo.expression.ExpressionManager;
import gov.va.demo.nb.sim.jpa.Expressions;
import gov.va.demo.nb.sim.jpa.JpaManager;
import gov.va.demo.terminology.TerminologyService;
import gov.va.sim.impl.expression.Expression;

import org.ihtsdo.tk.api.blueprint.ConceptCB;
import org.ihtsdo.tk.api.concept.ConceptVersionBI;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

import org.w3c.dom.Document;

//~--- JDK imports ------------------------------------------------------------

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;

import java.security.NoSuchAlgorithmException;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@ActionID(
   category    = "Edit",
   id          = "gov.va.sim.demo.ImportAllLego"
)
@ActionRegistration(
   iconBase    = "gov/va/sim/demo/database-import.png",
   displayName = "#CTL_ImportAllLego"
)
@ActionReferences({ @ActionReference(
   path        = "Menu/File",
   position    = 1000
) , @ActionReference(
   path        = "Toolbars/File",
   position    = 100
) })
@Messages("CTL_ImportAllLego=Process all LEGO...")
public final class ImportAllLego extends SwingWorker<Object, Object> implements ActionListener {
   File topLegoDir;

   //~--- methods -------------------------------------------------------------

   @Override
   public void actionPerformed(ActionEvent e) {
      final JFileChooser fc = new JFileChooser();

      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      int returnVal = fc.showOpenDialog(null);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
         topLegoDir = fc.getSelectedFile();
         execute();
      }
   }

   private boolean checkExpressionTable(Expression expression) {
      EntityManager em      = JpaManager.getEntityManager();
      boolean       inTable = false;

      try {
         Query countEuuidQuery = em.createNamedQuery("Expressions.countEuuid");

         countEuuidQuery.setParameter("euuid", expression.getUuid().toString());

         List obs   = countEuuidQuery.getResultList();
         long count = (Long) obs.get(0);

         if (count > 0) {
            inTable = true;
         }
      } catch (IOException ex) {
         Exceptions.printStackTrace(ex);
      } catch (NoSuchAlgorithmException ex) {
         Exceptions.printStackTrace(ex);
      }

      return inTable;
   }

   @Override
   protected Object doInBackground() throws Exception {
      File[] files = topLegoDir.listFiles(new FileFilter() {
         @Override
         public boolean accept(File pathname) {
            return pathname.getName().endsWith(".xml");
         }
      });

      if (files != null) {
         for (File f : files) {
            processLegoFile(f);
            processMapFile(f);
         }
      }

      return null;
   }

   private void processLegoFile(File f) {
      FileInputStream fis        = null;
      Expression      expression = null;

      try {
         System.out.println("Processing: " + f.getName());

         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder        builder = factory.newDocumentBuilder();

         fis = new FileInputStream(f);

         Document             assertionDoc   = builder.parse(fis);
         List<Expression>     expressionList = DomExpressionAdapter.convertToExpressionList(assertionDoc);
         Iterator<Expression> expressionItr  = expressionList.iterator();

         while (expressionItr.hasNext()) {
            expression = expressionItr.next();

            int cnid = ExpressionManager.getCnid(expression);

            if (cnid == Integer.MIN_VALUE) {

               // Not in EL++ index, need to add...
               if ((expression.getFocus() != null) && (expression.getFocus().getValue() != null)) {
                  ConceptCB blueprint = ExpressionManager.getBlueprint(expression);

                  TerminologyService.getBuilder().construct(blueprint);
                  TerminologyService.commit();

                  ConceptVersionBI newConcept =
                     TerminologyService.getSnapshot().getConceptVersion(blueprint.getComponentUuid());

                  cnid = newConcept.getNid();
               } else {
                  System.out.println(
                      "FAIL: Focus of expression (or it's value) is null... Will not make concept. ");
               }
            }

            if (cnid != Integer.MIN_VALUE) {

               // in index, maybe add to expression table.
               boolean inTable = checkExpressionTable(expression);

               if (!inTable) {
                  EntityManager em = JpaManager.getEntityManager();

                  try {
                     EntityTransaction entr = em.getTransaction();

                     entr.begin();

                     Expressions expressionObj = new Expressions();

                     expressionObj.setCnid(ExpressionManager.getCnid(expression));
                     expressionObj.setEuuid(expression.getUuid().toString());
                     expressionObj.setExpression(expression.getXml());
                     em.persist(expressionObj);
                     entr.commit();
                  } catch (IOException ex) {
                     Exceptions.printStackTrace(ex);
                  } catch (NoSuchAlgorithmException ex) {
                     Exceptions.printStackTrace(ex);
                  }
               }
            }
         }
      } catch (Exception ex) {
         Exceptions.printStackTrace(ex);
      } finally {
         try {
            if (fis != null) {
               fis.close();
            }
         } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
         }
      }
   }

   private void processMapFile(File f) {
      FileInputStream fis = null;

      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder        builder = factory.newDocumentBuilder();

         fis = new FileInputStream(f);

         Document             mapDoc       = builder.parse(fis);
         PncsLegoMapGenerator mapGenerator = new PncsLegoMapGenerator();

         mapGenerator.addToMap(mapDoc);
      } catch (Exception ex) {
         Exceptions.printStackTrace(ex);
      } finally {
         try {
            fis.close();
         } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
         }
      }
   }
}
