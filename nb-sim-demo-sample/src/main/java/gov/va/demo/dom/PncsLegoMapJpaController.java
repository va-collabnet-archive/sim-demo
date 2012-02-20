/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.demo.dom;

import gov.va.demo.dom.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import gov.va.demo.nb.sim.jpa.Expressions;
import gov.va.demo.nb.sim.jpa.PncsLegoMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author kec
 */
public class PncsLegoMapJpaController implements Serializable {

    public PncsLegoMapJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PncsLegoMap pncsLegoMap) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Expressions discernableEnid = pncsLegoMap.getDiscernableEnid();
            if (discernableEnid != null) {
                discernableEnid = em.getReference(discernableEnid.getClass(), discernableEnid.getEnid());
                pncsLegoMap.setDiscernableEnid(discernableEnid);
            }
            Expressions qualifierEnid = pncsLegoMap.getQualifierEnid();
            if (qualifierEnid != null) {
                qualifierEnid = em.getReference(qualifierEnid.getClass(), qualifierEnid.getEnid());
                pncsLegoMap.setQualifierEnid(qualifierEnid);
            }
            Expressions valueEnid = pncsLegoMap.getValueEnid();
            if (valueEnid != null) {
                valueEnid = em.getReference(valueEnid.getClass(), valueEnid.getEnid());
                pncsLegoMap.setValueEnid(valueEnid);
            }
            em.persist(pncsLegoMap);
            if (discernableEnid != null) {
                discernableEnid.getPncsLegoMapCollection().add(pncsLegoMap);
                discernableEnid = em.merge(discernableEnid);
            }
            if (qualifierEnid != null) {
                qualifierEnid.getPncsLegoMapCollection().add(pncsLegoMap);
                qualifierEnid = em.merge(qualifierEnid);
            }
            if (valueEnid != null) {
                valueEnid.getPncsLegoMapCollection().add(pncsLegoMap);
                valueEnid = em.merge(valueEnid);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PncsLegoMap pncsLegoMap) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PncsLegoMap persistentPncsLegoMap = em.find(PncsLegoMap.class, pncsLegoMap.getMapId());
            Expressions discernableEnidOld = persistentPncsLegoMap.getDiscernableEnid();
            Expressions discernableEnidNew = pncsLegoMap.getDiscernableEnid();
            Expressions qualifierEnidOld = persistentPncsLegoMap.getQualifierEnid();
            Expressions qualifierEnidNew = pncsLegoMap.getQualifierEnid();
            Expressions valueEnidOld = persistentPncsLegoMap.getValueEnid();
            Expressions valueEnidNew = pncsLegoMap.getValueEnid();
            if (discernableEnidNew != null) {
                discernableEnidNew = em.getReference(discernableEnidNew.getClass(), discernableEnidNew.getEnid());
                pncsLegoMap.setDiscernableEnid(discernableEnidNew);
            }
            if (qualifierEnidNew != null) {
                qualifierEnidNew = em.getReference(qualifierEnidNew.getClass(), qualifierEnidNew.getEnid());
                pncsLegoMap.setQualifierEnid(qualifierEnidNew);
            }
            if (valueEnidNew != null) {
                valueEnidNew = em.getReference(valueEnidNew.getClass(), valueEnidNew.getEnid());
                pncsLegoMap.setValueEnid(valueEnidNew);
            }
            pncsLegoMap = em.merge(pncsLegoMap);
            if (discernableEnidOld != null && !discernableEnidOld.equals(discernableEnidNew)) {
                discernableEnidOld.getPncsLegoMapCollection().remove(pncsLegoMap);
                discernableEnidOld = em.merge(discernableEnidOld);
            }
            if (discernableEnidNew != null && !discernableEnidNew.equals(discernableEnidOld)) {
                discernableEnidNew.getPncsLegoMapCollection().add(pncsLegoMap);
                discernableEnidNew = em.merge(discernableEnidNew);
            }
            if (qualifierEnidOld != null && !qualifierEnidOld.equals(qualifierEnidNew)) {
                qualifierEnidOld.getPncsLegoMapCollection().remove(pncsLegoMap);
                qualifierEnidOld = em.merge(qualifierEnidOld);
            }
            if (qualifierEnidNew != null && !qualifierEnidNew.equals(qualifierEnidOld)) {
                qualifierEnidNew.getPncsLegoMapCollection().add(pncsLegoMap);
                qualifierEnidNew = em.merge(qualifierEnidNew);
            }
            if (valueEnidOld != null && !valueEnidOld.equals(valueEnidNew)) {
                valueEnidOld.getPncsLegoMapCollection().remove(pncsLegoMap);
                valueEnidOld = em.merge(valueEnidOld);
            }
            if (valueEnidNew != null && !valueEnidNew.equals(valueEnidOld)) {
                valueEnidNew.getPncsLegoMapCollection().add(pncsLegoMap);
                valueEnidNew = em.merge(valueEnidNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pncsLegoMap.getMapId();
                if (findPncsLegoMap(id) == null) {
                    throw new NonexistentEntityException("The pncsLegoMap with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PncsLegoMap pncsLegoMap;
            try {
                pncsLegoMap = em.getReference(PncsLegoMap.class, id);
                pncsLegoMap.getMapId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pncsLegoMap with id " + id + " no longer exists.", enfe);
            }
            Expressions discernableEnid = pncsLegoMap.getDiscernableEnid();
            if (discernableEnid != null) {
                discernableEnid.getPncsLegoMapCollection().remove(pncsLegoMap);
                discernableEnid = em.merge(discernableEnid);
            }
            Expressions qualifierEnid = pncsLegoMap.getQualifierEnid();
            if (qualifierEnid != null) {
                qualifierEnid.getPncsLegoMapCollection().remove(pncsLegoMap);
                qualifierEnid = em.merge(qualifierEnid);
            }
            Expressions valueEnid = pncsLegoMap.getValueEnid();
            if (valueEnid != null) {
                valueEnid.getPncsLegoMapCollection().remove(pncsLegoMap);
                valueEnid = em.merge(valueEnid);
            }
            em.remove(pncsLegoMap);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PncsLegoMap> findPncsLegoMapEntities() {
        return findPncsLegoMapEntities(true, -1, -1);
    }

    public List<PncsLegoMap> findPncsLegoMapEntities(int maxResults, int firstResult) {
        return findPncsLegoMapEntities(false, maxResults, firstResult);
    }

    private List<PncsLegoMap> findPncsLegoMapEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PncsLegoMap.class));
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

    public PncsLegoMap findPncsLegoMap(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PncsLegoMap.class, id);
        } finally {
            em.close();
        }
    }

    public int getPncsLegoMapCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PncsLegoMap> rt = cq.from(PncsLegoMap.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
