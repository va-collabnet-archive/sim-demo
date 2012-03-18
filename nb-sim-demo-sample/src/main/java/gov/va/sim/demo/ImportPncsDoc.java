/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.sim.demo;

import gov.va.demo.dom.PncsLegoMapGenerator;
import gov.va.demo.nb.sim.jpa.JpaManager;
import gov.va.sim.impl.expression.Expression;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

@ActionID(category = "Edit",
id = "gov.va.sim.demo.ImportPncsDoc")
@ActionRegistration(iconBase = "gov/va/sim/demo/document--plus.png",
displayName = "#CTL_ImportPncsDoc")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 900),
    @ActionReference(path = "Toolbars/File", position = 0)
})
@Messages("CTL_ImportPncsDoc=Import PNCS Document")
public final class ImportPncsDoc extends SwingWorker<Object, Object> implements ActionListener {

    File pncsDocFile;

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

    @Override
    protected Object doInBackground() {
        FileInputStream fis = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            fis = new FileInputStream(pncsDocFile);
            Document pncsDoc = builder.parse(fis);


            int whatToShow = NodeFilter.SHOW_ELEMENT;
            NodeFilter filter = new SectionNodeFilter();
            boolean entityReferenceExpansion = false;
            TreeWalker walker = ((DocumentTraversal) pncsDoc).createTreeWalker(pncsDoc, whatToShow, filter,
                    entityReferenceExpansion);

            Node domNode = walker.nextNode();
            Node sectionNode = null;
            while (domNode != null) {
                if (domNode.getNodeName().equals("section")) {
                    sectionNode = domNode;
                    System.out.println();
                } else {
                    String idStr = domNode.getAttributes().getNamedItem("id").getNodeValue();
                    TreeWalker valueWalker = ((DocumentTraversal) pncsDoc).createTreeWalker(domNode,
                            NodeFilter.SHOW_TEXT,
                            new TextNodeFilter(),
                            entityReferenceExpansion);
                    Node value = valueWalker.nextNode();
                    String valueStr = value.getNodeValue();
                    System.out.println(sectionNode.getAttributes().getNamedItem("text").getNodeValue()
                            + domNode.getAttributes().getNamedItem("text").getNodeValue()
                            + " id: " + idStr + " value: " + valueStr);
                    if (checkPcnsMap(idStr, valueStr)) {
                        System.out.println("   Mapped");
                    } else {
                        System.out.println("   NOT Mapped");
                    }
                }

                domNode = walker.nextNode();
            }
            System.out.println("Processed file: " + pncsDocFile);
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

    private boolean checkPcnsMap(String idStr, String valueStr) {
        EntityManager em = JpaManager.getEntityManager();
        boolean inTable = false;

        Query countMapQuery = em.createNamedQuery("PncsLegoMap.countPncsIdPncsValue");
        countMapQuery.setParameter("pncsValue", valueStr);
        countMapQuery.setParameter("pncsId", Integer.parseInt(idStr));
        List obs = countMapQuery.getResultList();
        long count = (Long) obs.get(0);
        if (count > 0) {
            inTable = true;
        }
        return inTable;
    }

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
            return NodeFilter.FILTER_SKIP;
        }
    }
}
