
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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.util.Exceptions;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author kec
 */
public class DomExpressionAdapter {

    public static Expression convertToExpression(String expression) throws ParserConfigurationException, SAXException, IOException {

        StringBuilder docBuilder = new StringBuilder("<doc><objective><assertion><discernable>");

        docBuilder.append(expression);
        docBuilder.append("</discernable></assertion></objective></doc>");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document assertionDoc = builder.parse(new InputSource(new StringReader(docBuilder.toString())));

        int whatToShow = NodeFilter.SHOW_ELEMENT;
        NodeFilter filter = new ExpressionRootNodeFilter();
        boolean entityReferenceExpansion = false;
        TreeWalker walker = ((DocumentTraversal) assertionDoc).createTreeWalker(assertionDoc, whatToShow, filter,
                entityReferenceExpansion);
        Node domNode = walker.nextNode();
        while (domNode != null) {
            TreeWalker expressionWalker = ((DocumentTraversal) assertionDoc).createTreeWalker(domNode, whatToShow, null,
                    entityReferenceExpansion);
            String nodeString = expressionWalker.getCurrentNode().getParentNode().getParentNode().toString();
            nodeString = nodeString + expressionWalker.getCurrentNode().getParentNode().toString();
            nodeString = nodeString + expressionWalker.getCurrentNode().toString();
            nodeString = nodeString + expressionWalker.toString();
            try {
                Expression exp = convertToExpression(expressionWalker);
                return exp;
            } catch (Exception iOException) {
                Exceptions.printStackTrace(new Exception("Processing: " + nodeString, iOException));
            }
            domNode = walker.nextNode();
        }
        return null;
    }

    public static List<Expression> convertToExpressionList(Document assertionDoc) {
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
            String nodeString = expressionWalker.getCurrentNode().getParentNode().getParentNode().toString();
            nodeString = nodeString + expressionWalker.getCurrentNode().getParentNode().toString();
            nodeString = nodeString + expressionWalker.getCurrentNode().toString();
            nodeString = nodeString + expressionWalker.toString();
            try {
                Expression exp = convertToExpression(expressionWalker);
                expressionList.add(exp);
            } catch (Exception iOException) {
                Exceptions.printStackTrace(new Exception("Processing: " + nodeString, iOException));
            }
            domNode = walker.nextNode();
        }
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
        ConceptVersionBI cv;
        if (node.getAttributes().getNamedItem("sctid") != null) {
            String sctId = node.getAttributes().getNamedItem("sctid").getNodeValue();
            cv = TerminologyService.getSnapshot().getConceptVersionFromAlternateId(sctId);
        } else {
            String uuid = node.getAttributes().getNamedItem("uuid").getNodeValue();
            cv = TerminologyService.getSnapshot().getConceptVersion(UUID.fromString(uuid));
        }
        ConceptNode expNode = new ConceptNode();

        expNode.setValue(cv);
        exp.setFocus(expNode);
        traverseRelTypeLevel(TerminologyService.getSnapshot(), expressionWalker, 0, expNode);

        return exp;
    }

    private static void traverseRelDestLevel(TerminologySnapshotDI tSnap, TreeWalker walker, int level,
            ConceptNode origin, ConceptVersionBI typeCv)
            throws IOException, PropertyVetoException {
        Node parent = walker.getCurrentNode();

        // traverse children:
        for (Node destinationDomNode = walker.firstChild(); destinationDomNode != null;
                destinationDomNode = walker.nextSibling()) {
            ConceptVersionBI descCv;
            
            if (destinationDomNode.getAttributes().getNamedItem("sctid") != null) {

                String sctId = destinationDomNode.getAttributes().getNamedItem("sctid").getNodeValue();

                descCv = tSnap.getConceptVersionFromAlternateId(sctId);
            } else {
                String uuid = destinationDomNode.getAttributes().getNamedItem("uuid").getNodeValue();
                descCv = TerminologyService.getSnapshot().getConceptVersion(UUID.fromString(uuid));
            }
                if (descCv != null) {
                    ConceptNode destNode = new ConceptNode();
                    destNode.setValue(descCv);
                    origin.addRel(typeCv, destNode);
                    traverseRelTypeLevel(tSnap, walker, level + 1, destNode);
                }
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
            ConceptVersionBI typeCv;
            if (typeDomNode.getAttributes().getNamedItem("sctid") != null) {
                String sctId = typeDomNode.getAttributes().getNamedItem("sctid").getNodeValue();
                typeCv = tSnap.getConceptVersionFromAlternateId(sctId);
            } else {
                String uuid = typeDomNode.getAttributes().getNamedItem("typeUuid").getNodeValue();
                typeCv = TerminologyService.getSnapshot().getConceptVersion(UUID.fromString(uuid));
            }
 
            traverseRelDestLevel(tSnap, walker, level + 1, expNode, typeCv);
        }

        // return position to the current (level up):
        walker.setCurrentNode(parent);
    }
}
