
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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kec
 */
public class DomExpressionAdapter {

    public static List<Expression> convertToExpressionList(Document assertionDoc) throws IOException, PropertyVetoException {
        ArrayList<Expression> expressionList = new ArrayList<Expression>();
        int whatToShow = NodeFilter.SHOW_ELEMENT;
        NodeFilter filter = new ExpressionRootNodeFilter();
        boolean entityReferenceExpansion = false;
        TreeWalker walker = ((DocumentTraversal) assertionDoc).createTreeWalker(assertionDoc, whatToShow, filter,
                entityReferenceExpansion);
        Node domNode = walker.nextNode();
        while (domNode != null) {
            TreeWalker expressionWalker = ((DocumentTraversal) assertionDoc).createTreeWalker(domNode, whatToShow, null,
                entityReferenceExpansion);
            Expression exp = convertToExpression(expressionWalker);
            //System.out.println("Exp: " + exp);
            expressionList.add(exp);
            domNode = walker.nextNode();
        }
        //System.out.println("Expression list: " + expressionList.size() + " " + expressionList);
        return expressionList;
    }

    public static class ExpressionRootNodeFilter implements NodeFilter {

        @Override
        public short acceptNode(Node n) {

            if (n.getNodeName().equals("concept")) {
                Node parent = n.getParentNode();
                if (parent.getNodeName().equals("discernable")
                        || parent.getNodeName().equals("qualifier")
                        || parent.getNodeName().equals("value")) {
                    return NodeFilter.FILTER_ACCEPT;
                }
            } else if (n.getNodeName().equals("rel")) {
                return NodeFilter.FILTER_REJECT;
            }
            return NodeFilter.FILTER_SKIP;
        }
    }

    public static Expression convertToExpression(TreeWalker expressionWalker) throws IOException, PropertyVetoException {
        Node node = expressionWalker.getCurrentNode();
        Expression exp = new Expression();
        String sctId = node.getAttributes().getNamedItem("sctid").getNodeValue();
        ConceptVersionBI cv = TerminologyService.getSnapshot().getConceptVersionFromAlternateId(sctId);
        ConceptNode expNode = new ConceptNode();

        expNode.setValue(cv);
        exp.setFocus(expNode);
        traverseRelTypeLevel(TerminologyService.getSnapshot(), expressionWalker, 0, expNode);

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
        Node parent = walker.getCurrentNode();

        // traverse children:
        for (Node destinationDomNode = walker.firstChild(); destinationDomNode != null;
                destinationDomNode = walker.nextSibling()) {
            String sctId = destinationDomNode.getAttributes().getNamedItem("sctid").getNodeValue();
            ConceptVersionBI descCv = tSnap.getConceptVersionFromAlternateId(sctId);
            ConceptNode destNode = new ConceptNode();

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
        Node parent = walker.getCurrentNode();

        // traverse children:
        for (Node typeDomNode = walker.firstChild(); typeDomNode != null; typeDomNode = walker.nextSibling()) {
            String sctId = typeDomNode.getAttributes().getNamedItem("sctid").getNodeValue();
            ConceptVersionBI typeCv = tSnap.getConceptVersionFromAlternateId(sctId);

            traverseRelDestLevel(tSnap, walker, level + 1, expNode, typeCv);
        }

        // return position to the current (level up):
        walker.setCurrentNode(parent);
    }
}
