/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.dom;

import gov.va.demo.nb.sim.jpa.Expressions;
import gov.va.demo.nb.sim.jpa.JpaManager;
import gov.va.demo.nb.sim.jpa.PncsLegoMap;
import gov.va.sim.impl.expression.Expression;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

/**
 *
 * @author kec
 */
public class PncsLegoMapGenerator {

    private int pncsId;
    private String pncsValue;
    private EntityManager em;

    public void addToMap(Document assertionDoc) throws IOException, PropertyVetoException, NoSuchAlgorithmException {
        em = JpaManager.getEntityManager();

        int whatToShow = NodeFilter.SHOW_ELEMENT;
        NodeFilter filter = new ExpressionRootNodeFilter();
        boolean entityReferenceExpansion = false;
        TreeWalker walker = ((DocumentTraversal) assertionDoc).createTreeWalker(assertionDoc, whatToShow, filter,
                entityReferenceExpansion);
        Node domNode = walker.nextNode();
        PncsLegoMap map = null;
        EntityTransaction entr = null;
        while (domNode != null) {
            if (domNode.getNodeName().equals("pncs")) {
                pncsValue = domNode.getAttributes().getNamedItem("value").getNodeValue();
                pncsId = Integer.parseInt(domNode.getAttributes().getNamedItem("id").getNodeValue());
            } else if (domNode.getNodeName().equals("discernable")) {
                entr = em.getTransaction();
                entr.begin();
                map = new PncsLegoMap();
                map.setPncsId(pncsId);
                map.setPncsValue(pncsValue);
                Expressions dbExpression = processExpression(assertionDoc, domNode, whatToShow, entityReferenceExpansion);
                map.setDiscernableEnid(dbExpression);
            } else if (domNode.getNodeName().equals("qualifier")) {
                Expressions dbExpression = processExpression(assertionDoc, domNode, whatToShow, entityReferenceExpansion);
                map.setQualifierEnid(dbExpression);
            } else if (domNode.getNodeName().equals("value")) {
                Expressions dbExpression = processExpression(assertionDoc, domNode, whatToShow, entityReferenceExpansion);
                map.setValueEnid(dbExpression);
                em.persist(map);
                entr.commit();
                map = null;
            } 
            domNode = walker.nextNode();
        }
    }

    private Expressions processExpression(Document assertionDoc, Node domNode, int whatToShow, boolean entityReferenceExpansion) throws DOMException, IOException, PropertyVetoException, UnsupportedEncodingException, NoSuchAlgorithmException {
        for (int i = 0; i < domNode.getChildNodes().getLength(); i++) {
            Node child = domNode.getChildNodes().item(i);
            if (child.getNodeName().equals("concept")) {
                TreeWalker expressionWalker = ((DocumentTraversal) assertionDoc).createTreeWalker(child, whatToShow, null,
                        entityReferenceExpansion);
                Expression exp = DomExpressionAdapter.convertToExpression(expressionWalker);

                Query countEuuidQuery = em.createNamedQuery("Expressions.findByEuuid");
                countEuuidQuery.setParameter("euuid", exp.getUuid().toString());
                List obs = countEuuidQuery.getResultList();
                Expressions dbExpression = (Expressions) obs.get(0);

                return dbExpression;
            }
        }
        throw new IOException("No expression found: " + domNode);
    }

    public static class ExpressionRootNodeFilter implements NodeFilter {

        @Override
        public short acceptNode(Node n) {

            if (n.getNodeName().equals("pncs")) {
                return NodeFilter.FILTER_ACCEPT;
            }
            if (n.getNodeName().equals("discernable")) {
                return NodeFilter.FILTER_ACCEPT;
            }
            if (n.getNodeName().equals("qualifier")) {
                return NodeFilter.FILTER_ACCEPT;
            }
            if (n.getNodeName().equals("value")) {
                return NodeFilter.FILTER_ACCEPT;
            }

            if (n.getNodeName().equals("concept")) {
                return NodeFilter.FILTER_REJECT;
            }
            return NodeFilter.FILTER_SKIP;
        }
    }
}
