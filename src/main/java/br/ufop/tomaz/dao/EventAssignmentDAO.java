package br.ufop.tomaz.dao;

import br.ufop.tomaz.model.ClassE;
import br.ufop.tomaz.model.EventAssignment;
import br.ufop.tomaz.model.Professor;

import java.util.List;

public interface EventAssignmentDAO {

    void persistEventAssignment(EventAssignment eventAssignment);

    void deleteEventAssignment(EventAssignment eventAssignment);

    void updateEventAssignment(EventAssignment eventAssignment);

    List<EventAssignment> retrieveEventsAssignmentsByProfessor(Professor professor);

    List<EventAssignment> retrieveEventsAssignmentsByClass(ClassE classE);

    EventAssignment getEventAssignmentById(Long id);
}
