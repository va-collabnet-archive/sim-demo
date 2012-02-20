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
import gov.va.demo.nb.sim.jpa.Documents;
import java.util.ArrayList;
import java.util.Collection;
import gov.va.demo.nb.sim.jpa.Assertions;
import gov.va.demo.nb.sim.jpa.Intervals;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author kec
 */
public class IntervalsJpaController implements Serializable {

    public IntervalsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Intervals intervals) {
        if (intervals.getDocumentsCollection() == null) {
            intervals.setDocumentsCollection(new ArrayList<Documents>());
        }
        if (intervals.getAssertionsCollection() == null) {
            intervals.setAssertionsCollection(new ArrayList<Assertions>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Documents> attachedDocumentsCollection = new ArrayList<Documents>();
            for (Documents documentsCollectionDocumentsToAttach : intervals.getDocumentsCollection()) {
                documentsCollectionDocumentsToAttach = em.getReference(documentsCollectionDocumentsToAttach.getClass(), documentsCollectionDocumentsToAttach.getDnid());
                attachedDocumentsCollection.add(documentsCollectionDocumentsToAttach);
            }
            intervals.setDocumentsCollection(attachedDocumentsCollection);
            Collection<Assertions> attachedAssertionsCollection = new ArrayList<Assertions>();
            for (Assertions assertionsCollectionAssertionsToAttach : intervals.getAssertionsCollection()) {
                assertionsCollectionAssertionsToAttach = em.getReference(assertionsCollectionAssertionsToAttach.getClass(), assertionsCollectionAssertionsToAttach.getAuuid());
                attachedAssertionsCollection.add(assertionsCollectionAssertionsToAttach);
            }
            intervals.setAssertionsCollection(attachedAssertionsCollection);
            em.persist(intervals);
            for (Documents documentsCollectionDocuments : intervals.getDocumentsCollection()) {
                Intervals oldInidOfDocumentsCollectionDocuments = documentsCollectionDocuments.getInid();
                documentsCollectionDocuments.setInid(intervals);
                documentsCollectionDocuments = em.merge(documentsCollectionDocuments);
                if (oldInidOfDocumentsCollectionDocuments != null) {
                    oldInidOfDocumentsCollectionDocuments.getDocumentsCollection().remove(documentsCollectionDocuments);
                    oldInidOfDocumentsCollectionDocuments = em.merge(oldInidOfDocumentsCollectionDocuments);
                }
            }
            for (Assertions assertionsCollectionAssertions : intervals.getAssertionsCollection()) {
                Intervals oldInidOfAssertionsCollectionAssertions = assertionsCollectionAssertions.getInid();
                assertionsCollectionAssertions.setInid(intervals);
                assertionsCollectionAssertions = em.merge(assertionsCollectionAssertions);
                if (oldInidOfAssertionsCollectionAssertions != null) {
                    oldInidOfAssertionsCollectionAssertions.getAssertionsCollection().remove(assertionsCollectionAssertions);
                    oldInidOfAssertionsCollectionAssertions = em.merge(oldInidOfAssertionsCollectionAssertions);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Intervals intervals) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Intervals persistentIntervals = em.find(Intervals.class, intervals.getInid());
            Collection<Documents> documentsCollectionOld = persistentIntervals.getDocumentsCollection();
            Collection<Documents> documentsCollectionNew = intervals.getDocumentsCollection();
            Collection<Assertions> assertionsCollectionOld = persistentIntervals.getAssertionsCollection();
            Collection<Assertions> assertionsCollectionNew = intervals.getAssertionsCollection();
            List<String> illegalOrphanMessages = null;
            for (Documents documentsCollectionOldDocuments : documentsCollectionOld) {
                if (!documentsCollectionNew.contains(documentsCollectionOldDocuments)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Documents " + documentsCollectionOldDocuments + " since its inid field is not nullable.");
                }
            }
            for (Assertions assertionsCollectionOldAssertions : assertionsCollectionOld) {
                if (!assertionsCollectionNew.contains(assertionsCollectionOldAssertions)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Assertions " + assertionsCollectionOldAssertions + " since its inid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Documents> attachedDocumentsCollectionNew = new ArrayList<Documents>();
            for (Documents documentsCollectionNewDocumentsToAttach : documentsCollectionNew) {
                documentsCollectionNewDocumentsToAttach = em.getReference(documentsCollectionNewDocumentsToAttach.getClass(), documentsCollectionNewDocumentsToAttach.getDnid());
                attachedDocumentsCollectionNew.add(documentsCollectionNewDocumentsToAttach);
            }
            documentsCollectionNew = attachedDocumentsCollectionNew;
            intervals.setDocumentsCollection(documentsCollectionNew);
            Collection<Assertions> attachedAssertionsCollectionNew = new ArrayList<Assertions>();
            for (Assertions assertionsCollectionNewAssertionsToAttach : assertionsCollectionNew) {
                assertionsCollectionNewAssertionsToAttach = em.getReference(assertionsCollectionNewAssertionsToAttach.getClass(), assertionsCollectionNewAssertionsToAttach.getAuuid());
                attachedAssertionsCollectionNew.add(assertionsCollectionNewAssertionsToAttach);
            }
            assertionsCollectionNew = attachedAssertionsCollectionNew;
            intervals.setAssertionsCollection(assertionsCollectionNew);
            intervals = em.merge(intervals);
            for (Documents documentsCollectionNewDocuments : documentsCollectionNew) {
                if (!documentsCollectionOld.contains(documentsCollectionNewDocuments)) {
                    Intervals oldInidOfDocumentsCollectionNewDocuments = documentsCollectionNewDocuments.getInid();
                    documentsCollectionNewDocuments.setInid(intervals);
                    documentsCollectionNewDocuments = em.merge(documentsCollectionNewDocuments);
                    if (oldInidOfDocumentsCollectionNewDocuments != null && !oldInidOfDocumentsCollectionNewDocuments.equals(intervals)) {
                        oldInidOfDocumentsCollectionNewDocuments.getDocumentsCollection().remove(documentsCollectionNewDocuments);
                        oldInidOfDocumentsCollectionNewDocuments = em.merge(oldInidOfDocumentsCollectionNewDocuments);
                    }
                }
            }
            for (Assertions assertionsCollectionNewAssertions : assertionsCollectionNew) {
                if (!assertionsCollectionOld.contains(assertionsCollectionNewAssertions)) {
                    Intervals oldInidOfAssertionsCollectionNewAssertions = assertionsCollectionNewAssertions.getInid();
                    assertionsCollectionNewAssertions.setInid(intervals);
                    assertionsCollectionNewAssertions = em.merge(assertionsCollectionNewAssertions);
                    if (oldInidOfAssertionsCollectionNewAssertions != null && !oldInidOfAssertionsCollectionNewAssertions.equals(intervals)) {
                        oldInidOfAssertionsCollectionNewAssertions.getAssertionsCollection().remove(assertionsCollectionNewAssertions);
                        oldInidOfAssertionsCollectionNewAssertions = em.merge(oldInidOfAssertionsCollectionNewAssertions);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = intervals.getInid();
                if (findIntervals(id) == null) {
                    throw new NonexistentEntityException("The intervals with id " + id + " no longer exists.");
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
            Intervals intervals;
            try {
                intervals = em.getReference(Intervals.class, id);
                intervals.getInid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The intervals with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Documents> documentsCollectionOrphanCheck = intervals.getDocumentsCollection();
            for (Documents documentsCollectionOrphanCheckDocuments : documentsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Intervals (" + intervals + ") cannot be destroyed since the Documents " + documentsCollectionOrphanCheckDocuments + " in its documentsCollection field has a non-nullable inid field.");
            }
            Collection<Assertions> assertionsCollectionOrphanCheck = intervals.getAssertionsCollection();
            for (Assertions assertionsCollectionOrphanCheckAssertions : assertionsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Intervals (" + intervals + ") cannot be destroyed since the Assertions " + assertionsCollectionOrphanCheckAssertions + " in its assertionsCollection field has a non-nullable inid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(intervals);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Intervals> findIntervalsEntities() {
        return findIntervalsEntities(true, -1, -1);
    }

    public List<Intervals> findIntervalsEntities(int maxResults, int firstResult) {
        return findIntervalsEntities(false, maxResults, firstResult);
    }

    private List<Intervals> findIntervalsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Intervals.class));
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

    public Intervals findIntervals(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Intervals.class, id);
        } finally {
            em.close();
        }
    }

    public int getIntervalsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Intervals> rt = cq.from(Intervals.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
