package br.ufop.tomaz.dao;

import br.ufop.tomaz.model.Professor;

import javax.persistence.NoResultException;
import java.util.List;

public interface ProfessorDAO {
    void persistProfessor(Professor professor);

    void deleteProfessor(Professor professor);

    Professor getProfessorById(Long professorId);

    void updateProfessor(Professor professor);

    List<Professor> getProfessors();

    Professor getProfessorByName(String name) throws NoResultException;
}
