package br.com.fiap.lambda.dto;

public class DetailsFeedbackDTO {
    private String courseName;
    private String teacherName;
    private Double averageRating;
    private Long countTotalRates;
    private Long countTotalBadRates;

    public DetailsFeedbackDTO(String courseName, String teacherName, Double averageRating, Long countTotalRates, Long countTotalBadRates) {
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

    public Long getCountTotalRates() {
        return countTotalRates;
    }

    public void setCountTotalRates(Long countTotalRates) {
        this.countTotalRates = countTotalRates;
    }

    public Long getCountTotalBadRates() {
        return countTotalBadRates;
    }

    public void setCountTotalBadRates(Long countTotalBadRates) {
        this.countTotalBadRates = countTotalBadRates;
    }
}
