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
import gov.va.demo.nb.sim.jpa.Persons;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author kec
 */
public class PersonsJpaController implements Serializable {

    public PersonsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Persons persons) {
        if (persons.getDocumentsCollection() == null) {
            persons.setDocumentsCollection(new ArrayList<Documents>());
        }
        if (persons.getDocumentsCollection1() == null) {
            persons.setDocumentsCollection1(new ArrayList<Documents>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Documents> attachedDocumentsCollection = new ArrayList<Documents>();
            for (Documents documentsCollectionDocumentsToAttach : persons.getDocumentsCollection()) {
                documentsCollectionDocumentsToAttach = em.getReference(documentsCollectionDocumentsToAttach.getClass(), documentsCollectionDocumentsToAttach.getDnid());
                attachedDocumentsCollection.add(documentsCollectionDocumentsToAttach);
            }
            persons.setDocumentsCollection(attachedDocumentsCollection);
            Collection<Documents> attachedDocumentsCollection1 = new ArrayList<Documents>();
            for (Documents documentsCollection1DocumentsToAttach : persons.getDocumentsCollection1()) {
                documentsCollection1DocumentsToAttach = em.getReference(documentsCollection1DocumentsToAttach.getClass(), documentsCollection1DocumentsToAttach.getDnid());
                attachedDocumentsCollection1.add(documentsCollection1DocumentsToAttach);
            }
            persons.setDocumentsCollection1(attachedDocumentsCollection1);
            em.persist(persons);
            for (Documents documentsCollectionDocuments : persons.getDocumentsCollection()) {
                Persons oldProvidernidOfDocumentsCollectionDocuments = documentsCollectionDocuments.getProvidernid();
                documentsCollectionDocuments.setProvidernid(persons);
                documentsCollectionDocuments = em.merge(documentsCollectionDocuments);
                if (oldProvidernidOfDocumentsCollectionDocuments != null) {
                    oldProvidernidOfDocumentsCollectionDocuments.getDocumentsCollection().remove(documentsCollectionDocuments);
                    oldProvidernidOfDocumentsCollectionDocuments = em.merge(oldProvidernidOfDocumentsCollectionDocuments);
                }
            }
            for (Documents documentsCollection1Documents : persons.getDocumentsCollection1()) {
                Persons oldPatientnidOfDocumentsCollection1Documents = documentsCollection1Documents.getPatientnid();
                documentsCollection1Documents.setPatientnid(persons);
                documentsCollection1Documents = em.merge(documentsCollection1Documents);
                if (oldPatientnidOfDocumentsCollection1Documents != null) {
                    oldPatientnidOfDocumentsCollection1Documents.getDocumentsCollection1().remove(documentsCollection1Documents);
                    oldPatientnidOfDocumentsCollection1Documents = em.merge(oldPatientnidOfDocumentsCollection1Documents);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Persons persons) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Persons persistentPersons = em.find(Persons.class, persons.getPnid());
            Collection<Documents> documentsCollectionOld = persistentPersons.getDocumentsCollection();
            Collection<Documents> documentsCollectionNew = persons.getDocumentsCollection();
            Collection<Documents> documentsCollection1Old = persistentPersons.getDocumentsCollection1();
            Collection<Documents> documentsCollection1New = persons.getDocumentsCollection1();
            List<String> illegalOrphanMessages = null;
            for (Documents documentsCollection1OldDocuments : documentsCollection1Old) {
                if (!documentsCollection1New.contains(documentsCollection1OldDocuments)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Documents " + documentsCollection1OldDocuments + " since its patientnid field is not nullable.");
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
            persons.setDocumentsCollection(documentsCollectionNew);
            Collection<Documents> attachedDocumentsCollection1New = new ArrayList<Documents>();
            for (Documents documentsCollection1NewDocumentsToAttach : documentsCollection1New) {
                documentsCollection1NewDocumentsToAttach = em.getReference(documentsCollection1NewDocumentsToAttach.getClass(), documentsCollection1NewDocumentsToAttach.getDnid());
                attachedDocumentsCollection1New.add(documentsCollection1NewDocumentsToAttach);
            }
            documentsCollection1New = attachedDocumentsCollection1New;
            persons.setDocumentsCollection1(documentsCollection1New);
            persons = em.merge(persons);
            for (Documents documentsCollectionOldDocuments : documentsCollectionOld) {
                if (!documentsCollectionNew.contains(documentsCollectionOldDocuments)) {
                    documentsCollectionOldDocuments.setProvidernid(null);
                    documentsCollectionOldDocuments = em.merge(documentsCollectionOldDocuments);
                }
            }
            for (Documents documentsCollectionNewDocuments : documentsCollectionNew) {
                if (!documentsCollectionOld.contains(documentsCollectionNewDocuments)) {
                    Persons oldProvidernidOfDocumentsCollectionNewDocuments = documentsCollectionNewDocuments.getProvidernid();
                    documentsCollectionNewDocuments.setProvidernid(persons);
                    documentsCollectionNewDocuments = em.merge(documentsCollectionNewDocuments);
                    if (oldProvidernidOfDocumentsCollectionNewDocuments != null && !oldProvidernidOfDocumentsCollectionNewDocuments.equals(persons)) {
                        oldProvidernidOfDocumentsCollectionNewDocuments.getDocumentsCollection().remove(documentsCollectionNewDocuments);
                        oldProvidernidOfDocumentsCollectionNewDocuments = em.merge(oldProvidernidOfDocumentsCollectionNewDocuments);
                    }
                }
            }
            for (Documents documentsCollection1NewDocuments : documentsCollection1New) {
                if (!documentsCollection1Old.contains(documentsCollection1NewDocuments)) {
                    Persons oldPatientnidOfDocumentsCollection1NewDocuments = documentsCollection1NewDocuments.getPatientnid();
                    documentsCollection1NewDocuments.setPatientnid(persons);
                    documentsCollection1NewDocuments = em.merge(documentsCollection1NewDocuments);
                    if (oldPatientnidOfDocumentsCollection1NewDocuments != null && !oldPatientnidOfDocumentsCollection1NewDocuments.equals(persons)) {
                        oldPatientnidOfDocumentsCollection1NewDocuments.getDocumentsCollection1().remove(documentsCollection1NewDocuments);
                        oldPatientnidOfDocumentsCollection1NewDocuments = em.merge(oldPatientnidOfDocumentsCollection1NewDocuments);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = persons.getPnid();
                if (findPersons(id) == null) {
                    throw new NonexistentEntityException("The persons with id " + id + " no longer exists.");
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
            Persons persons;
            try {
                persons = em.getReference(Persons.class, id);
                persons.getPnid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The persons with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Documents> documentsCollection1OrphanCheck = persons.getDocumentsCollection1();
            for (Documents documentsCollection1OrphanCheckDocuments : documentsCollection1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Persons (" + persons + ") cannot be destroyed since the Documents " + documentsCollection1OrphanCheckDocuments + " in its documentsCollection1 field has a non-nullable patientnid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Documents> documentsCollection = persons.getDocumentsCollection();
            for (Documents documentsCollectionDocuments : documentsCollection) {
                documentsCollectionDocuments.setProvidernid(null);
                documentsCollectionDocuments = em.merge(documentsCollectionDocuments);
            }
            em.remove(persons);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Persons> findPersonsEntities() {
        return findPersonsEntities(true, -1, -1);
    }

    public List<Persons> findPersonsEntities(int maxResults, int firstResult) {
        return findPersonsEntities(false, maxResults, firstResult);
    }

    private List<Persons> findPersonsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Persons.class));
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

    public Persons findPersons(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Persons.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Persons> rt = cq.from(Persons.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
