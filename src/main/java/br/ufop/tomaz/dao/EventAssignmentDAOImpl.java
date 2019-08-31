package br.ufop.tomaz.dao;

import br.ufop.tomaz.model.ClassE;
import br.ufop.tomaz.model.EventAssignment;
import br.ufop.tomaz.model.Professor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.util.List;

public class EventAssignmentDAOImpl implements EventAssignmentDAO {

    private static EventAssignmentDAOImpl eventAssignmentDAO = null;
    private EntityManager entityManager;

    private EventAssignmentDAOImpl() {
        entityManager = getEntityManager();
    }

    private EntityManager getEntityManager() {
        if (entityManager == null) {
            EntityManagerFactory factory = Persistence.createEntityManagerFactory("persistence-unit-H2");
            entityManager = factory.createEntityManager();
        }
        return entityManager;
    }

    public static EventAssignmentDAO getInstance() {
        if (eventAssignmentDAO == null)
            eventAssignmentDAO = new EventAssignmentDAOImpl();
        return eventAssignmentDAO;
    }

    @Override
    public void persistEventAssignment(EventAssignment eventAssignment) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(eventAssignment);
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void deleteEventAssignment(EventAssignment eventAssignment) {
        eventAssignment = entityManager.find(EventAssignment.class, eventAssignment.getId());
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(eventAssignment);
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void updateEventAssignment(EventAssignment eventAssignment) {
        eventAssignment = entityManager.find(EventAssignment.class, eventAssignment.getId());
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(eventAssignment);
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    @Override
    public List<EventAssignment> retrieveEventsAssignmentsByProfessor(Professor professor) {
        return null;
    }

    @Override
    public List<EventAssignment> retrieveEventsAssignmentsByClass(ClassE classE) {
        return null;
    }

    @Override
    public EventAssignment getEventAssignmentById(Long id) {
        return entityManager.find(EventAssignment.class, id);
    }
}
