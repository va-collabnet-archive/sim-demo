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
import gov.va.demo.nb.sim.jpa.Persons;
import gov.va.demo.nb.sim.jpa.Intervals;
import gov.va.demo.nb.sim.jpa.Assertions;
import gov.va.demo.nb.sim.jpa.Documents;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author kec
 */
public class DocumentsJpaController implements Serializable {

    public DocumentsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Documents documents) {
        if (documents.getAssertionsCollection() == null) {
            documents.setAssertionsCollection(new ArrayList<Assertions>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Persons providernid = documents.getProvidernid();
            if (providernid != null) {
                providernid = em.getReference(providernid.getClass(), providernid.getPnid());
                documents.setProvidernid(providernid);
            }
            Persons patientnid = documents.getPatientnid();
            if (patientnid != null) {
                patientnid = em.getReference(patientnid.getClass(), patientnid.getPnid());
                documents.setPatientnid(patientnid);
            }
            Intervals inid = documents.getInid();
            if (inid != null) {
                inid = em.getReference(inid.getClass(), inid.getInid());
                documents.setInid(inid);
            }
            Collection<Assertions> attachedAssertionsCollection = new ArrayList<Assertions>();
            for (Assertions assertionsCollectionAssertionsToAttach : documents.getAssertionsCollection()) {
                assertionsCollectionAssertionsToAttach = em.getReference(assertionsCollectionAssertionsToAttach.getClass(), assertionsCollectionAssertionsToAttach.getAuuid());
                attachedAssertionsCollection.add(assertionsCollectionAssertionsToAttach);
            }
            documents.setAssertionsCollection(attachedAssertionsCollection);
            em.persist(documents);
            if (providernid != null) {
                providernid.getDocumentsCollection().add(documents);
                providernid = em.merge(providernid);
            }
            if (patientnid != null) {
                patientnid.getDocumentsCollection().add(documents);
                patientnid = em.merge(patientnid);
            }
            if (inid != null) {
                inid.getDocumentsCollection().add(documents);
                inid = em.merge(inid);
            }
            for (Assertions assertionsCollectionAssertions : documents.getAssertionsCollection()) {
                Documents oldDnidOfAssertionsCollectionAssertions = assertionsCollectionAssertions.getDnid();
                assertionsCollectionAssertions.setDnid(documents);
                assertionsCollectionAssertions = em.merge(assertionsCollectionAssertions);
                if (oldDnidOfAssertionsCollectionAssertions != null) {
                    oldDnidOfAssertionsCollectionAssertions.getAssertionsCollection().remove(assertionsCollectionAssertions);
                    oldDnidOfAssertionsCollectionAssertions = em.merge(oldDnidOfAssertionsCollectionAssertions);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Documents documents) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Documents persistentDocuments = em.find(Documents.class, documents.getDnid());
            Persons providernidOld = persistentDocuments.getProvidernid();
            Persons providernidNew = documents.getProvidernid();
            Persons patientnidOld = persistentDocuments.getPatientnid();
            Persons patientnidNew = documents.getPatientnid();
            Intervals inidOld = persistentDocuments.getInid();
            Intervals inidNew = documents.getInid();
            Collection<Assertions> assertionsCollectionOld = persistentDocuments.getAssertionsCollection();
            Collection<Assertions> assertionsCollectionNew = documents.getAssertionsCollection();
            List<String> illegalOrphanMessages = null;
            for (Assertions assertionsCollectionOldAssertions : assertionsCollectionOld) {
                if (!assertionsCollectionNew.contains(assertionsCollectionOldAssertions)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Assertions " + assertionsCollectionOldAssertions + " since its dnid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (providernidNew != null) {
                providernidNew = em.getReference(providernidNew.getClass(), providernidNew.getPnid());
                documents.setProvidernid(providernidNew);
            }
            if (patientnidNew != null) {
                patientnidNew = em.getReference(patientnidNew.getClass(), patientnidNew.getPnid());
                documents.setPatientnid(patientnidNew);
            }
            if (inidNew != null) {
                inidNew = em.getReference(inidNew.getClass(), inidNew.getInid());
                documents.setInid(inidNew);
            }
            Collection<Assertions> attachedAssertionsCollectionNew = new ArrayList<Assertions>();
            for (Assertions assertionsCollectionNewAssertionsToAttach : assertionsCollectionNew) {
                assertionsCollectionNewAssertionsToAttach = em.getReference(assertionsCollectionNewAssertionsToAttach.getClass(), assertionsCollectionNewAssertionsToAttach.getAuuid());
                attachedAssertionsCollectionNew.add(assertionsCollectionNewAssertionsToAttach);
            }
            assertionsCollectionNew = attachedAssertionsCollectionNew;
            documents.setAssertionsCollection(assertionsCollectionNew);
            documents = em.merge(documents);
            if (providernidOld != null && !providernidOld.equals(providernidNew)) {
                providernidOld.getDocumentsCollection().remove(documents);
                providernidOld = em.merge(providernidOld);
            }
            if (providernidNew != null && !providernidNew.equals(providernidOld)) {
                providernidNew.getDocumentsCollection().add(documents);
                providernidNew = em.merge(providernidNew);
            }
            if (patientnidOld != null && !patientnidOld.equals(patientnidNew)) {
                patientnidOld.getDocumentsCollection().remove(documents);
                patientnidOld = em.merge(patientnidOld);
            }
            if (patientnidNew != null && !patientnidNew.equals(patientnidOld)) {
                patientnidNew.getDocumentsCollection().add(documents);
                patientnidNew = em.merge(patientnidNew);
            }
            if (inidOld != null && !inidOld.equals(inidNew)) {
                inidOld.getDocumentsCollection().remove(documents);
                inidOld = em.merge(inidOld);
            }
            if (inidNew != null && !inidNew.equals(inidOld)) {
                inidNew.getDocumentsCollection().add(documents);
                inidNew = em.merge(inidNew);
            }
            for (Assertions assertionsCollectionNewAssertions : assertionsCollectionNew) {
                if (!assertionsCollectionOld.contains(assertionsCollectionNewAssertions)) {
                    Documents oldDnidOfAssertionsCollectionNewAssertions = assertionsCollectionNewAssertions.getDnid();
                    assertionsCollectionNewAssertions.setDnid(documents);
                    assertionsCollectionNewAssertions = em.merge(assertionsCollectionNewAssertions);
                    if (oldDnidOfAssertionsCollectionNewAssertions != null && !oldDnidOfAssertionsCollectionNewAssertions.equals(documents)) {
                        oldDnidOfAssertionsCollectionNewAssertions.getAssertionsCollection().remove(assertionsCollectionNewAssertions);
                        oldDnidOfAssertionsCollectionNewAssertions = em.merge(oldDnidOfAssertionsCollectionNewAssertions);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = documents.getDnid();
                if (findDocuments(id) == null) {
                    throw new NonexistentEntityException("The documents with id " + id + " no longer exists.");
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
            Documents documents;
            try {
                documents = em.getReference(Documents.class, id);
                documents.getDnid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The documents with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Assertions> assertionsCollectionOrphanCheck = documents.getAssertionsCollection();
            for (Assertions assertionsCollectionOrphanCheckAssertions : assertionsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Documents (" + documents + ") cannot be destroyed since the Assertions " + assertionsCollectionOrphanCheckAssertions + " in its assertionsCollection field has a non-nullable dnid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Persons providernid = documents.getProvidernid();
            if (providernid != null) {
                providernid.getDocumentsCollection().remove(documents);
                providernid = em.merge(providernid);
            }
            Persons patientnid = documents.getPatientnid();
            if (patientnid != null) {
                patientnid.getDocumentsCollection().remove(documents);
                patientnid = em.merge(patientnid);
            }
            Intervals inid = documents.getInid();
            if (inid != null) {
                inid.getDocumentsCollection().remove(documents);
                inid = em.merge(inid);
            }
            em.remove(documents);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Documents> findDocumentsEntities() {
        return findDocumentsEntities(true, -1, -1);
    }

    public List<Documents> findDocumentsEntities(int maxResults, int firstResult) {
        return findDocumentsEntities(false, maxResults, firstResult);
    }

    private List<Documents> findDocumentsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Documents.class));
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

    public Documents findDocuments(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Documents.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocumentsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Documents> rt = cq.from(Documents.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
