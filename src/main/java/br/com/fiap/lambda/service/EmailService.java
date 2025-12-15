package br.com.fiap.lambda.service;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.amazonaws.services.lambda.runtime.Context;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

@ApplicationScoped
public class EmailService {

    @Inject
    SesClient sesClient;

    @ConfigProperty(name = "aws.ses.sender")
    private String REMETENTE;

    public void sendEmailWithAttachment(Context context, List<String> receivers, String body, byte[] pdfBytes, String fileName) {

        DataSource source = new ByteArrayDataSource(pdfBytes, "application/pdf");
        String subject = "Relatório de Feedbacks | " +
                LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " - " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        receivers.forEach(receiver -> {

            try {

                context.getLogger().log("Iniciando envio de email para " + receivers + "...");

                Session session = Session.getDefaultInstance(new Properties());
                MimeMessage message = new MimeMessage(session);

                message.setSubject(subject);
                message.setFrom(new InternetAddress(REMETENTE));
                message.setRecipients(jakarta.mail.Message.RecipientType.TO, receiver);

                MimeMultipart multipart = new MimeMultipart();

                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(body);
                multipart.addBodyPart(textPart);

                MimeBodyPart attachmentPart = new MimeBodyPart();

                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(fileName);
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

                context.getLogger().log("Email enviado de " + REMETENTE + " para " + receiver + " com sucesso em " + LocalDateTime.now());

            } catch (MessagingException | IOException e) {
                throw new RuntimeException("Erro ao construir ou enviar email: " + e.getMessage(), e);
            }

        });
    }

        public void sendSimpleEmail(Context context, List<String> receivers, String body) {

            String subject = "Relatório de Feedbacks | " +
                    LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    " - " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            for (String receiver : receivers) {
                SendEmailRequest request = SendEmailRequest.builder()
                        .source(REMETENTE)
                        .destination(Destination.builder()
                                .toAddresses(receiver)
                                .build())
                        .message(Message.builder()
                                .subject(Content.builder().data(subject).build())
                                .body(Body.builder()
                                        .text(Content.builder().data(body).build())
                                        .build())
                                .build())
                        .build();

                sesClient.sendEmail(request);
            }

            context.getLogger().log("Envio de emails simples finalizado.");
        }

}


