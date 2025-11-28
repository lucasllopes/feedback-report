package br.com.fiap.lambda;


public class FeedbackRequest {
    private String email;
    private String subject;
    //private List<StudentData> data;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    //public List<StudentData> getData() { return data; }
    //public void setData(List<StudentData> data) { this.data = data; }
}