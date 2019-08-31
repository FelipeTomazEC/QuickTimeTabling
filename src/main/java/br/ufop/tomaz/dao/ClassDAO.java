package br.ufop.tomaz.dao;

import br.ufop.tomaz.model.ClassE;

import javax.persistence.NoResultException;
import java.util.List;

public interface ClassDAO {

    void persistClass(ClassE classE);

    void deleteClass(ClassE classE);

    ClassE getClassById(Long classID);

    void updateClass(ClassE classE);

    List<ClassE> getClasses();

    ClassE getClassByName(String className) throws NoResultException;
}
