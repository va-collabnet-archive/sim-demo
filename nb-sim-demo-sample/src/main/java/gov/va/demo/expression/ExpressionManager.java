
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package gov.va.demo.expression;

//~--- non-JDK imports --------------------------------------------------------

import gov.va.demo.terminology.TerminologyService;
import gov.va.sim.impl.expression.Expression;
import gov.va.sim.impl.expression.node.ConceptNode;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 *
 * @author kec
 */
public class ExpressionManager {
   public static int getCnid(Expression expression)
           throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException {
      if (expression.getFocus() == null) {
         return Integer.MAX_VALUE;
      }

      // existing concept in db. Just return it's nid.
      if (expression.getFocus().getAllRels().length == 0) {
         ConceptNode cn = (ConceptNode) expression.getFocus();

         return cn.getValue().getNid();
      }

      // Complex expression. Must analyze db, and possibly add new concept.
      // TODO For now, just get nid so we can write to the database. Revise to add concepts if needed.
      return TerminologyService.getStore().getNidForUuids(expression.getUuid());
   }

    public static UUID getElConceptUuid(Expression expression) throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException {
      if (expression.getFocus() == null) {
         return null;
      }

      // existing concept in db. Just return it's nid.
      if (expression.getFocus().getAllRels().length == 0) {
         ConceptNode cn = (ConceptNode) expression.getFocus();

         return cn.getValue().getPrimUuid();
      }
      return expression.getUuid();
    }
}
