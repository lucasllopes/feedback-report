package br.com.fiap.lambda.dto;

public class DetailsFeedbackDTO {
    private String courseName;
    private String teacherName;
    private Double averageRating;
    private Integer countTotalRates;
    private Integer countTotalBadRates;

    public DetailsFeedbackDTO(String courseName, String teacherName, Double averageRating, Integer countTotalRates, Integer countTotalBadRates) {
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.averageRating = averageRating;
        this.countTotalRates = countTotalRates;
        this.countTotalBadRates = countTotalBadRates;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getCountTotalRates() {
        return countTotalRates;
    }

    public void setCountTotalRates(Integer countTotalRates) {
        this.countTotalRates = countTotalRates;
    }

    public Integer getCountTotalBadRates() {
        return countTotalBadRates;
    }

    public void setCountTotalBadRates(Integer countTotalBadRates) {
        this.countTotalBadRates = countTotalBadRates;
    }
}
