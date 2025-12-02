package br.com.fiap.lambda.service;

import br.com.fiap.lambda.dto.DetailsFeedbackDTO;
import br.com.fiap.lambda.dto.FeedbackDTO;
import com.amazonaws.services.lambda.runtime.Context;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class PDFGeneratorService {

    public static byte[] gerarPdf(Context context, List<FeedbackDTO> data, List<DetailsFeedbackDTO> dataDetails) throws DocumentException {

        context.getLogger().log("Iniciando geração do PDF...");

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

        dataDetails.forEach(f -> {
            addColoredCell(tableDetails, f.getCourseName(), Color.WHITE, false);
            addColoredCell(tableDetails, f.getTeacherName(), Color.WHITE, false);
            addColoredCell(tableDetails, String.valueOf(f.getAverageRating()), Color.WHITE, false);
            addColoredCell(tableDetails, String.valueOf(f.getCountTotalRates()), Color.WHITE, false);
            addColoredCell(tableDetails, String.valueOf(f.getCountTotalBadRates()), Color.WHITE, false);
        });

        document.add(tableDetails);
        document.close();

        context.getLogger().log("PDF gerado com sucesso!");

        return out.toByteArray();
    }

    private static void addColoredCell(PdfPTable table, String text, Color bgColor, boolean isBold) {
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