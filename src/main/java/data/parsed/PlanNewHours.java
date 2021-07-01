package main.java.data.parsed;

public class PlanNewHours {

    private String objectCode;
    private String course;
    private String semester;
    private String session;
    private String hoursCount;
    private String typeOfWorkCode;


    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse (String course) {
        this.course = course;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getHoursCount() {
        return hoursCount;
    }

    public void setHoursCount(String hoursCount) {
        this.hoursCount = hoursCount;
    }

    public String getTypeOfWorkCode() {
        return typeOfWorkCode;
    }

    public void setTypeOfWorkCode(String typeOfWorkCode) {
        this.typeOfWorkCode = typeOfWorkCode;
    }
}
