package main.java.data.addition;

import java.util.HashMap;
import java.util.Map;

public class Course {

    private String courseNumber;
    private Map<String, Semester> semesters = new HashMap<>();

    public Map<String, Semester> getSemesters() {
        return semesters;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

}
