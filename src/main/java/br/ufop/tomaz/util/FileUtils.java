package br.ufop.tomaz.util;

import br.ufop.tomaz.dao.*;
import br.ufop.tomaz.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import javax.persistence.NoResultException;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

    public List<ClassE> importClasses(File file) throws IOException {
        List<ClassE> classList = new ArrayList<>();
        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat
                .DEFAULT
                .withHeader("Class", "Unavailable Times")
                .withFirstRecordAsHeader()
                .parse(in);

        records.forEach((line) -> {
            String className = line.get("Class");
            String unavailability = line.get("Unavailable Times");

            classList.add(getClassE(className, unavailability));
        });
        return classList;
    }

    public void exportsClasses(List<ClassE> classList, File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String [] header = {"Class", "Unavailable Times"};
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(header));

        classList.forEach(classE -> {
            String name = classE.getName();
            String unavailableTimes = unavailabilityToString(classE.getUnavailabilities());

            try {
                System.out.println(unavailableTimes);
                printer.printRecord(name, unavailableTimes);
            } catch (IOException e) {
                System.out.println("Occurred some error when exporting classes.");
                e.printStackTrace();
            }
        });

        printer.flush();
        writer.close();
    }

    public List<Professor> importProfessors(File file) throws IOException {
        List<Professor> professorList = new ArrayList<>();
        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader("Professor",
                        "Workload",
                        "Min. Days",
                        "Max. Days",
                        "Unavailable Times",
                        "Undesired Times",
                        "Undesired Patterns",
                        "Priority",
                        "Tag"
                ).withFirstRecordAsHeader()
                .parse(in);

        records.forEach(line -> {
            String name = line.get("Professor");
            String workload = line.get("Workload");
            String minDays = line.get("Min. Days");
            String maxDays = line.get("Max. Days");
            String unavailableTimes = line.get("Unavailable Times");
            String undesiredTimes = line.get("Undesired Times");
            String undesiredPatterns = line.get("Undesired Patterns");
            String priority = line.get("Priority");
            String tag = line.get("Tag");

            professorList.add(
                    getProfessor(name, workload, minDays, maxDays, unavailableTimes, undesiredTimes, undesiredPatterns, priority, tag)
            );
        });
        return professorList;
    }

    public void exportProfessors(List<Professor> professorList, File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String [] header = {"Professor",
                "Workload",
                "Min. Days",
                "Max. Days",
                "Unavailable Times",
                "Undesired Times",
                "Undesired Patterns",
                "Priority",
                "Tag"
        };
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(header));

        professorList.forEach(professor -> {
            String name = professor.getName();
            int workload = professor.getWorkload();
            int minDays = professor.getPreferredMinNumberOfWorkingDays();
            int maxDays = professor.getPreferredMaxNumberOfWorkingDays();
            double priority = professor.getPriority();
            String tag = professor.getTag();

            List<Unavailability> unavailableList = professor.getUnavailabilities()
                    .stream()
                    .filter(u -> u.getType() == Unavailability.HARD_UNAVAILABILITY)
                    .collect(Collectors.toList());

            List<Unavailability> undesiredList = professor.getUnavailabilities()
                    .stream()
                    .filter(u -> u.getType() == Unavailability.SOFT_UNAVAILABILITY)
                    .collect(Collectors.toList());

            String unavailableTimes = unavailabilityToString(unavailableList);
            String undesiredTimes = unavailabilityToString(undesiredList);
            String undesiredPatterns = professor.getUndesiredPatterns()
                    .stream()
                    .map(undesiredPattern -> undesiredPattern.getPattern())
                    .map(pattern -> String.copyValueOf(pattern))
                    .reduce((p1, p2) -> p1.concat("+").concat(p2))
                    .orElse("");

            try {
                printer.printRecord(name,
                        workload,
                        minDays,
                        maxDays,
                        unavailableTimes,
                        undesiredTimes,
                        undesiredPatterns,
                        priority,
                        tag
                );
            } catch (IOException e) {
                System.out.println("Occurred some error when exporting professors.");
                e.printStackTrace();
            }
        });

        writer.flush();
        printer.flush();
        printer.close();
        writer.close();
    }

    public List<Event> importEvents(File file) throws IOException {

        List<Event> eventList = new ArrayList<>();
        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader("Class",
                        "Subject",
                        "Tag",
                        "Duration",
                        "Professor",
                        "Preassigned Times",
                        "Min Days Gap",
                        "Max Days Gap"
                )
                .withDelimiter(';')
                .withFirstRecordAsHeader()
                .parse(in);

        records.forEach(line -> {
            String className = line.get("Class");
            String subject = line.get("Subject");
            String tag = line.get("Tag");
            String duration = line.get("Duration");
            String professor = line.get("Professor");
            String preassigned = line.get("Preassigned Times");
            String minDaysGap = line.get("Min Days Gap");
            String maxDaysGap = line.get("Max Days Gap");
            eventList.add(getEvent(className, subject, tag, duration, professor, preassigned, minDaysGap, maxDaysGap));
        });
        return eventList;
    }

    public List<EventAssignment> importEventsAssignments(File solutionFile) throws IOException {
        List<EventAssignment> eventAssignments = new ArrayList<>();
        int eventIndex = 0;
        int classIndex = 1;
        int professorIndex = 2;
        int dayIndex = 3;
        int timeIndex = 4;
        ClassDAO classDAO = ClassDAOImpl.getInstance();
        ProfessorDAO professorDAO = ProfessorDAOImpl.getInstance();
        EventDAO eventDAO = EventDAOImpl.getInstance();

        List<String[]> lines = Files.lines(solutionFile.toPath())
                .filter(line -> line.startsWith("x") && line.endsWith("1"))
                .map(line -> line
                        .substring(line.indexOf("(") + 1, line.indexOf(")"))
                        .replace("..", " ")
                        .replace("~", "-")
                        .split(",")
                )
                .collect(Collectors.toList());

        lines.forEach(line -> {
            ClassE classE = classDAO.getClassByName(line[classIndex]);
            Professor professor = professorDAO.getProfessorByName(line[professorIndex]);
            Event event = eventDAO.getEventsByClass(classE)
                    .stream()
                    .filter(e -> e.getSubject().equalsIgnoreCase(line[eventIndex]))
                    .findFirst()
                    .orElse(null);
            int day = Integer.parseInt(line[dayIndex]);
            int time = Integer.parseInt(line[timeIndex]);
            EventAssignment eventAssignment = new EventAssignment();
            eventAssignment.setProfessor(professor);
            eventAssignment.setClassE(classE);
            eventAssignment.setEvent(event);
            eventAssignment.setDay(day);
            eventAssignment.setTime(time);
            eventAssignments.add(eventAssignment);
        });

        return eventAssignments;
    }

    private List<Unavailability> getUnavailabilities(String[] unavailabilitiesLine, int unavailabilityType) {
        List<String> unavailabityList = Arrays.asList(unavailabilitiesLine);
        List<Unavailability> unavailabilities = new ArrayList<>();
        unavailabityList.forEach(line -> {
            String[] lineVet = line.split(",");
            if (lineVet.length == 2) {
                int day = Integer.parseInt(lineVet[0]);
                int time = Integer.parseInt(lineVet[1]);
                Unavailability u = new Unavailability(day, time, unavailabilityType);
                unavailabilities.add(u);
            }
        });
        return unavailabilities;
    }

    private List<UndesiredPattern> getUndesiredPatterns(String patterns) {
        String[] patternsVet = patterns.split("\\+");
        return Arrays.stream(patternsVet)
                .map(line -> new UndesiredPattern(line.trim().toCharArray()))
                .collect(Collectors.toList());
    }

    public List<Violation> importSolutionViolations(File solutionFile) throws IOException {
        return null;
    }

    public class InvalidFileFormatException extends Exception {
        public InvalidFileFormatException(String expectedFormat, String foundFormat) {
            super("The file does not have the correct format. Expected Format: "
                    .concat(expectedFormat)
                    .concat(" Found Format").concat(foundFormat)
            );
        }
    }

    //TODO -- Include Preassigned Events
    private Event getEvent(String className,
                           String subject,
                           String tag,
                           String duration,
                           String professor,
                           String preassigned,
                           String minDaysGap,
                           String maxDaysGap
    ) {
        Event event = new Event();
        event.setSubject(subject);
        event.setTag(tag);
        event.setDuration(Integer.parseInt(duration));
        event.setSplit(duration);
        event.setMinGapBetweenMeetings(Integer.parseInt(minDaysGap));
        event.setMaxGapBetweenMeetings(Integer.parseInt(maxDaysGap));
        event.setClassE(getClassFromDb(className));
        event.setProfessorWeights(getProfessorWeights(professor.split("\\+")));
        EventDAOImpl.getInstance().persistEvent(event);
        return event;
    }

    private Professor getProfessor(String name,
                                   String workload,
                                   String minDays,
                                   String maxDays,
                                   String unavailableTimes,
                                   String undesiredTimes,
                                   String undesiredPatterns,
                                   String priority,
                                   String tag
    ) {
        Professor professor = getProfessorFromDb(name);
        if (!workload.isEmpty()) professor.setWorkload(Integer.parseInt(workload));
        if (!minDays.isEmpty()) professor.setPreferredMinNumberOfWorkingDays(Integer.parseInt(minDays));
        if (!maxDays.isEmpty()) professor.setPreferredMaxNumberOfWorkingDays(Integer.parseInt(maxDays));
        if (!priority.isEmpty()) professor.setPriority(Double.parseDouble(priority));
        List<Unavailability> unavailableTimesList = getUnavailabilities(
                unavailableTimes.split("\\+"), Unavailability.HARD_UNAVAILABILITY
        );
        List<Unavailability> undesiredTimesList = getUnavailabilities(
                undesiredTimes.split("\\+"), Unavailability.SOFT_UNAVAILABILITY
        );
        List<UndesiredPattern> undesiredPatternsList = getUndesiredPatterns(undesiredPatterns);
        professor.getUnavailabilities().addAll(unavailableTimesList);
        professor.getUnavailabilities().addAll(undesiredTimesList);
        professor.setUndesiredPatterns(undesiredPatternsList);
        professor.setTag(tag);
        ProfessorDAOImpl.getInstance().updateProfessor(professor);
        return professor;
    }

    private ClassE getClassE(String className, String unavailability) {
        ClassE classE = getClassFromDb(className);
        classE.setUnavailabilities(
                getUnavailabilities(unavailability.split("\\+"), Unavailability.HARD_UNAVAILABILITY)
        );
        ClassDAOImpl.getInstance().updateClass(classE);
        return classE;
    }

    private List<ProfessorWeight> getProfessorWeights(String[] professors) {
        return Arrays.stream(professors)
                .map(p -> new ProfessorWeight(getProfessorFromDb(p), 0))
                .collect(Collectors.toList());
    }

    private ClassE getClassFromDb(String className) {
        try {
            return ClassDAOImpl.getInstance().getClassByName(className);
        } catch (NoResultException e) {
            ClassE classE = new ClassE();
            classE.setName(className);
            ClassDAOImpl.getInstance().persistClass(classE);
            return classE;
        }
    }

    private Professor getProfessorFromDb(String name) {
        try {
            return ProfessorDAOImpl.getInstance().getProfessorByName(name);
        } catch (NoResultException e) {
            Professor professor = new Professor();
            professor.setName(name);
            ProfessorDAOImpl.getInstance().persistProfessor(professor);
            return professor;
        }
    }

    private String unavailabilityToString(List<Unavailability> unavailabilityList){
        return unavailabilityList.stream()
                .map(unavailability -> unavailability.toString())
                .reduce((u1, u2) -> u1.concat("+").concat(u2))
                .orElse("");
    }
}
