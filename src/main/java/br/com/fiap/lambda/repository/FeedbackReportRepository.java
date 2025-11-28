package br.com.fiap.lambda.repository;

import br.com.fiap.lambda.dto.DetailsFeedbackDTO;
import br.com.fiap.lambda.dto.FeedbackDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FeedbackReportRepository {

    @Inject
    RdsDataClient rds;

    @ConfigProperty(name = "db.cluster-arn")
    String clusterArn;

    @ConfigProperty(name = "db.secret-arn")
    String secretArn;

    @ConfigProperty(name = "db.name")
    String database;


    public List<FeedbackDTO> listLastFeedbacks() {

        String sql = "SELECT f.description, f.rating, s.name , t.name, c.name FROM feedback f " +
        "INNER JOIN class cl ON cl.id = f.class_id " +
        "INNER JOIN teacher t ON t.id = cl.teacher_id " +
        "INNER JOIN course c ON c.id = cl.course_id " +
        "INNER JOIN student s ON s.id = f.student_id " +
        "WHERE f.created_at >= NOW() - INTERVAL '7 days' " +
        "ORDER BY f.created_at DESC";

        ExecuteStatementResponse resp = executeStatement(sql);

        List<FeedbackDTO> result = new ArrayList<>();

        for (List<Field> row : resp.records()) {
            String description = row.get(0).isNull() ? null : row.get(0).stringValue();
            Integer rating = Integer.parseInt(row.get(1).stringValue());
            String studentName = row.get(2).stringValue();
            String teacherName = row.get(3).stringValue();
            String courseName = row.get(4).stringValue();

            result.add(new FeedbackDTO(description, rating, studentName, teacherName, courseName));
        }

        return result;
    }


    public List<DetailsFeedbackDTO> listDetailsFeedbacks() {

        //TODO: montar a query para os detalhes
        String sql = "SELECT f.description, f.rating, s.name , t.name, c.name FROM feedback f " +
                "INNER JOIN class cl ON cl.id = f.class_id " +
                "INNER JOIN teacher t ON t.id = cl.teacher_id " +
                "INNER JOIN course c ON c.id = cl.course_id " +
                "INNER JOIN student s ON s.id = f.student_id " +
                "WHERE f.created_at >= NOW() - INTERVAL '7 days' " +
                "ORDER BY f.created_at DESC";


        ExecuteStatementResponse resp = executeStatement(sql);
        List<DetailsFeedbackDTO> result = new ArrayList<>();

        for (List<Field> row : resp.records()) {
            String courseName = row.get(0).isNull() ? null : row.get(0).stringValue();
            String teacherName = row.get(1).stringValue();
            Double averageRating = Double.parseDouble(row.get(2).stringValue());
            Integer countTotalRates = Integer.parseInt(row.get(3).stringValue());
            Integer countTotalBadRates = Integer.parseInt(row.get(4).stringValue());

            result.add(new DetailsFeedbackDTO(courseName, teacherName, averageRating, countTotalRates, countTotalBadRates));
        }

        return result;
    }

    private ExecuteStatementResponse executeStatement(String sql){
        ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                .resourceArn(clusterArn)
                .secretArn(secretArn)
                .database(database)
                .sql(sql)
                .build();

        return rds.executeStatement(request);
    }
}
