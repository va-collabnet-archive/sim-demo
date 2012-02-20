/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.dom;

import gov.va.demo.dom.exceptions.NonexistentEntityException;
import gov.va.demo.dom.exceptions.PreexistingEntityException;
import gov.va.demo.nb.sim.jpa.Assertions;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import gov.va.demo.nb.sim.jpa.Intervals;
import gov.va.demo.nb.sim.jpa.Expressions;
import gov.va.demo.nb.sim.jpa.Documents;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author kec
 */
public class AssertionsJpaController implements Serializable {

    public AssertionsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Assertions assertions) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Intervals inid = assertions.getInid();
            if (inid != null) {
                inid = em.getReference(inid.getClass(), inid.getInid());
                assertions.setInid(inid);
            }
            Expressions valueEnid = assertions.getValueEnid();
            if (valueEnid != null) {
                valueEnid = em.getReference(valueEnid.getClass(), valueEnid.getEnid());
                assertions.setValueEnid(valueEnid);
            }
            Expressions qualifierEnid = assertions.getQualifierEnid();
            if (qualifierEnid != null) {
                qualifierEnid = em.getReference(qualifierEnid.getClass(), qualifierEnid.getEnid());
                assertions.setQualifierEnid(qualifierEnid);
            }
            Expressions discernableEnid = assertions.getDiscernableEnid();
            if (discernableEnid != null) {
                discernableEnid = em.getReference(discernableEnid.getClass(), discernableEnid.getEnid());
                assertions.setDiscernableEnid(discernableEnid);
            }
            Expressions sectionEnid = assertions.getSectionEnid();
            if (sectionEnid != null) {
                sectionEnid = em.getReference(sectionEnid.getClass(), sectionEnid.getEnid());
                assertions.setSectionEnid(sectionEnid);
            }
            Documents dnid = assertions.getDnid();
            if (dnid != null) {
                dnid = em.getReference(dnid.getClass(), dnid.getDnid());
                assertions.setDnid(dnid);
            }
            em.persist(assertions);
            if (inid != null) {
                inid.getAssertionsCollection().add(assertions);
                inid = em.merge(inid);
            }
            if (valueEnid != null) {
                valueEnid.getAssertionsCollection().add(assertions);
                valueEnid = em.merge(valueEnid);
            }
            if (qualifierEnid != null) {
                qualifierEnid.getAssertionsCollection().add(assertions);
                qualifierEnid = em.merge(qualifierEnid);
            }
            if (discernableEnid != null) {
                discernableEnid.getAssertionsCollection().add(assertions);
                discernableEnid = em.merge(discernableEnid);
            }
            if (sectionEnid != null) {
                sectionEnid.getAssertionsCollection().add(assertions);
                sectionEnid = em.merge(sectionEnid);
            }
            if (dnid != null) {
                dnid.getAssertionsCollection().add(assertions);
                dnid = em.merge(dnid);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findAssertions(assertions.getAuuid()) != null) {
                throw new PreexistingEntityException("Assertions " + assertions + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Assertions assertions) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Assertions persistentAssertions = em.find(Assertions.class, assertions.getAuuid());
            Intervals inidOld = persistentAssertions.getInid();
            Intervals inidNew = assertions.getInid();
            Expressions valueEnidOld = persistentAssertions.getValueEnid();
            Expressions valueEnidNew = assertions.getValueEnid();
            Expressions qualifierEnidOld = persistentAssertions.getQualifierEnid();
            Expressions qualifierEnidNew = assertions.getQualifierEnid();
            Expressions discernableEnidOld = persistentAssertions.getDiscernableEnid();
            Expressions discernableEnidNew = assertions.getDiscernableEnid();
            Expressions sectionEnidOld = persistentAssertions.getSectionEnid();
            Expressions sectionEnidNew = assertions.getSectionEnid();
            Documents dnidOld = persistentAssertions.getDnid();
            Documents dnidNew = assertions.getDnid();
            if (inidNew != null) {
                inidNew = em.getReference(inidNew.getClass(), inidNew.getInid());
                assertions.setInid(inidNew);
            }
            if (valueEnidNew != null) {
                valueEnidNew = em.getReference(valueEnidNew.getClass(), valueEnidNew.getEnid());
                assertions.setValueEnid(valueEnidNew);
            }
            if (qualifierEnidNew != null) {
                qualifierEnidNew = em.getReference(qualifierEnidNew.getClass(), qualifierEnidNew.getEnid());
                assertions.setQualifierEnid(qualifierEnidNew);
            }
            if (discernableEnidNew != null) {
                discernableEnidNew = em.getReference(discernableEnidNew.getClass(), discernableEnidNew.getEnid());
                assertions.setDiscernableEnid(discernableEnidNew);
            }
            if (sectionEnidNew != null) {
                sectionEnidNew = em.getReference(sectionEnidNew.getClass(), sectionEnidNew.getEnid());
                assertions.setSectionEnid(sectionEnidNew);
            }
            if (dnidNew != null) {
                dnidNew = em.getReference(dnidNew.getClass(), dnidNew.getDnid());
                assertions.setDnid(dnidNew);
            }
            assertions = em.merge(assertions);
            if (inidOld != null && !inidOld.equals(inidNew)) {
                inidOld.getAssertionsCollection().remove(assertions);
                inidOld = em.merge(inidOld);
            }
            if (inidNew != null && !inidNew.equals(inidOld)) {
                inidNew.getAssertionsCollection().add(assertions);
                inidNew = em.merge(inidNew);
            }
            if (valueEnidOld != null && !valueEnidOld.equals(valueEnidNew)) {
                valueEnidOld.getAssertionsCollection().remove(assertions);
                valueEnidOld = em.merge(valueEnidOld);
            }
            if (valueEnidNew != null && !valueEnidNew.equals(valueEnidOld)) {
                valueEnidNew.getAssertionsCollection().add(assertions);
                valueEnidNew = em.merge(valueEnidNew);
            }
            if (qualifierEnidOld != null && !qualifierEnidOld.equals(qualifierEnidNew)) {
                qualifierEnidOld.getAssertionsCollection().remove(assertions);
                qualifierEnidOld = em.merge(qualifierEnidOld);
            }
            if (qualifierEnidNew != null && !qualifierEnidNew.equals(qualifierEnidOld)) {
                qualifierEnidNew.getAssertionsCollection().add(assertions);
                qualifierEnidNew = em.merge(qualifierEnidNew);
            }
            if (discernableEnidOld != null && !discernableEnidOld.equals(discernableEnidNew)) {
                discernableEnidOld.getAssertionsCollection().remove(assertions);
                discernableEnidOld = em.merge(discernableEnidOld);
            }
            if (discernableEnidNew != null && !discernableEnidNew.equals(discernableEnidOld)) {
                discernableEnidNew.getAssertionsCollection().add(assertions);
                discernableEnidNew = em.merge(discernableEnidNew);
            }
            if (sectionEnidOld != null && !sectionEnidOld.equals(sectionEnidNew)) {
                sectionEnidOld.getAssertionsCollection().remove(assertions);
                sectionEnidOld = em.merge(sectionEnidOld);
            }
            if (sectionEnidNew != null && !sectionEnidNew.equals(sectionEnidOld)) {
                sectionEnidNew.getAssertionsCollection().add(assertions);
                sectionEnidNew = em.merge(sectionEnidNew);
            }
            if (dnidOld != null && !dnidOld.equals(dnidNew)) {
                dnidOld.getAssertionsCollection().remove(assertions);
                dnidOld = em.merge(dnidOld);
            }
            if (dnidNew != null && !dnidNew.equals(dnidOld)) {
                dnidNew.getAssertionsCollection().add(assertions);
                dnidNew = em.merge(dnidNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = assertions.getAuuid();
                if (findAssertions(id) == null) {
                    throw new NonexistentEntityException("The assertions with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Assertions assertions;
            try {
                assertions = em.getReference(Assertions.class, id);
                assertions.getAuuid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The assertions with id " + id + " no longer exists.", enfe);
            }
            Intervals inid = assertions.getInid();
            if (inid != null) {
                inid.getAssertionsCollection().remove(assertions);
                inid = em.merge(inid);
            }
            Expressions valueEnid = assertions.getValueEnid();
            if (valueEnid != null) {
                valueEnid.getAssertionsCollection().remove(assertions);
                valueEnid = em.merge(valueEnid);
            }
            Expressions qualifierEnid = assertions.getQualifierEnid();
            if (qualifierEnid != null) {
                qualifierEnid.getAssertionsCollection().remove(assertions);
                qualifierEnid = em.merge(qualifierEnid);
            }
            Expressions discernableEnid = assertions.getDiscernableEnid();
            if (discernableEnid != null) {
                discernableEnid.getAssertionsCollection().remove(assertions);
                discernableEnid = em.merge(discernableEnid);
            }
            Expressions sectionEnid = assertions.getSectionEnid();
            if (sectionEnid != null) {
                sectionEnid.getAssertionsCollection().remove(assertions);
                sectionEnid = em.merge(sectionEnid);
            }
            Documents dnid = assertions.getDnid();
            if (dnid != null) {
                dnid.getAssertionsCollection().remove(assertions);
                dnid = em.merge(dnid);
            }
            em.remove(assertions);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Assertions> findAssertionsEntities() {
        return findAssertionsEntities(true, -1, -1);
    }

    public List<Assertions> findAssertionsEntities(int maxResults, int firstResult) {
        return findAssertionsEntities(false, maxResults, firstResult);
    }

    private List<Assertions> findAssertionsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Assertions.class));
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

    public Assertions findAssertions(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Assertions.class, id);
        } finally {
            em.close();
        }
    }

    public int getAssertionsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Assertions> rt = cq.from(Assertions.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
