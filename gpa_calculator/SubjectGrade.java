public class SubjectGrade {
    private String classCode;
    private String description;
    private int credit;
    private int grades;

    public SubjectGrade(String classCode, String description, int credit, int grades) {
        this.classCode = classCode;
        this.description = description;
        this.credit = credit;
        this.grades = grades;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getDescription() {
        return description;
    }

    public int getCredit() {
        return credit;
    }

    public int getGrades() {
        return grades;
    }
}
