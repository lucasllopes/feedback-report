package br.com.fiap.lambda;

import br.com.fiap.lambda.dto.DetailsFeedbackDTO;
import br.com.fiap.lambda.dto.FeedbackDTO;
import br.com.fiap.lambda.repository.FeedbackReportRepository;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.inject.Inject;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

public class ReportLambda implements RequestHandler<FeedbackRequest, String> {

    @Inject
    SesClient sesClient;

    @Inject
    FeedbackReportRepository repository;

    private static final String REMETENTE_FIXO = "caikerodriguesqueiroz@gmail.com";

    @Override
    public String handleRequest(FeedbackRequest input, Context context) {

        try {
            byte[] pdfBytes = gerarPdf(context);

            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session);

            message.setSubject(input.getSubject());
            message.setFrom(new InternetAddress(REMETENTE_FIXO));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(input.getEmail()));

            MimeMultipart multipart = new MimeMultipart();

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Olá, \n\nSegue em anexo o relatório consolidado de feedbacks dos alunos.\n\nAtenciosamente,\nLambda FIAP");
            multipart.addBodyPart(textPart);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(pdfBytes, "application/pdf");
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("relatorio_feedbacks_" + LocalDateTime.now() + ".pdf" );
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);

            SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder()
                    .rawMessage(RawMessage.builder()
                            .data(SdkBytes.fromByteArray(outputStream.toByteArray()))
                            .build())
                    .build();

            sesClient.sendRawEmail(rawEmailRequest);

            return "Email com PDF enviado de " + REMETENTE_FIXO + " para: " + input.getEmail();

        } catch (Exception e) {
            context.getLogger().log("Erro crítico: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private byte[] gerarPdf(Context context) throws DocumentException {


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);

        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titulo = new Paragraph("Relatório de Feedbacks", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        addColoredCell(table, "Comentário", Color.WHITE, true);
        addColoredCell(table, "Avaliação", Color.WHITE, true);
        addColoredCell(table, "Nome do Aluno", Color.WHITE, true);
        addColoredCell(table, "Nome do Professor", Color.WHITE, true);
        addColoredCell(table, "Nome do Curso", Color.WHITE, true);

        List<FeedbackDTO> data = repository.listLastFeedbacks(context);

        data.forEach(f -> {
            addColoredCell(table, f.getDescription(), Color.WHITE, false);
            addColoredCell(table, String.valueOf(f.getRating()), f.getRating() <= 5 ? new Color(255, 107, 107) : Color.WHITE, false);
            addColoredCell(table, f.getStudentName(),Color.WHITE, false);
            addColoredCell(table, f.getTeacherName(),Color.WHITE, false);
            addColoredCell(table, f.getCourseName(),Color.WHITE, false);
        });

        document.add(table);

        document.newPage();

        Font fontTituloDetalhes = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph tituloDetalhes = new Paragraph("Métricas de Feedbacks", fontTituloDetalhes);
        tituloDetalhes.setAlignment(Element.ALIGN_CENTER);
        tituloDetalhes.setSpacingAfter(20);
        document.add(tituloDetalhes);

        PdfPTable tableDetails = new PdfPTable(5);
        tableDetails.setWidthPercentage(100);

        addColoredCell(tableDetails, "Curso", new Color(211, 211, 211), true);
        addColoredCell(tableDetails, "Nome do Professor", new Color(211, 211, 211), true);
        addColoredCell(tableDetails, "Média das Avaliações", new Color(211, 211, 211), true);
        addColoredCell(tableDetails, "Total de Avaliações", new Color(211, 211, 211), true);
        addColoredCell(tableDetails, "Total de Avaliações(Ruins)", new Color(211, 211, 211), true);

        List<DetailsFeedbackDTO> dataDetails = repository.listDetailsFeedbacks(context);

        dataDetails.forEach(f -> {
            addColoredCell(tableDetails, f.getCourseName(), new Color(211, 211, 211), false);
            addColoredCell(tableDetails, f.getTeacherName(), new Color(211, 211, 211), false);
            addColoredCell(tableDetails, String.valueOf(f.getAverageRating()), new Color(211, 211, 211), false);
            addColoredCell(tableDetails, String.valueOf(f.getCountTotalRates()), new Color(211, 211, 211), false);
            addColoredCell(tableDetails, String.valueOf(f.getCountTotalBadRates()), new Color(211, 211, 211), false);
        });

        document.add(tableDetails);
        document.close();

        return out.toByteArray();
    }

    private void addColoredCell(PdfPTable table, String text, Color bgColor, boolean isBold) {
        Font font = isBold ?
                FontFactory.getFont(FontFactory.HELVETICA_BOLD) :
                FontFactory.getFont(FontFactory.HELVETICA);

        PdfPCell cell = new PdfPCell(new Phrase(text, font));

        cell.setPadding(5);

        if (bgColor != null) {
            cell.setBackgroundColor(bgColor);
        }

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(cell);
    }

}