package br.com.fiap.lambda;

import br.com.fiap.lambda.dto.DetailsFeedbackDTO;
import br.com.fiap.lambda.dto.FeedbackDTO;
import br.com.fiap.lambda.repository.FeedbackReportRepository;
import br.com.fiap.lambda.service.EmailService;
import br.com.fiap.lambda.service.PDFGeneratorService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportLambda implements RequestHandler<FeedbackRequest, String> {

    @Inject
    EmailService emailService;

    @Inject
    FeedbackReportRepository repository;

    @Override
    public String handleRequest(FeedbackRequest input, Context context) {

        try {

            context.getLogger().log("Iniciando processamento do relatório...");

            List<FeedbackDTO> data = repository.listLastFeedbacks(context);
            List<DetailsFeedbackDTO> dataDetails = repository.listDetailsFeedbacks(context);

            byte[] pdfBytes = PDFGeneratorService.gerarPdf(context, data, dataDetails);

            String fileName = "relatorio_feedbacks_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf";

            String emailBody = "Olá, " +
                    "\n\nSegue em anexo o relatório consolidado de feedbacks dos alunos." +
                    "\n\nAtenciosamente," +
                    "\nFeedback Hub";

            emailService.sendEmailWithAttachment(
                    context,
                    input.getEmail(),
                    input.getSubject(),
                    emailBody,
                    pdfBytes,
                    fileName
            );

            return "Processamento concluído com sucesso.";
        } catch (Exception e) {
            context.getLogger().log("Erro crítico na Lambda: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}