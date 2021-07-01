package main.java.outputData;

import main.java.data.addition.Course;

import java.util.HashMap;
import java.util.Map;

public class TXTRow {
    private String subject;
    private String firstSemester;
    private String secondSemester;
    private String thirdSemester;
    private String fourthSemester;
    private String titleFull;
    private Map<String, Course> courses = new HashMap<>();

    public String getTitleFull() {
        return titleFull;
    }

    public void setTitleFull(String titleFull) {
        this.titleFull = titleFull;
    }

    public String getFirstSemester() {
        return firstSemester;
    }

    public void setFirstSemester(String firstSemester) {
        this.firstSemester = firstSemester;
    }

    public String getSecondSemester() {
        return secondSemester;
    }

    public void setSecondSemester(String secondSemester) {
        this.secondSemester = secondSemester;
    }

    public String getThirdSemester() {
        return thirdSemester;
    }

    public void setThirdSemester(String thirdSemester) {
        this.thirdSemester = thirdSemester;
    }

    public String getFourthSemester() {
        return fourthSemester;
    }

    public void setFourthSemester(String fourthSemester) {
        this.fourthSemester = fourthSemester;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, Course> getCourses() {
        return courses;
    }

}
