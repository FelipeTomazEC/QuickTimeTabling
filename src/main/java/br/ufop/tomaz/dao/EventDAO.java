package br.ufop.tomaz.dao;

import br.ufop.tomaz.model.ClassE;
import br.ufop.tomaz.model.Event;
import br.ufop.tomaz.model.Professor;

import java.util.List;

public interface EventDAO {

    void persistEvent(Event event);

    void deleteEvent(Event event);

    List<Event> getEventsByProfessor(Professor professor);

    List<Event> getEventsByClass(ClassE classE);

    Event getEventBySubjectAndClass(String subject, String className);

    void updateEvent(Event event);

    Event getEventById(Long id);

    List<Event> getAllEvents();
}
