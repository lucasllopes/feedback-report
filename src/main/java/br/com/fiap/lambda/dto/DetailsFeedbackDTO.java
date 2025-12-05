package br.com.fiap.lambda.dto;


public class DetailsFeedbackDTO {
    private String courseName;
    private String teacherName;
    private Double average;
    private String ratingDate;
    private Long totalRatesPerDay;
    private Long totalRatesPerCriticalUrgency;
    private Long totalRatesPerHighUrgency;
    private Long totalRatesPerMediumUrgency;
    private Long totalRatesPerLowUrgency;

    public DetailsFeedbackDTO(String courseName, String teacherName, Double average, String ratingDate, Long totalRatesPerDay, Long totalRatesPerCriticalUrgency, Long totalRatesPerHighUrgency, Long totalRatesPerMediumUrgency, Long totalRatesPerLowUrgency) {
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.average = average;
        this.ratingDate = ratingDate;
        this.totalRatesPerDay = totalRatesPerDay;
        this.totalRatesPerCriticalUrgency = totalRatesPerCriticalUrgency;
        this.totalRatesPerHighUrgency = totalRatesPerHighUrgency;
        this.totalRatesPerMediumUrgency = totalRatesPerMediumUrgency;
        this.totalRatesPerLowUrgency = totalRatesPerLowUrgency;
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

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public String getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(String ratingDate) {
        this.ratingDate = ratingDate;
    }

    public Long getTotalRatesPerDay() {
        return totalRatesPerDay;
    }

    public void setTotalRatesPerDay(Long totalRatesPerDay) {
        this.totalRatesPerDay = totalRatesPerDay;
    }

    public Long getTotalRatesPerCriticalUrgency() {
        return totalRatesPerCriticalUrgency;
    }

    public void setTotalRatesPerCriticalUrgency(Long totalRatesPerCriticalUrgency) {
        this.totalRatesPerCriticalUrgency = totalRatesPerCriticalUrgency;
    }

    public Long getTotalRatesPerHighUrgency() {
        return totalRatesPerHighUrgency;
    }

    public void setTotalRatesPerHighUrgency(Long totalRatesPerHighUrgency) {
        this.totalRatesPerHighUrgency = totalRatesPerHighUrgency;
    }

    public Long getTotalRatesPerMediumUrgency() {
        return totalRatesPerMediumUrgency;
    }

    public void setTotalRatesPerMediumUrgency(Long totalRatesPerMediumUrgency) {
        this.totalRatesPerMediumUrgency = totalRatesPerMediumUrgency;
    }

    public Long getTotalRatesPerLowUrgency() {
        return totalRatesPerLowUrgency;
    }

    public void setTotalRatesPerLowUrgency(Long totalRatesPerLowUrgency) {
        this.totalRatesPerLowUrgency = totalRatesPerLowUrgency;
    }
}
