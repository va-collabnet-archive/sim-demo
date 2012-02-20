
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.expression;

//~--- non-JDK imports --------------------------------------------------------
import gov.va.demo.terminology.TerminologyService;
import gov.va.sim.act.expression.ExpressionRelBI;
import gov.va.sim.impl.expression.Expression;
import gov.va.sim.impl.expression.node.ConceptNode;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import org.ihtsdo.lang.LANG_CODE;
import org.ihtsdo.tk.api.blueprint.ConceptCB;
import org.ihtsdo.tk.api.blueprint.InvalidCAB;
import org.ihtsdo.tk.api.blueprint.RelCAB;
import org.ihtsdo.tk.api.concept.ConceptVersionBI;
import org.ihtsdo.tk.binding.Snomed;
import org.ihtsdo.tk.dto.concept.component.relationship.TkRelType;

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

        if (TerminologyService.getStore().hasUuid(expression.getUuid())) {
            return TerminologyService.getStore().getNidForUuids(expression.getUuid());
        }
        return Integer.MIN_VALUE;
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

    public static ConceptCB getBlueprint(Expression expression) throws InvalidCAB, Exception {
        ConceptCB blueprint = new ConceptCB(expression.getFocus().getFullySpecifiedText()
                + " exp " + expression.getUuid().toString(),
                expression.getFocus().getPreferredText()
                + " exp " + expression.getUuid().toString(),
                LANG_CODE.EN,
                Snomed.IS_A.getUuids()[0],
                ((ConceptVersionBI) expression.getFocus().getValue()).getPrimUuid());
        blueprint.setComponentUuid(expression.getUuid());
        for (ExpressionRelBI rel : expression.getFocus().getAllRels()) {
            RelCAB rcab = new RelCAB(blueprint.getComponentUuid(), rel.getType().getPrimUuid(),
                    ((ConceptVersionBI) rel.getDestination().getValue()).getPrimUuid(), 0, TkRelType.STATED_ROLE);
            blueprint.getRelCABs().add(rcab);
        }

        return blueprint;
    }
}
