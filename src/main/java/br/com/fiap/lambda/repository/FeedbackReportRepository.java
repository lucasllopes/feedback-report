package br.com.fiap.lambda.repository;

import br.com.fiap.lambda.dto.DetailsFeedbackDTO;
import br.com.fiap.lambda.dto.FeedbackDTO;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
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


    public List<FeedbackDTO> listLastFeedbacks(Context context) {

        LambdaLogger log = context.getLogger();

        String sql = "SELECT f.description, f.rating, s.name , t.name, c.name, f.created_at FROM feedback f " +
        "INNER JOIN class cl ON cl.id = f.class_id " +
        "INNER JOIN teacher t ON t.id = cl.teacher_id " +
        "INNER JOIN course c ON c.id = cl.course_id " +
        "INNER JOIN student s ON s.id = f.student_id " +
        "WHERE f.created_at >= NOW() - INTERVAL '7 days' " +
        "ORDER BY f.created_at DESC";

        ExecuteStatementResponse resp = executeStatement(sql);

        log.log("INFO: Dados: " + resp.toString());
        log.log("INFO: Feedbacks recuperados: " + resp.records().size() + "\n");


        List<FeedbackDTO> result = new ArrayList<>();

        for (List<Field> row : resp.records()) {
            String description = row.get(0).stringValue();
            Long rating = row.get(1).longValue();
            String studentName = row.get(2).stringValue();
            String teacherName = row.get(3).stringValue();
            String courseName = row.get(4).stringValue();
            String date = row.get(5).stringValue();
            result.add(new FeedbackDTO(description, rating, studentName, teacherName, courseName, date));
        }

        return result;
    }


    public List<DetailsFeedbackDTO> listDetailsFeedbacks(Context context) {

        LambdaLogger log = context.getLogger();

        String sql = "SELECT c.name, " +
                "t.name, " +
                "AVG(f.rating) AS AVERAGE, " +
                "DATE(f.created_at), " +
                "COUNT(f.id) AS TOTAL_RATES_PER_DAY, " +
                "SUM(CASE WHEN f.rating <= 3 THEN 1 ELSE 0 END) AS TOTAL_RATES_PER_CRITICAL_URGENCY, " +
                "SUM(CASE WHEN f.rating > 3 AND f.rating <= 5 THEN 1 ELSE 0 END) AS TOTAL_RATES_PER_HIGH_URGENCY, " +
                "SUM(CASE WHEN f.rating > 5 AND f.rating <= 7 THEN 1 ELSE 0 END) AS TOTAL_RATES_PER_MEDIUM_URGENCY, " +
                "SUM(CASE WHEN f.rating > 7 THEN 1 ELSE 0 END) AS TOTAL_RATES_PER_LOW_URGENCY " +
                "FROM course c " +
                "JOIN class cl ON c.id = cl.course_id  " +
                "JOIN teacher t ON t.id = cl.teacher_id  " +
                "JOIN feedback f ON f.class_id = cl.id  " +
                "GROUP BY c.name, " +
                "t.name, " +
                "DATE(f.created_at)";


        ExecuteStatementResponse resp = executeStatement(sql);

        log.log("INFO: Dados: " + resp.toString());
        log.log("INFO: Feedbacks recuperados: " + resp.records().size() + "\n");

        List<DetailsFeedbackDTO> result = new ArrayList<>();

        for (List<Field> row : resp.records()) {
            String courseName = row.get(0).stringValue();
            String teacherName = row.get(1).stringValue();
            String averageRating = String.format("%.2f",row.get(2).doubleValue());
            String ratingDate = row.get(3).stringValue();
            Long totalRatesPerDay = row.get(4).longValue();
            Long totalCritical = row.get(5).longValue();
            Long totalHigh = row.get(6).longValue();
            Long totalMedium = row.get(7).longValue();
            Long totalLow = row.get(8).longValue();

            result.add(new DetailsFeedbackDTO(
                    courseName,
                    teacherName,
                    averageRating,
                    ratingDate,
                    totalRatesPerDay,
                    totalCritical,
                    totalHigh,
                    totalMedium,
                    totalLow
            ));
        }

        return result;
    }

    public List<String> listActivesAdmins(Context context) {

        LambdaLogger log = context.getLogger();

        String sql = "SELECT a.email FROM admin a WHERE a.active = true";

        //TODO: ver se os professores vao receber relatorios
        //SELECT * FROM teacher t WHERE t.active = true;

        ExecuteStatementResponse resp = executeStatement(sql);

        log.log("INFO: Dados: " + resp.toString());
        log.log("INFO: Administradores Ativos: " + resp.records().size() + "\n");

        List<String> result = new ArrayList<>();

        for (List<Field> row : resp.records()) {
            String email = row.get(0).stringValue();
            result.add(email);
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
