package br.ufop.tomaz.services;

import br.ufop.tomaz.App;
import br.ufop.tomaz.model.ClassE;
import br.ufop.tomaz.model.Constraint;
import br.ufop.tomaz.model.Event;
import br.ufop.tomaz.model.Professor;
import br.ufop.tomaz.util.FileUtils;
import br.ufop.tomaz.model.Constraint.ConstraintType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class SolutionGenerator {
    private File professors;
    private File classes;
    private File events;
    private File linkedEvents;
    private final String SOLUTION_TEMPORARY_FILES_DIR = App.APP_DIRECTORY;

    public SolutionGenerator(List<Professor> professorList,
                             List<ClassE> classList,
                             List<Event> eventList) throws IOException
    {
        File tempDir = new File(SOLUTION_TEMPORARY_FILES_DIR);

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
            process.waitFor();
            System.out.println(commandLine);
            System.out.println("Exit code: "+ process.exitValue());
            InputStream error = process.getErrorStream();
            StringBuilder errorMessage = new StringBuilder();
            for (int i = 0; i < error.available(); i++) {
                errorMessage.append((char)error.read());
            }
            System.out.println(errorMessage);
            Thread.sleep(3000);
            process.destroy();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getCommandLine(){
        Map<ConstraintType, Constraint> constraintSettings = AppSettings.getInstance().getConstraintMap();
        String solverPath = this.getClass()
                .getResource("/solver/ET3.jar")
                .getFile()
                .replaceFirst("/","");
        String argument2 = String.valueOf(constraintSettings.get(ConstraintType.PROFESSORS_COST).getWeight());
        String argument3 = String.valueOf(constraintSettings.get(ConstraintType.NUMBER_OF_BUSY_DAYS).getWeight());
        String argument4 = String.valueOf(constraintSettings.get(ConstraintType.UNDESIRED_TIMES).getWeight());
        String argument5 = String.valueOf(constraintSettings.get(ConstraintType.UNDESIRED_PATTERNS).getWeight());

        return "java -jar ".concat(solverPath)
                .concat(" ").concat("\""+SOLUTION_TEMPORARY_FILES_DIR+"\"")
                .concat(" ").concat(argument2)
                .concat(" ").concat(argument3)
                .concat(" ").concat(argument4)
                .concat(" ").concat(argument5);
    }
}
