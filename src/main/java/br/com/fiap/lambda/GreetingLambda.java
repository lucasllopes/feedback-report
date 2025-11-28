package br.com.fiap.lambda;

import br.com.fiap.lambda.dto.FeedbackDTO;
import br.com.fiap.lambda.repository.FeedbackReportRepository;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
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

import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Properties;

@Named("greeting")
public class GreetingLambda implements RequestHandler<FeedbackRequest, String> {

    @Inject
    SesClient sesClient;

    @Inject
    FeedbackReportRepository repository;

    private static final String REMETENTE_FIXO = "caikerodriguesqueiroz@gmail.com";

    @Override
    public String handleRequest(FeedbackRequest input, Context context) {

        try {
            // 1. Gerar o PDF em memória
            byte[] pdfBytes = gerarPdf();

            // 2. Criar o objeto da mensagem de e-mail (MIME)
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session);

            message.setSubject(input.getSubject());
            message.setFrom(new InternetAddress(REMETENTE_FIXO));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(input.getEmail()));

            // 3. Criar as partes do e-mail (Texto + Anexo)
            MimeMultipart multipart = new MimeMultipart();

            // Parte A: O texto do corpo do e-mail
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Olá, \n\nSegue em anexo o relatório consolidado de feedbacks dos alunos.\n\nAtenciosamente,\nLambda FIAP");
            multipart.addBodyPart(textPart);

            // Parte B: O anexo PDF
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(pdfBytes, "application/pdf");
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("relatorio_feedbacks.pdf");
            multipart.addBodyPart(attachmentPart);

            // Juntar tudo na mensagem
            message.setContent(multipart);

            // 4. Converter a mensagem Java Mail para bytes que a AWS entende
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);

            // 5. Enviar usando SendRawEmail
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

    private byte[] gerarPdf() throws DocumentException {
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

        addHeader(table, "Comentário");
        addHeader(table, "Nota");
        addHeader(table, "Nome do Aluno");
        addHeader(table, "Nome do Professor");
        addHeader(table, "Nome do Curso");


        List<FeedbackDTO> data = repository.listLastFeedbacks();

        data.forEach(f -> {
            table.addCell(f.getDescription());
            table.addCell(String.valueOf(f.getRating()));
            table.addCell(f.getStudentName());
            table.addCell(f.getTeacherName());
            table.addCell(f.getCourseName());
        });

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    private void addHeader(PdfPTable table, String text) {
        PdfPCell header = new PdfPCell();
        header.setPhrase(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}