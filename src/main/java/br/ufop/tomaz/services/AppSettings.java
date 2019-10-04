package br.ufop.tomaz.services;

import br.ufop.tomaz.App;
import br.ufop.tomaz.model.Constraint;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import br.ufop.tomaz.util.Day;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AppSettings {

    private static AppSettings appSettings = null;
    private List<String> daysList;
    private List<String> timesList;
    private List<Constraint> constraintList;

    private AppSettings() {
        try {
            File settingsFiles = getSettingsFile();
            this.daysList = getDaysListFromXML(settingsFiles);
            this.timesList = getTimesListFromXML(settingsFiles);
            this.constraintList = getConstraintListFromXML(settingsFiles);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static AppSettings getInstance() {
        if (appSettings == null)
            appSettings = new AppSettings();
        return appSettings;
    }

    public void saveSettings() {
        Element settingsXML = new Element("settings");
        Document documentXML = new Document(settingsXML);
        settingsXML.addContent(getDaysXML(this.daysList));
        settingsXML.addContent(getTimesXML(this.timesList));
        settingsXML.addContent(getConstraintsXML(this.constraintList));
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        try {
            String filepath = App.APP_DIRECTORY.concat("/settings.xml");
            OutputStream out = new FileOutputStream(new File(filepath));
            xmlOutputter.output(documentXML, out);
            out.close();
            System.out.println("Saving ...");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Element getDaysXML(List<String> daysList) {
        Element days = new Element("days");
        List<Element> list = new ArrayList<>();
        for (String day : daysList) {
            Element e = new Element("day");
            e.setText(day);
            list.add(e);
        }
        days.addContent(list);
        return days;
    }

    private Element getTimesXML(List<String> timesList) {
        Element times = new Element("times");
        List<Element> list = new ArrayList<>();
        for (String time : timesList) {
            Element e = new Element("time");
            e.setText(time);
            list.add(e);
        }
        times.addContent(list);
        return times;
    }

    private List<String> getDaysListFromXML(File settingsFile) {

        SAXBuilder builder = new SAXBuilder();

        try {
            Document document = builder.build(settingsFile);
            Element settings = document.getRootElement();
            Element days = settings.getChild("days");

            return days.getChildren()
                    .stream()
                    .map(Element::getText)
                    .collect(Collectors.toList());

        } catch (JDOMException | IOException | NullPointerException e) {
            e.printStackTrace();
            return Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        }
    }

    private List<String> getTimesListFromXML(File settingsFile) {
        SAXBuilder builder = new SAXBuilder();
        try {
            Document document = builder.build(settingsFile);
            Element settings = document.getRootElement();
            Element times = settings.getChild("times");
            return times.getChildren()
                    .stream()
                    .map(Element::getText)
                    .collect(Collectors.toList());

        } catch (JDOMException | IOException | NullPointerException e) {
            e.printStackTrace();
            return Arrays.asList("Time 1", "Time 2", "Time 3", "Time 4");
        }
    }

    private List<Constraint> getConstraintListFromXML(File settingsFile) {
        SAXBuilder builder = new SAXBuilder();

        try{
            Document document = builder.build(settingsFile);
            Element settings = document.getRootElement();
            Element constraints = settings.getChild("constraints");
            return constraints.getChildren()
                    .stream()
                    .map(this::getConstraintFromXML)
                    .collect(Collectors.toList());
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Constraint getConstraintFromXML(Element element){
        String typeText = element.getChildText("type")
                .replace(' ', '_')
                .toUpperCase();
        Constraint.ConstraintType type = Constraint.ConstraintType.valueOf(typeText);
        int weight = Integer.parseInt(element.getChildText("weight"));
        String info = element.getChildText("info");
        String applyTo = element.getChildText("applyTo");
        return new Constraint(type, applyTo, info, weight);
    }

    private Element getConstraintsXML(List<Constraint> constraintList){
        List<Element> constraintsXml = constraintList.stream()
                .map(constraint -> {
                    Element weight = new Element("weight");
                    Element info = new Element("info");
                    Element applyTo = new Element("applyTo");
                    Element type = new Element("type");

                    weight.setText(String.valueOf(constraint.getWeight()));
                    info.setText(constraint.getInfo());
                    applyTo.setText(constraint.getApplyTo());
                    type.setText(constraint.getType());

                    return new Element("constraint").addContent(Arrays.asList(type, applyTo, weight, info));
                }).collect(Collectors.toList());

        return new Element("constraints").addContent(constraintsXml);
    }

    public List<String> getDaysList() {
        return daysList;
    }

    public void setDaysList(List<String> daysList) {
        this.daysList = daysList;
    }

    public List<String> getTimesList() {
        return timesList;
    }

    public void setTimesList(List<String> timesList) {
        this.timesList = timesList;
    }

    public List<Constraint> getConstraintList() {
        return constraintList;
    }

    public void setConstraintList(List<Constraint> constraintList) {
        this.constraintList = constraintList;
    }

    public List<Day> defaultDays() {
        return daysList.stream().map(d -> new Day(d, timesList)).collect(Collectors.toList());
    }

    private File getSettingsFile(){
        String filepath = App.APP_DIRECTORY.concat("/settings.xml");
        return (Files.exists(Path.of(filepath))) ?
                new File(filepath) : new File(getClass().getResource("/META-INF/settings.xml").getPath());
    }

}
