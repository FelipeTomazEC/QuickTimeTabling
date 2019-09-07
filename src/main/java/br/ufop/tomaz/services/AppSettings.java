package br.ufop.tomaz.services;

import br.ufop.tomaz.App;
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

    private AppSettings() {
        try {
            File settingsFiles = getSettingsFile();
            this.daysList = getDaysListFromXML(settingsFiles);
            this.timesList = getTimesListFromXML(settingsFiles);
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
        settingsXML.addContent(getDaysXML());
        settingsXML.addContent(getTimesXML());
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

    private Element getDaysXML() {
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

    private Element getTimesXML() {
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

    public List<Day> defaultDays() {
        return daysList.stream().map(d -> new Day(d, timesList)).collect(Collectors.toList());
    }

    private File getSettingsFile(){
        String filepath = App.APP_DIRECTORY.concat("/settings.xml");
        return (Files.exists(Path.of(filepath))) ?
                new File(filepath) : new File(getClass().getResource("/META-INF/settings.xml").getPath());
    }

}
