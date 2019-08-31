package br.ufop.tomaz.dao;

import br.ufop.tomaz.model.Professor;

import javax.persistence.*;
import java.util.List;

public class ProfessorDAOImpl implements ProfessorDAO {

    private static ProfessorDAOImpl instance = null;
    private EntityManager entityManager;

    public static ProfessorDAOImpl getInstance() {
        if (instance == null) {
            instance = new ProfessorDAOImpl();
        }
        return instance;
    }

    private ProfessorDAOImpl() {
        entityManager = getEntityManager();
    }

    private EntityManager getEntityManager() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("persistence-unit-H2");
        if (entityManager == null)
            entityManager = factory.createEntityManager();
        return entityManager;
    }

    @Override
    public void persistProfessor(Professor professor) {

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(professor);
            entityManager.getTransaction().commit();
        } catch (EntityExistsException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void deleteProfessor(Professor professor) {
        Professor p = entityManager.find(Professor.class, professor.getId());
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(p);
            entityManager.getTransaction().commit();
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public Professor getProfessorById(Long professorId) {
        return entityManager.find(Professor.class, professorId);
    }

    @Override
    public void updateProfessor(Professor professor) {
        professor = entityManager.find(Professor.class, professor.getId());
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(professor);
            entityManager.getTransaction().commit();
        } catch (EntityNotFoundException e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    @Override
    public List<Professor> getProfessors() {
        return entityManager.createNamedQuery("Professor.findAll", Professor.class).getResultList();
    }

    @Override
    public Professor getProfessorByName(String name) throws NoResultException {
        String sqlQuery = "SELECT P FROM Professor P WHERE P.name = :name";
        TypedQuery<Professor> query = entityManager.createQuery(sqlQuery, Professor.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }
}
