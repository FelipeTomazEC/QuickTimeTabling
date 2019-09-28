package br.ufop.tomaz.dao;

import br.ufop.tomaz.model.ClassE;
import br.ufop.tomaz.model.Event;
import br.ufop.tomaz.model.Professor;

import javax.persistence.*;
import java.util.List;

public class EventDAOImpl implements EventDAO {

    private static EventDAOImpl instance = null;
    private EntityManager entityManager;

    private EventDAOImpl() {
        entityManager = getEntityManager();
    }

    private EntityManager getEntityManager() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("persistence-unit-H2");
        if (entityManager == null)
            entityManager = factory.createEntityManager();
        return entityManager;
    }

    public static EventDAOImpl getInstance() {
        if (instance == null)
            instance = new EventDAOImpl();
        return instance;
    }

    @Override
    public void persistEvent(Event event) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(event);
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEvent(Event event) {
        try {
            event = entityManager.find(Event.class, event.getId());
            entityManager.getTransaction().begin();
            entityManager.remove(event);
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public List<Event> getEventsByProfessor(Professor professor) {
        try {
            String sqlQuery = "SELECT E FROM Event E JOIN E.professorWeights PW WHERE :professor = PW.professor";
            professor = entityManager.find(Professor.class, professor.getId());
            TypedQuery<Event> query = entityManager.createQuery(sqlQuery, Event.class);
            query.setParameter("professor", professor);
            return query.getResultList();
        } catch (PersistenceException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Event> getEventsByClass(ClassE classE) {
        String sqlQuery = "SELECT E FROM Event E WHERE E.classE = :classE";
        try {
            classE = entityManager.find(ClassE.class, classE.getId());
            TypedQuery<Event> query = entityManager.createQuery(sqlQuery, Event.class);
            query.setParameter("classE", classE);
            return query.getResultList();
        } catch (PersistenceException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Event getEventBySubjectAndClass(String subject, String className) {
        String sqlQuery = "SELECT E FROM Event E WHERE E.subject = :subject and E.classE =: classE";
        ClassE classE = ClassDAOImpl.getInstance().getClassByName(className);
        try {
            TypedQuery<Event> query = entityManager.createQuery(sqlQuery, Event.class);
            query.setParameter("subject", subject);
            query.setParameter("classE", classE);
            return query.getSingleResult();
        } catch (PersistenceException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateEvent(Event event) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(event);
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public Event getEventById(Long id) {
        return entityManager.find(Event.class, id);
    }

    @Override
    public List<Event> getAllEvents() {
        return entityManager.createNamedQuery("Event.findAll", Event.class).getResultList();
    }
}
