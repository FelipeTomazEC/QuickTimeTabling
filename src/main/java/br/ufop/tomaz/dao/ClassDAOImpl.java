package br.ufop.tomaz.dao;

import br.ufop.tomaz.model.ClassE;

import javax.persistence.*;
import java.util.List;

public class ClassDAOImpl implements ClassDAO {

    private static ClassDAOImpl instance = null;
    private EntityManager entityManager;

    private ClassDAOImpl() {
        entityManager = getEntityManager();
    }

    private EntityManager getEntityManager() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("persistence-unit-H2");
        if (entityManager == null)
            entityManager = factory.createEntityManager();
        return entityManager;
    }

    public static ClassDAOImpl getInstance() {
        if (instance == null)
            instance = new ClassDAOImpl();
        return instance;
    }


    @Override
    public void persistClass(ClassE classE) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(classE);
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void deleteClass(ClassE classE) {
        try {
            classE = entityManager.find(ClassE.class, classE.getId());
            entityManager.getTransaction().begin();
            entityManager.remove(classE);
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public ClassE getClassById(Long classID) {
        try {
            return entityManager.find(ClassE.class, classID);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateClass(ClassE classE) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(classE);
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public List<ClassE> getClasses() {
        return entityManager.createNamedQuery("ClassE.findAll", ClassE.class).getResultList();
    }

    @Override
    public ClassE getClassByName(String className) throws NoResultException {
        String strQuery = "SELECT C FROM ClassE C WHERE C.name = :name";
        TypedQuery<ClassE> query = entityManager.createQuery(strQuery, ClassE.class);
        query.setParameter("name", className);
        return query.getSingleResult();
    }
}
