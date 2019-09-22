package br.ufop.tomaz.model;


import br.ufop.tomaz.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Solution {

    private File solutionFile;
    private List<EventAssignment> eventAssignments;
    private List<Violation> violations;

    public Solution(File solutionFile) {
        this.solutionFile = solutionFile;
        initAssignments();
        initViolations();
    }

    private void initAssignments() {
        try {
            this.eventAssignments = new FileUtils().importEventsAssignments(solutionFile);
        } catch (IOException e) {
            this.eventAssignments = new ArrayList<>();
            e.printStackTrace();
        }
    }

    private void initViolations() {
        try {
            this.violations = new FileUtils().importSolutionViolations(solutionFile);
        } catch (IOException e) {
            this.violations = new ArrayList<>();
            e.printStackTrace();
        }
    }
}
