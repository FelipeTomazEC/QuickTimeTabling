package br.ufop.tomaz.services;

import br.ufop.tomaz.App;
import br.ufop.tomaz.model.ClassE;
import br.ufop.tomaz.model.Event;
import br.ufop.tomaz.model.Professor;
import br.ufop.tomaz.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SolutionGenerator {
    private File professors;
    private File classes;
    private File events;
    private File linkedEvents;

    public SolutionGenerator(List<Professor> professorList,
                             List<ClassE> classList,
                             List<Event> eventList) throws IOException
    {
        File tempDir = new File(App.APP_DIRECTORY);

        this.professors = new File(tempDir, "professors.csv");
        this.classes = new File(tempDir, "classes.csv");
        this.events = new File(tempDir, "events.csv");
        this.linkedEvents = new File(tempDir, "linkedEvents.csv");

        FileUtils fileUtils = new FileUtils();
        fileUtils.exportProfessors(professorList, this.professors);
        fileUtils.exportsClasses(classList, this.classes);
        fileUtils.exportEvents(eventList, this.events);
        fileUtils.exportLinkedEvents(eventList, this.linkedEvents);

        this.professors.deleteOnExit();
        this.classes.deleteOnExit();
        this.events.deleteOnExit();
        this.linkedEvents.deleteOnExit();
    }

    public File getSolution(double timeToSolve) {
        String commandLine = getCommandLine();
        try {
            Process process = Runtime.getRuntime().exec(commandLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getCommandLine(){
        String solverPath = this.getClass().getResource("/solver/ET3.jar").getPath();
        String commandLine = "java -jar ".concat(solverPath);
        return commandLine;
    }
}
