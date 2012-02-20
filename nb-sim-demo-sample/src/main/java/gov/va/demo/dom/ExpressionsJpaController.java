/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.dom;

import gov.va.demo.dom.exceptions.IllegalOrphanException;
import gov.va.demo.dom.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import gov.va.demo.nb.sim.jpa.Assertions;
import gov.va.demo.nb.sim.jpa.Expressions;
import java.util.ArrayList;
import java.util.Collection;
import gov.va.demo.nb.sim.jpa.PncsLegoMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author kec
 */
public class ExpressionsJpaController implements Serializable {

    public ExpressionsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Expressions expressions) {
        if (expressions.getAssertionsCollection() == null) {
            expressions.setAssertionsCollection(new ArrayList<Assertions>());
        }
        if (expressions.getAssertionsCollection1() == null) {
            expressions.setAssertionsCollection1(new ArrayList<Assertions>());
        }
        if (expressions.getAssertionsCollection2() == null) {
            expressions.setAssertionsCollection2(new ArrayList<Assertions>());
        }
        if (expressions.getAssertionsCollection3() == null) {
            expressions.setAssertionsCollection3(new ArrayList<Assertions>());
        }
        if (expressions.getPncsLegoMapCollection() == null) {
            expressions.setPncsLegoMapCollection(new ArrayList<PncsLegoMap>());
        }
        if (expressions.getPncsLegoMapCollection1() == null) {
            expressions.setPncsLegoMapCollection1(new ArrayList<PncsLegoMap>());
        }
        if (expressions.getPncsLegoMapCollection2() == null) {
            expressions.setPncsLegoMapCollection2(new ArrayList<PncsLegoMap>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Assertions> attachedAssertionsCollection = new ArrayList<Assertions>();
            for (Assertions assertionsCollectionAssertionsToAttach : expressions.getAssertionsCollection()) {
                assertionsCollectionAssertionsToAttach = em.getReference(assertionsCollectionAssertionsToAttach.getClass(), assertionsCollectionAssertionsToAttach.getAuuid());
                attachedAssertionsCollection.add(assertionsCollectionAssertionsToAttach);
            }
            expressions.setAssertionsCollection(attachedAssertionsCollection);
            Collection<Assertions> attachedAssertionsCollection1 = new ArrayList<Assertions>();
            for (Assertions assertionsCollection1AssertionsToAttach : expressions.getAssertionsCollection1()) {
                assertionsCollection1AssertionsToAttach = em.getReference(assertionsCollection1AssertionsToAttach.getClass(), assertionsCollection1AssertionsToAttach.getAuuid());
                attachedAssertionsCollection1.add(assertionsCollection1AssertionsToAttach);
            }
            expressions.setAssertionsCollection1(attachedAssertionsCollection1);
            Collection<Assertions> attachedAssertionsCollection2 = new ArrayList<Assertions>();
            for (Assertions assertionsCollection2AssertionsToAttach : expressions.getAssertionsCollection2()) {
                assertionsCollection2AssertionsToAttach = em.getReference(assertionsCollection2AssertionsToAttach.getClass(), assertionsCollection2AssertionsToAttach.getAuuid());
                attachedAssertionsCollection2.add(assertionsCollection2AssertionsToAttach);
            }
            expressions.setAssertionsCollection2(attachedAssertionsCollection2);
            Collection<Assertions> attachedAssertionsCollection3 = new ArrayList<Assertions>();
            for (Assertions assertionsCollection3AssertionsToAttach : expressions.getAssertionsCollection3()) {
                assertionsCollection3AssertionsToAttach = em.getReference(assertionsCollection3AssertionsToAttach.getClass(), assertionsCollection3AssertionsToAttach.getAuuid());
                attachedAssertionsCollection3.add(assertionsCollection3AssertionsToAttach);
            }
            expressions.setAssertionsCollection3(attachedAssertionsCollection3);
            Collection<PncsLegoMap> attachedPncsLegoMapCollection = new ArrayList<PncsLegoMap>();
            for (PncsLegoMap pncsLegoMapCollectionPncsLegoMapToAttach : expressions.getPncsLegoMapCollection()) {
                pncsLegoMapCollectionPncsLegoMapToAttach = em.getReference(pncsLegoMapCollectionPncsLegoMapToAttach.getClass(), pncsLegoMapCollectionPncsLegoMapToAttach.getMapId());
                attachedPncsLegoMapCollection.add(pncsLegoMapCollectionPncsLegoMapToAttach);
            }
            expressions.setPncsLegoMapCollection(attachedPncsLegoMapCollection);
            Collection<PncsLegoMap> attachedPncsLegoMapCollection1 = new ArrayList<PncsLegoMap>();
            for (PncsLegoMap pncsLegoMapCollection1PncsLegoMapToAttach : expressions.getPncsLegoMapCollection1()) {
                pncsLegoMapCollection1PncsLegoMapToAttach = em.getReference(pncsLegoMapCollection1PncsLegoMapToAttach.getClass(), pncsLegoMapCollection1PncsLegoMapToAttach.getMapId());
                attachedPncsLegoMapCollection1.add(pncsLegoMapCollection1PncsLegoMapToAttach);
            }
            expressions.setPncsLegoMapCollection1(attachedPncsLegoMapCollection1);
            Collection<PncsLegoMap> attachedPncsLegoMapCollection2 = new ArrayList<PncsLegoMap>();
            for (PncsLegoMap pncsLegoMapCollection2PncsLegoMapToAttach : expressions.getPncsLegoMapCollection2()) {
                pncsLegoMapCollection2PncsLegoMapToAttach = em.getReference(pncsLegoMapCollection2PncsLegoMapToAttach.getClass(), pncsLegoMapCollection2PncsLegoMapToAttach.getMapId());
                attachedPncsLegoMapCollection2.add(pncsLegoMapCollection2PncsLegoMapToAttach);
            }
            expressions.setPncsLegoMapCollection2(attachedPncsLegoMapCollection2);
            em.persist(expressions);
            for (Assertions assertionsCollectionAssertions : expressions.getAssertionsCollection()) {
                Expressions oldValueEnidOfAssertionsCollectionAssertions = assertionsCollectionAssertions.getValueEnid();
                assertionsCollectionAssertions.setValueEnid(expressions);
                assertionsCollectionAssertions = em.merge(assertionsCollectionAssertions);
                if (oldValueEnidOfAssertionsCollectionAssertions != null) {
                    oldValueEnidOfAssertionsCollectionAssertions.getAssertionsCollection().remove(assertionsCollectionAssertions);
                    oldValueEnidOfAssertionsCollectionAssertions = em.merge(oldValueEnidOfAssertionsCollectionAssertions);
                }
            }
            for (Assertions assertionsCollection1Assertions : expressions.getAssertionsCollection1()) {
                Expressions oldQualifierEnidOfAssertionsCollection1Assertions = assertionsCollection1Assertions.getQualifierEnid();
                assertionsCollection1Assertions.setQualifierEnid(expressions);
                assertionsCollection1Assertions = em.merge(assertionsCollection1Assertions);
                if (oldQualifierEnidOfAssertionsCollection1Assertions != null) {
                    oldQualifierEnidOfAssertionsCollection1Assertions.getAssertionsCollection1().remove(assertionsCollection1Assertions);
                    oldQualifierEnidOfAssertionsCollection1Assertions = em.merge(oldQualifierEnidOfAssertionsCollection1Assertions);
                }
            }
            for (Assertions assertionsCollection2Assertions : expressions.getAssertionsCollection2()) {
                Expressions oldDiscernableEnidOfAssertionsCollection2Assertions = assertionsCollection2Assertions.getDiscernableEnid();
                assertionsCollection2Assertions.setDiscernableEnid(expressions);
                assertionsCollection2Assertions = em.merge(assertionsCollection2Assertions);
                if (oldDiscernableEnidOfAssertionsCollection2Assertions != null) {
                    oldDiscernableEnidOfAssertionsCollection2Assertions.getAssertionsCollection2().remove(assertionsCollection2Assertions);
                    oldDiscernableEnidOfAssertionsCollection2Assertions = em.merge(oldDiscernableEnidOfAssertionsCollection2Assertions);
                }
            }
            for (Assertions assertionsCollection3Assertions : expressions.getAssertionsCollection3()) {
                Expressions oldSectionEnidOfAssertionsCollection3Assertions = assertionsCollection3Assertions.getSectionEnid();
                assertionsCollection3Assertions.setSectionEnid(expressions);
                assertionsCollection3Assertions = em.merge(assertionsCollection3Assertions);
                if (oldSectionEnidOfAssertionsCollection3Assertions != null) {
                    oldSectionEnidOfAssertionsCollection3Assertions.getAssertionsCollection3().remove(assertionsCollection3Assertions);
                    oldSectionEnidOfAssertionsCollection3Assertions = em.merge(oldSectionEnidOfAssertionsCollection3Assertions);
                }
            }
            for (PncsLegoMap pncsLegoMapCollectionPncsLegoMap : expressions.getPncsLegoMapCollection()) {
                Expressions oldDiscernableEnidOfPncsLegoMapCollectionPncsLegoMap = pncsLegoMapCollectionPncsLegoMap.getDiscernableEnid();
                pncsLegoMapCollectionPncsLegoMap.setDiscernableEnid(expressions);
                pncsLegoMapCollectionPncsLegoMap = em.merge(pncsLegoMapCollectionPncsLegoMap);
                if (oldDiscernableEnidOfPncsLegoMapCollectionPncsLegoMap != null) {
                    oldDiscernableEnidOfPncsLegoMapCollectionPncsLegoMap.getPncsLegoMapCollection().remove(pncsLegoMapCollectionPncsLegoMap);
                    oldDiscernableEnidOfPncsLegoMapCollectionPncsLegoMap = em.merge(oldDiscernableEnidOfPncsLegoMapCollectionPncsLegoMap);
                }
            }
            for (PncsLegoMap pncsLegoMapCollection1PncsLegoMap : expressions.getPncsLegoMapCollection1()) {
                Expressions oldQualifierEnidOfPncsLegoMapCollection1PncsLegoMap = pncsLegoMapCollection1PncsLegoMap.getQualifierEnid();
                pncsLegoMapCollection1PncsLegoMap.setQualifierEnid(expressions);
                pncsLegoMapCollection1PncsLegoMap = em.merge(pncsLegoMapCollection1PncsLegoMap);
                if (oldQualifierEnidOfPncsLegoMapCollection1PncsLegoMap != null) {
                    oldQualifierEnidOfPncsLegoMapCollection1PncsLegoMap.getPncsLegoMapCollection1().remove(pncsLegoMapCollection1PncsLegoMap);
                    oldQualifierEnidOfPncsLegoMapCollection1PncsLegoMap = em.merge(oldQualifierEnidOfPncsLegoMapCollection1PncsLegoMap);
                }
            }
            for (PncsLegoMap pncsLegoMapCollection2PncsLegoMap : expressions.getPncsLegoMapCollection2()) {
                Expressions oldValueEnidOfPncsLegoMapCollection2PncsLegoMap = pncsLegoMapCollection2PncsLegoMap.getValueEnid();
                pncsLegoMapCollection2PncsLegoMap.setValueEnid(expressions);
                pncsLegoMapCollection2PncsLegoMap = em.merge(pncsLegoMapCollection2PncsLegoMap);
                if (oldValueEnidOfPncsLegoMapCollection2PncsLegoMap != null) {
                    oldValueEnidOfPncsLegoMapCollection2PncsLegoMap.getPncsLegoMapCollection2().remove(pncsLegoMapCollection2PncsLegoMap);
                    oldValueEnidOfPncsLegoMapCollection2PncsLegoMap = em.merge(oldValueEnidOfPncsLegoMapCollection2PncsLegoMap);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Expressions expressions) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Expressions persistentExpressions = em.find(Expressions.class, expressions.getEnid());
            Collection<Assertions> assertionsCollectionOld = persistentExpressions.getAssertionsCollection();
            Collection<Assertions> assertionsCollectionNew = expressions.getAssertionsCollection();
            Collection<Assertions> assertionsCollection1Old = persistentExpressions.getAssertionsCollection1();
            Collection<Assertions> assertionsCollection1New = expressions.getAssertionsCollection1();
            Collection<Assertions> assertionsCollection2Old = persistentExpressions.getAssertionsCollection2();
            Collection<Assertions> assertionsCollection2New = expressions.getAssertionsCollection2();
            Collection<Assertions> assertionsCollection3Old = persistentExpressions.getAssertionsCollection3();
            Collection<Assertions> assertionsCollection3New = expressions.getAssertionsCollection3();
            Collection<PncsLegoMap> pncsLegoMapCollectionOld = persistentExpressions.getPncsLegoMapCollection();
            Collection<PncsLegoMap> pncsLegoMapCollectionNew = expressions.getPncsLegoMapCollection();
            Collection<PncsLegoMap> pncsLegoMapCollection1Old = persistentExpressions.getPncsLegoMapCollection1();
            Collection<PncsLegoMap> pncsLegoMapCollection1New = expressions.getPncsLegoMapCollection1();
            Collection<PncsLegoMap> pncsLegoMapCollection2Old = persistentExpressions.getPncsLegoMapCollection2();
            Collection<PncsLegoMap> pncsLegoMapCollection2New = expressions.getPncsLegoMapCollection2();
            List<String> illegalOrphanMessages = null;
            for (Assertions assertionsCollectionOldAssertions : assertionsCollectionOld) {
                if (!assertionsCollectionNew.contains(assertionsCollectionOldAssertions)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Assertions " + assertionsCollectionOldAssertions + " since its valueEnid field is not nullable.");
                }
            }
            for (Assertions assertionsCollection1OldAssertions : assertionsCollection1Old) {
                if (!assertionsCollection1New.contains(assertionsCollection1OldAssertions)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Assertions " + assertionsCollection1OldAssertions + " since its qualifierEnid field is not nullable.");
                }
            }
            for (Assertions assertionsCollection2OldAssertions : assertionsCollection2Old) {
                if (!assertionsCollection2New.contains(assertionsCollection2OldAssertions)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Assertions " + assertionsCollection2OldAssertions + " since its discernableEnid field is not nullable.");
                }
            }
            for (Assertions assertionsCollection3OldAssertions : assertionsCollection3Old) {
                if (!assertionsCollection3New.contains(assertionsCollection3OldAssertions)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Assertions " + assertionsCollection3OldAssertions + " since its sectionEnid field is not nullable.");
                }
            }
            for (PncsLegoMap pncsLegoMapCollectionOldPncsLegoMap : pncsLegoMapCollectionOld) {
                if (!pncsLegoMapCollectionNew.contains(pncsLegoMapCollectionOldPncsLegoMap)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PncsLegoMap " + pncsLegoMapCollectionOldPncsLegoMap + " since its discernableEnid field is not nullable.");
                }
            }
            for (PncsLegoMap pncsLegoMapCollection1OldPncsLegoMap : pncsLegoMapCollection1Old) {
                if (!pncsLegoMapCollection1New.contains(pncsLegoMapCollection1OldPncsLegoMap)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PncsLegoMap " + pncsLegoMapCollection1OldPncsLegoMap + " since its qualifierEnid field is not nullable.");
                }
            }
            for (PncsLegoMap pncsLegoMapCollection2OldPncsLegoMap : pncsLegoMapCollection2Old) {
                if (!pncsLegoMapCollection2New.contains(pncsLegoMapCollection2OldPncsLegoMap)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PncsLegoMap " + pncsLegoMapCollection2OldPncsLegoMap + " since its valueEnid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Assertions> attachedAssertionsCollectionNew = new ArrayList<Assertions>();
            for (Assertions assertionsCollectionNewAssertionsToAttach : assertionsCollectionNew) {
                assertionsCollectionNewAssertionsToAttach = em.getReference(assertionsCollectionNewAssertionsToAttach.getClass(), assertionsCollectionNewAssertionsToAttach.getAuuid());
                attachedAssertionsCollectionNew.add(assertionsCollectionNewAssertionsToAttach);
            }
            assertionsCollectionNew = attachedAssertionsCollectionNew;
            expressions.setAssertionsCollection(assertionsCollectionNew);
            Collection<Assertions> attachedAssertionsCollection1New = new ArrayList<Assertions>();
            for (Assertions assertionsCollection1NewAssertionsToAttach : assertionsCollection1New) {
                assertionsCollection1NewAssertionsToAttach = em.getReference(assertionsCollection1NewAssertionsToAttach.getClass(), assertionsCollection1NewAssertionsToAttach.getAuuid());
                attachedAssertionsCollection1New.add(assertionsCollection1NewAssertionsToAttach);
            }
            assertionsCollection1New = attachedAssertionsCollection1New;
            expressions.setAssertionsCollection1(assertionsCollection1New);
            Collection<Assertions> attachedAssertionsCollection2New = new ArrayList<Assertions>();
            for (Assertions assertionsCollection2NewAssertionsToAttach : assertionsCollection2New) {
                assertionsCollection2NewAssertionsToAttach = em.getReference(assertionsCollection2NewAssertionsToAttach.getClass(), assertionsCollection2NewAssertionsToAttach.getAuuid());
                attachedAssertionsCollection2New.add(assertionsCollection2NewAssertionsToAttach);
            }
            assertionsCollection2New = attachedAssertionsCollection2New;
            expressions.setAssertionsCollection2(assertionsCollection2New);
            Collection<Assertions> attachedAssertionsCollection3New = new ArrayList<Assertions>();
            for (Assertions assertionsCollection3NewAssertionsToAttach : assertionsCollection3New) {
                assertionsCollection3NewAssertionsToAttach = em.getReference(assertionsCollection3NewAssertionsToAttach.getClass(), assertionsCollection3NewAssertionsToAttach.getAuuid());
                attachedAssertionsCollection3New.add(assertionsCollection3NewAssertionsToAttach);
            }
            assertionsCollection3New = attachedAssertionsCollection3New;
            expressions.setAssertionsCollection3(assertionsCollection3New);
            Collection<PncsLegoMap> attachedPncsLegoMapCollectionNew = new ArrayList<PncsLegoMap>();
            for (PncsLegoMap pncsLegoMapCollectionNewPncsLegoMapToAttach : pncsLegoMapCollectionNew) {
                pncsLegoMapCollectionNewPncsLegoMapToAttach = em.getReference(pncsLegoMapCollectionNewPncsLegoMapToAttach.getClass(), pncsLegoMapCollectionNewPncsLegoMapToAttach.getMapId());
                attachedPncsLegoMapCollectionNew.add(pncsLegoMapCollectionNewPncsLegoMapToAttach);
            }
            pncsLegoMapCollectionNew = attachedPncsLegoMapCollectionNew;
            expressions.setPncsLegoMapCollection(pncsLegoMapCollectionNew);
            Collection<PncsLegoMap> attachedPncsLegoMapCollection1New = new ArrayList<PncsLegoMap>();
            for (PncsLegoMap pncsLegoMapCollection1NewPncsLegoMapToAttach : pncsLegoMapCollection1New) {
                pncsLegoMapCollection1NewPncsLegoMapToAttach = em.getReference(pncsLegoMapCollection1NewPncsLegoMapToAttach.getClass(), pncsLegoMapCollection1NewPncsLegoMapToAttach.getMapId());
                attachedPncsLegoMapCollection1New.add(pncsLegoMapCollection1NewPncsLegoMapToAttach);
            }
            pncsLegoMapCollection1New = attachedPncsLegoMapCollection1New;
            expressions.setPncsLegoMapCollection1(pncsLegoMapCollection1New);
            Collection<PncsLegoMap> attachedPncsLegoMapCollection2New = new ArrayList<PncsLegoMap>();
            for (PncsLegoMap pncsLegoMapCollection2NewPncsLegoMapToAttach : pncsLegoMapCollection2New) {
                pncsLegoMapCollection2NewPncsLegoMapToAttach = em.getReference(pncsLegoMapCollection2NewPncsLegoMapToAttach.getClass(), pncsLegoMapCollection2NewPncsLegoMapToAttach.getMapId());
                attachedPncsLegoMapCollection2New.add(pncsLegoMapCollection2NewPncsLegoMapToAttach);
            }
            pncsLegoMapCollection2New = attachedPncsLegoMapCollection2New;
            expressions.setPncsLegoMapCollection2(pncsLegoMapCollection2New);
            expressions = em.merge(expressions);
            for (Assertions assertionsCollectionNewAssertions : assertionsCollectionNew) {
                if (!assertionsCollectionOld.contains(assertionsCollectionNewAssertions)) {
                    Expressions oldValueEnidOfAssertionsCollectionNewAssertions = assertionsCollectionNewAssertions.getValueEnid();
                    assertionsCollectionNewAssertions.setValueEnid(expressions);
                    assertionsCollectionNewAssertions = em.merge(assertionsCollectionNewAssertions);
                    if (oldValueEnidOfAssertionsCollectionNewAssertions != null && !oldValueEnidOfAssertionsCollectionNewAssertions.equals(expressions)) {
                        oldValueEnidOfAssertionsCollectionNewAssertions.getAssertionsCollection().remove(assertionsCollectionNewAssertions);
                        oldValueEnidOfAssertionsCollectionNewAssertions = em.merge(oldValueEnidOfAssertionsCollectionNewAssertions);
                    }
                }
            }
            for (Assertions assertionsCollection1NewAssertions : assertionsCollection1New) {
                if (!assertionsCollection1Old.contains(assertionsCollection1NewAssertions)) {
                    Expressions oldQualifierEnidOfAssertionsCollection1NewAssertions = assertionsCollection1NewAssertions.getQualifierEnid();
                    assertionsCollection1NewAssertions.setQualifierEnid(expressions);
                    assertionsCollection1NewAssertions = em.merge(assertionsCollection1NewAssertions);
                    if (oldQualifierEnidOfAssertionsCollection1NewAssertions != null && !oldQualifierEnidOfAssertionsCollection1NewAssertions.equals(expressions)) {
                        oldQualifierEnidOfAssertionsCollection1NewAssertions.getAssertionsCollection1().remove(assertionsCollection1NewAssertions);
                        oldQualifierEnidOfAssertionsCollection1NewAssertions = em.merge(oldQualifierEnidOfAssertionsCollection1NewAssertions);
                    }
                }
            }
            for (Assertions assertionsCollection2NewAssertions : assertionsCollection2New) {
                if (!assertionsCollection2Old.contains(assertionsCollection2NewAssertions)) {
                    Expressions oldDiscernableEnidOfAssertionsCollection2NewAssertions = assertionsCollection2NewAssertions.getDiscernableEnid();
                    assertionsCollection2NewAssertions.setDiscernableEnid(expressions);
                    assertionsCollection2NewAssertions = em.merge(assertionsCollection2NewAssertions);
                    if (oldDiscernableEnidOfAssertionsCollection2NewAssertions != null && !oldDiscernableEnidOfAssertionsCollection2NewAssertions.equals(expressions)) {
                        oldDiscernableEnidOfAssertionsCollection2NewAssertions.getAssertionsCollection2().remove(assertionsCollection2NewAssertions);
                        oldDiscernableEnidOfAssertionsCollection2NewAssertions = em.merge(oldDiscernableEnidOfAssertionsCollection2NewAssertions);
                    }
                }
            }
            for (Assertions assertionsCollection3NewAssertions : assertionsCollection3New) {
                if (!assertionsCollection3Old.contains(assertionsCollection3NewAssertions)) {
                    Expressions oldSectionEnidOfAssertionsCollection3NewAssertions = assertionsCollection3NewAssertions.getSectionEnid();
                    assertionsCollection3NewAssertions.setSectionEnid(expressions);
                    assertionsCollection3NewAssertions = em.merge(assertionsCollection3NewAssertions);
                    if (oldSectionEnidOfAssertionsCollection3NewAssertions != null && !oldSectionEnidOfAssertionsCollection3NewAssertions.equals(expressions)) {
                        oldSectionEnidOfAssertionsCollection3NewAssertions.getAssertionsCollection3().remove(assertionsCollection3NewAssertions);
                        oldSectionEnidOfAssertionsCollection3NewAssertions = em.merge(oldSectionEnidOfAssertionsCollection3NewAssertions);
                    }
                }
            }
            for (PncsLegoMap pncsLegoMapCollectionNewPncsLegoMap : pncsLegoMapCollectionNew) {
                if (!pncsLegoMapCollectionOld.contains(pncsLegoMapCollectionNewPncsLegoMap)) {
                    Expressions oldDiscernableEnidOfPncsLegoMapCollectionNewPncsLegoMap = pncsLegoMapCollectionNewPncsLegoMap.getDiscernableEnid();
                    pncsLegoMapCollectionNewPncsLegoMap.setDiscernableEnid(expressions);
                    pncsLegoMapCollectionNewPncsLegoMap = em.merge(pncsLegoMapCollectionNewPncsLegoMap);
                    if (oldDiscernableEnidOfPncsLegoMapCollectionNewPncsLegoMap != null && !oldDiscernableEnidOfPncsLegoMapCollectionNewPncsLegoMap.equals(expressions)) {
                        oldDiscernableEnidOfPncsLegoMapCollectionNewPncsLegoMap.getPncsLegoMapCollection().remove(pncsLegoMapCollectionNewPncsLegoMap);
                        oldDiscernableEnidOfPncsLegoMapCollectionNewPncsLegoMap = em.merge(oldDiscernableEnidOfPncsLegoMapCollectionNewPncsLegoMap);
                    }
                }
            }
            for (PncsLegoMap pncsLegoMapCollection1NewPncsLegoMap : pncsLegoMapCollection1New) {
                if (!pncsLegoMapCollection1Old.contains(pncsLegoMapCollection1NewPncsLegoMap)) {
                    Expressions oldQualifierEnidOfPncsLegoMapCollection1NewPncsLegoMap = pncsLegoMapCollection1NewPncsLegoMap.getQualifierEnid();
                    pncsLegoMapCollection1NewPncsLegoMap.setQualifierEnid(expressions);
                    pncsLegoMapCollection1NewPncsLegoMap = em.merge(pncsLegoMapCollection1NewPncsLegoMap);
                    if (oldQualifierEnidOfPncsLegoMapCollection1NewPncsLegoMap != null && !oldQualifierEnidOfPncsLegoMapCollection1NewPncsLegoMap.equals(expressions)) {
                        oldQualifierEnidOfPncsLegoMapCollection1NewPncsLegoMap.getPncsLegoMapCollection1().remove(pncsLegoMapCollection1NewPncsLegoMap);
                        oldQualifierEnidOfPncsLegoMapCollection1NewPncsLegoMap = em.merge(oldQualifierEnidOfPncsLegoMapCollection1NewPncsLegoMap);
                    }
                }
            }
            for (PncsLegoMap pncsLegoMapCollection2NewPncsLegoMap : pncsLegoMapCollection2New) {
                if (!pncsLegoMapCollection2Old.contains(pncsLegoMapCollection2NewPncsLegoMap)) {
                    Expressions oldValueEnidOfPncsLegoMapCollection2NewPncsLegoMap = pncsLegoMapCollection2NewPncsLegoMap.getValueEnid();
                    pncsLegoMapCollection2NewPncsLegoMap.setValueEnid(expressions);
                    pncsLegoMapCollection2NewPncsLegoMap = em.merge(pncsLegoMapCollection2NewPncsLegoMap);
                    if (oldValueEnidOfPncsLegoMapCollection2NewPncsLegoMap != null && !oldValueEnidOfPncsLegoMapCollection2NewPncsLegoMap.equals(expressions)) {
                        oldValueEnidOfPncsLegoMapCollection2NewPncsLegoMap.getPncsLegoMapCollection2().remove(pncsLegoMapCollection2NewPncsLegoMap);
                        oldValueEnidOfPncsLegoMapCollection2NewPncsLegoMap = em.merge(oldValueEnidOfPncsLegoMapCollection2NewPncsLegoMap);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = expressions.getEnid();
                if (findExpressions(id) == null) {
                    throw new NonexistentEntityException("The expressions with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Expressions expressions;
            try {
                expressions = em.getReference(Expressions.class, id);
                expressions.getEnid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The expressions with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Assertions> assertionsCollectionOrphanCheck = expressions.getAssertionsCollection();
            for (Assertions assertionsCollectionOrphanCheckAssertions : assertionsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Expressions (" + expressions + ") cannot be destroyed since the Assertions " + assertionsCollectionOrphanCheckAssertions + " in its assertionsCollection field has a non-nullable valueEnid field.");
            }
            Collection<Assertions> assertionsCollection1OrphanCheck = expressions.getAssertionsCollection1();
            for (Assertions assertionsCollection1OrphanCheckAssertions : assertionsCollection1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Expressions (" + expressions + ") cannot be destroyed since the Assertions " + assertionsCollection1OrphanCheckAssertions + " in its assertionsCollection1 field has a non-nullable qualifierEnid field.");
            }
            Collection<Assertions> assertionsCollection2OrphanCheck = expressions.getAssertionsCollection2();
            for (Assertions assertionsCollection2OrphanCheckAssertions : assertionsCollection2OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Expressions (" + expressions + ") cannot be destroyed since the Assertions " + assertionsCollection2OrphanCheckAssertions + " in its assertionsCollection2 field has a non-nullable discernableEnid field.");
            }
            Collection<Assertions> assertionsCollection3OrphanCheck = expressions.getAssertionsCollection3();
            for (Assertions assertionsCollection3OrphanCheckAssertions : assertionsCollection3OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Expressions (" + expressions + ") cannot be destroyed since the Assertions " + assertionsCollection3OrphanCheckAssertions + " in its assertionsCollection3 field has a non-nullable sectionEnid field.");
            }
            Collection<PncsLegoMap> pncsLegoMapCollectionOrphanCheck = expressions.getPncsLegoMapCollection();
            for (PncsLegoMap pncsLegoMapCollectionOrphanCheckPncsLegoMap : pncsLegoMapCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Expressions (" + expressions + ") cannot be destroyed since the PncsLegoMap " + pncsLegoMapCollectionOrphanCheckPncsLegoMap + " in its pncsLegoMapCollection field has a non-nullable discernableEnid field.");
            }
            Collection<PncsLegoMap> pncsLegoMapCollection1OrphanCheck = expressions.getPncsLegoMapCollection1();
            for (PncsLegoMap pncsLegoMapCollection1OrphanCheckPncsLegoMap : pncsLegoMapCollection1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Expressions (" + expressions + ") cannot be destroyed since the PncsLegoMap " + pncsLegoMapCollection1OrphanCheckPncsLegoMap + " in its pncsLegoMapCollection1 field has a non-nullable qualifierEnid field.");
            }
            Collection<PncsLegoMap> pncsLegoMapCollection2OrphanCheck = expressions.getPncsLegoMapCollection2();
            for (PncsLegoMap pncsLegoMapCollection2OrphanCheckPncsLegoMap : pncsLegoMapCollection2OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Expressions (" + expressions + ") cannot be destroyed since the PncsLegoMap " + pncsLegoMapCollection2OrphanCheckPncsLegoMap + " in its pncsLegoMapCollection2 field has a non-nullable valueEnid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(expressions);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Expressions> findExpressionsEntities() {
        return findExpressionsEntities(true, -1, -1);
    }

    public List<Expressions> findExpressionsEntities(int maxResults, int firstResult) {
        return findExpressionsEntities(false, maxResults, firstResult);
    }

    private List<Expressions> findExpressionsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Expressions.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Expressions findExpressions(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Expressions.class, id);
        } finally {
            em.close();
        }
    }

    public int getExpressionsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Expressions> rt = cq.from(Expressions.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
