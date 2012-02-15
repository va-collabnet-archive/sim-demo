
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.demo.dom;

//~--- non-JDK imports --------------------------------------------------------

import gov.va.demo.terminology.TerminologyService;
import org.ihtsdo.tk.api.TerminologySnapshotDI;
import org.ihtsdo.tk.api.concept.ConceptVersionBI;

import gov.va.sim.impl.expression.Expression;
import gov.va.sim.impl.expression.node.ConceptNode;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

//~--- JDK imports ------------------------------------------------------------

import java.beans.PropertyVetoException;

import java.io.IOException;

/**
 *
 * @author kec
 */
public class DomExpressionAdapter {
   public static Expression convertToExpression(Document document) throws IOException, PropertyVetoException {
      int        whatToShow               = NodeFilter.SHOW_ELEMENT;
      NodeFilter filter                   = null;
      boolean    entityReferenceExpansion = false;
      TreeWalker walker                   = ((DocumentTraversal) document).createTreeWalker(document, whatToShow, filter,
                             entityReferenceExpansion);
      Expression            exp     = new Expression();
      Node                  domNode = walker.firstChild();
      String                sctId   = domNode.getAttributes().getNamedItem("sctid").getNodeValue();
      ConceptVersionBI      cv      = TerminologyService.getSnapshot().getConceptVersionFromAlternateId(sctId);
      ConceptNode           expNode = new ConceptNode();

      expNode.setValue(cv);
      exp.setFocus(expNode);
      traverseRelTypeLevel(TerminologyService.getSnapshot(), walker, 0, expNode);

      return exp;
   }

   private static Node describeCurrentNode(TreeWalker walker, int level) throws DOMException {

      // describe current node:
      Node parent = walker.getCurrentNode();

      for (int i = 0; i < level; i++) {
         System.out.print("    ");
      }

      System.out.println("- " + parent.getAttributes().getNamedItem("sctid").getNodeValue());

      return parent;
   }

   private static void traverseRelDestLevel(TerminologySnapshotDI tSnap, TreeWalker walker, int level,
           ConceptNode origin, ConceptVersionBI typeCv)
           throws IOException, PropertyVetoException {
      Node parent = describeCurrentNode(walker, level);

      // traverse children:
      for (Node destinationDomNode = walker.firstChild(); destinationDomNode != null;
              destinationDomNode = walker.nextSibling()) {
         String           sctId    = destinationDomNode.getAttributes().getNamedItem("sctid").getNodeValue();
         ConceptVersionBI descCv   = tSnap.getConceptVersionFromAlternateId(sctId);
         ConceptNode      destNode = new ConceptNode();

         destNode.setValue(descCv);
         origin.addRel(typeCv, destNode);
         traverseRelTypeLevel(tSnap, walker, level + 1, destNode);
      }

      // return position to the current (level up):
      walker.setCurrentNode(parent);
   }

   private static void traverseRelTypeLevel(TerminologySnapshotDI tSnap, TreeWalker walker, int level,
           ConceptNode expNode)
           throws IOException, PropertyVetoException {
      Node parent = describeCurrentNode(walker, level);

      // traverse children:
      for (Node typeDomNode = walker.firstChild(); typeDomNode != null; typeDomNode = walker.nextSibling()) {
         String           sctId  = typeDomNode.getAttributes().getNamedItem("sctid").getNodeValue();
         ConceptVersionBI typeCv = tSnap.getConceptVersionFromAlternateId(sctId);

         traverseRelDestLevel(tSnap, walker, level + 1, expNode, typeCv);
      }

      // return position to the current (level up):
      walker.setCurrentNode(parent);
   }
}
