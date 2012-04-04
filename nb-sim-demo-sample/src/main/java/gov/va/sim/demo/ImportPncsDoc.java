
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.sim.demo;

//~--- non-JDK imports --------------------------------------------------------

import gov.va.demo.nb.sim.jpa.*;

import org.ihtsdo.helper.uuid.Type5UuidFactory;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

//~--- JDK imports ------------------------------------------------------------

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@ActionID(
   category    = "Edit",
   id          = "gov.va.sim.demo.ImportPncsDoc"
)
@ActionRegistration(
   iconBase    = "gov/va/sim/demo/document--plus.png",
   displayName = "#CTL_ImportPncsDoc"
)
@ActionReferences({ @ActionReference(
   path        = "Menu/File",
   position    = 900
) , @ActionReference(
   path        = "Toolbars/File",
   position    = 0
) })
@Messages("CTL_ImportPncsDoc=Import PNCS Document")
public final class ImportPncsDoc extends SwingWorker<Object, Object> implements ActionListener {
   File pncsDocFile;

   //~--- methods -------------------------------------------------------------

   @Override
   public void actionPerformed(ActionEvent e) {
      final JFileChooser fc = new JFileChooser();

      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      fc.addChoosableFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            return f.getName().toLowerCase().endsWith(".xml");
         }
         @Override
         public String getDescription() {
            return "XML files";
         }
      });

      int returnVal = fc.showOpenDialog(null);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
         pncsDocFile = fc.getSelectedFile();
         execute();
      }
   }

   private Documents checkDocumentTable(UUID docUuid) {
      EntityManager em              = JpaManager.getEntityManager();
      Query         countEuuidQuery = em.createNamedQuery("Documents.findByDuuid");

      countEuuidQuery.setParameter("duuid", docUuid.toString());

      List obs = countEuuidQuery.getResultList();

      if (!obs.isEmpty()) {
         return (Documents) obs.get(0);
      }

      return null;
   }

   private Intervals checkIntervalTable(UUID intervalUuid) {
      EntityManager em              = JpaManager.getEntityManager();
      Query         countIuuidQuery = em.createNamedQuery("Intervals.findByIuuid");

      countIuuidQuery.setParameter("iuuid", intervalUuid.toString());

      List<Intervals> obs = countIuuidQuery.getResultList();

      if (!obs.isEmpty()) {
         return (Intervals) obs.get(0);
      }

      return null;
   }

   private PncsLegoMap checkPcnsMap(String idStr, String valueStr) {
      EntityManager em            = JpaManager.getEntityManager();
      Query         countMapQuery = em.createNamedQuery("PncsLegoMap.findByPncsIdPncsValue");

      countMapQuery.setParameter("pncsValue", valueStr);
      countMapQuery.setParameter("pncsId", Integer.parseInt(idStr));

      List obs = countMapQuery.getResultList();

      if (!obs.isEmpty()) {
         return (PncsLegoMap) obs.get(0);
      }

      return null;
   }

   private Persons checkPersonTable(UUID docUuid) {
      EntityManager em              = JpaManager.getEntityManager();
      Query         countEuuidQuery = em.createNamedQuery("Persons.findByPuuid");

      countEuuidQuery.setParameter("puuid", docUuid.toString());

      List<Persons> obs = countEuuidQuery.getResultList();

      if (!obs.isEmpty()) {
         return (Persons) obs.get(0);
      }

      return null;
   }

   @Override
   protected Object doInBackground() {
      FileInputStream fis = null;

      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder        builder = factory.newDocumentBuilder();

         fis = new FileInputStream(pncsDocFile);

         Document   pncsDoc                  = builder.parse(fis);
         int        whatToShow               = NodeFilter.SHOW_ELEMENT;
         NodeFilter filter                   = new SectionNodeFilter();
         boolean    entityReferenceExpansion = false;
         TreeWalker walker                   = ((DocumentTraversal) pncsDoc).createTreeWalker(pncsDoc, whatToShow, filter,
                                entityReferenceExpansion);
         Node    domNode           = walker.nextNode();
         Node    sectionNode       = null;
         boolean headerSection     = false;
         String  patientName       = null;
         String  patientIEN        = null;
         String  authorName        = null;
         String  documentTimestamp = null;
         String  institutionName   = null;
         String  institutionNumber = null;
         Date    timestamp;
         UUID    providerUuid    = null;
         UUID    patientUuid     = null;
         UUID    documentUuid    = null;
         UUID    intervalUuid    = null;
         int     patientPnid     = Integer.MAX_VALUE;
         int     providerPnid    = Integer.MAX_VALUE;
         int     documentNid     = Integer.MAX_VALUE;
         int     intervalNid     = Integer.MAX_VALUE;
         String  sectionIdStr    = null;
         String  sectionValueStr = null;

         // 03/07/2012@19:36
         SimpleDateFormat sdf           = new SimpleDateFormat("dd/MM/yyyy@HH:mm");
         // Jan 12, 2011@12:34
         SimpleDateFormat sdf2           = new SimpleDateFormat("MMM DD, yyyy@HH:mm");
         int              mappedCount   = 0;
         int              unmappedCount = 0;
         short            sequenceInDoc = 0;

         while (domNode != null) {
            sequenceInDoc++;

            if (domNode.getNodeName().equals("section")) {
               sectionNode = domNode;

               Node attr = sectionNode.getAttributes().getNamedItem("text");

               if (attr.getTextContent().equalsIgnoreCase("Section=Header")) {
                  headerSection = true;
               } else {
                  if (headerSection) {
                     headerSection = false;
                     System.out.println("patientName: " + patientName);
                     System.out.println("patientIEN: " + patientIEN);
                     System.out.println("authorName: " + authorName);
                     System.out.println("timeStamp: " + documentTimestamp);
                     System.out.println("institutionName: " + institutionName);
                     System.out.println("institutionNumber: " + institutionNumber);
                     providerUuid =
                        Type5UuidFactory.get(UUID.fromString("59c115d0-79c5-11e1-b0c4-0800200c9a66"),
                                             authorName);
                     patientUuid =
                        Type5UuidFactory.get(UUID.fromString("59c115d2-79c5-11e1-b0c4-0800200c9a66"),
                                             patientIEN);
                      try {
                          timestamp = sdf.parse(documentTimestamp);
                      } catch (ParseException parseException) {
                          timestamp = sdf2.parse(documentTimestamp);
                      }
                     String intervalStart = timestamp.toString();
                     String intervalEnd   = timestamp.toString();

                     intervalUuid =
                        Type5UuidFactory.get(UUID.fromString("6703d801-7b54-11e1-b0c4-0800200c9a66"),
                                             intervalStart + intervalEnd);
                     documentUuid =
                        Type5UuidFactory.get(UUID.fromString("59c115d3-79c5-11e1-b0c4-0800200c9a66"),
                                             authorName + patientIEN + intervalUuid.toString());
                     System.out.println("providerUuid: " + providerUuid);
                     System.out.println("patientUuid: " + patientUuid);
                     System.out.println("documentUuid: " + documentUuid);

                     Persons patient = checkPersonTable(patientUuid);

                     if (patient == null) {

                        // add patient to person table
                        EntityManager     em   = JpaManager.getEntityManager();
                        EntityTransaction entr = em.getTransaction();

                        entr.begin();

                        Persons personObj = new Persons();

                        personObj.setDob(new Date());

                        String[] names = patientName.split(",");

                        personObj.setFirstname(names[1]);
                        personObj.setLastname(names[0]);
                        personObj.setPuuid(patientUuid.toString());
                        em.persist(personObj);
                        entr.commit();
                        System.out.println("added patient Persons: " + personObj);
                        patient = personObj;
                     }

                     patientPnid = patient.getPnid();

                     Persons provider = checkPersonTable(providerUuid);

                     if (provider == null) {

                        // add provider to person table
                        EntityManager     em   = JpaManager.getEntityManager();
                        EntityTransaction entr = em.getTransaction();

                        entr.begin();

                        Persons personObj = new Persons();

                        personObj.setDob(new Date());

                        String[] names = authorName.split(",");

                        personObj.setFirstname(names[1]);
                        personObj.setLastname(names[0]);
                        personObj.setPuuid(providerUuid.toString());
                        em.persist(personObj);
                        entr.commit();
                        System.out.println("added provider Persons: " + personObj);
                        provider = personObj;
                     }

                     providerPnid = provider.getPnid();

                     Intervals interval = checkIntervalTable(intervalUuid);

                     if (interval == null) {
                        EntityManager     em   = JpaManager.getEntityManager();
                        EntityTransaction entr = em.getTransaction();

                        entr.begin();

                        Intervals intervalObj = new Intervals();

                        intervalObj.setIstart(timestamp);
                        intervalObj.setIend(timestamp);
                        intervalObj.setIuuid(intervalUuid.toString());
                        em.persist(intervalObj);
                        entr.commit();
                        System.out.println("added interval for doc: " + intervalObj);
                        interval    = intervalObj;
                        intervalNid = interval.getInid();
                     }

                     Documents doc = checkDocumentTable(documentUuid);

                     if (doc == null) {

                        // add document to document table
                        EntityManager     em   = JpaManager.getEntityManager();
                        EntityTransaction entr = em.getTransaction();

                        entr.begin();

                        Documents docObj = new Documents();

                        docObj.setDuuid(documentUuid.toString());
                        docObj.setInid(interval);
                        docObj.setPatientnid(patient);
                        docObj.setProvidernid(provider);
                        em.persist(docObj);
                        entr.commit();
                        System.out.println("added doc: " + docObj);
                        doc         = docObj;
                        documentNid = doc.getDnid();
                     }

                     System.out.println("in doc table: " + checkDocumentTable(documentUuid));
                     System.out.println("patient in person table: " + checkPersonTable(patientUuid));
                     System.out.println("provider in person table: " + checkPersonTable(providerUuid));
                  } else {    // Section header but not "Section=Header"
                     Node sectionValueNode = sectionNode.getAttributes().getNamedItem("value");

                     if (sectionValueNode != null) {
                        sectionValueStr = sectionValueNode.getTextContent();
                     } else {
                        sectionValueStr = null;
                     }

                     Node sectionIdNode = sectionNode.getAttributes().getNamedItem("id");

                     if (sectionIdNode != null) {
                        sectionIdStr = sectionIdNode.getTextContent();
                     } else {
                        sectionIdStr = null;
                     }
                  }
               }

               System.out.println(attr.getTextContent());
            } else {
               String     idStr       = domNode.getAttributes().getNamedItem("id").getNodeValue();
               TreeWalker valueWalker = ((DocumentTraversal) pncsDoc).createTreeWalker(domNode,
                                           NodeFilter.SHOW_TEXT | NodeFilter.SHOW_CDATA_SECTION, new TextNodeFilter(),
                                           entityReferenceExpansion);
               Node   value    = valueWalker.nextNode();
               String valueStr = value.getNodeValue();

               if (idStr.equals("45456")) {
                  patientName = valueStr;
               } else if (idStr.equals("1234556")) {
                  patientIEN = valueStr;
               } else if (idStr.equals("43325456")) {
                  authorName = valueStr;
               } else if (idStr.equals("3345456")) {
                  documentTimestamp = valueStr;
               } else if (idStr.equals("8845456")) {
                  institutionName = valueStr;
               } else if (idStr.equals("7745456")) {
                  institutionNumber = valueStr;
               }

               System.out.println(sectionNode.getAttributes().getNamedItem("text").getNodeValue() + " "
                                  + domNode.getAttributes().getNamedItem("text").getNodeValue() + " id: "
                                  + idStr + " value: " + valueStr);

               PncsLegoMap pcnsLegoMap = checkPcnsMap(idStr, valueStr);

               if (pcnsLegoMap != null) {
                  System.out.println("   Mapped");
                  mappedCount++;

                  if (documentUuid != null) {
                     PncsLegoMap       pcnsLegoSectionMap = checkPcnsMap(sectionIdStr, sectionValueStr);
                     EntityManager     em                 = JpaManager.getEntityManager();
                     EntityTransaction entr               = em.getTransaction();

                     entr.begin();

                     Documents  doc           = checkDocumentTable(documentUuid);
                     Intervals  interval      = checkIntervalTable(intervalUuid);
                     Assertions assertion     = new Assertions();
                     UUID       assertionUuid =
                        Type5UuidFactory.get(UUID.fromString("c7a482e1-7b90-11e1-b0c4-0800200c9a66"),
                                             documentUuid.toString() + sequenceInDoc);

                     assertion.setAuuid(assertionUuid.toString());
                     assertion.setDiscernableEnid(pcnsLegoMap.getDiscernableEnid());
                     assertion.setDnid(doc);
                     assertion.setInid(interval);
                     assertion.setQualifierEnid(pcnsLegoMap.getQualifierEnid());
                     assertion.setSectionEnid(pcnsLegoSectionMap.getDiscernableEnid());
                     assertion.setSeqInDoc(sequenceInDoc);
                     assertion.setValueEnid(pcnsLegoMap.getValueEnid());
                     doc.getAssertionsCollection().add(assertion);
                     em.persist(doc);
                     entr.commit();
                     System.out.println("Updated doc: " + doc + " with: " + assertion);
                  }
               } else {
                  System.out.println("   NOT Mapped");
                  unmappedCount++;
               }
            }

            domNode = walker.nextNode();
         }

         System.out.println("Processed file: " + pncsDocFile);
         System.out.println("   Mapped: " + mappedCount);
         System.out.println("   NOT Mapped: " + unmappedCount);
      } catch (Exception ex) {
         Exceptions.printStackTrace(ex);
      } finally {
         try {
            fis.close();
         } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
         }
      }

      return null;
   }

   //~--- inner classes -------------------------------------------------------

   private static class SectionNodeFilter implements NodeFilter {
      @Override
      public short acceptNode(Node n) {
         if (n.getNodeName().equals("section")) {
            return NodeFilter.FILTER_ACCEPT;
         }

         if (n.getNodeName().equals("obj")) {
            return NodeFilter.FILTER_ACCEPT;
         }

         return NodeFilter.FILTER_SKIP;
      }
   }


   private static class TextNodeFilter implements NodeFilter {
      @Override
      public short acceptNode(Node n) {
         if (n.getNodeType() == Node.TEXT_NODE) {
            return NodeFilter.FILTER_ACCEPT;
         }
         if (n.getNodeType() == Node.CDATA_SECTION_NODE) {
            return NodeFilter.FILTER_ACCEPT;
         }
         return NodeFilter.FILTER_SKIP;
      }
   }
}
