package br.com.fiap.lambda.dto;

public class FeedbackDTO {

    private String description;
    private Long rating;
    private String studentName;
    private String teacherName;
    private String courseName;

    public FeedbackDTO(String description, Long rating, String studentName, String teacherName, String courseName) {
        this.description = description;
        this.rating = rating;
        this.studentName = studentName;
        this.teacherName = teacherName;
        this.courseName = courseName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
