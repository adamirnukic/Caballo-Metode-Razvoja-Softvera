package main.caballo.util;

import main.caballo.model.DailyItemReport;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public final class PdfReportUtil {
    private PdfReportUtil() {}

    public static void generateDailySales(LocalDate date, double total, List<DailyItemReport> items, Path output) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            InputStream fontStream = PdfReportUtil.class.getResourceAsStream("/fonts/DejaVuSans.ttf");
            InputStream boldFontStream = PdfReportUtil.class.getResourceAsStream("/fonts/DejaVuSans-Bold.ttf");

            if (fontStream == null || boldFontStream == null) {
                throw new IOException("Font resources not found under /fonts in classpath");
            }

            PDFont regular = PDType0Font.load(doc, fontStream, true);
            PDFont bold = PDType0Font.load(doc, boldFontStream, true);

            float marginLeft = 40f;
            float rowHeight = 18f;

            float colItemName = 130f;
            float colOpening = 50f;
            float colReceived = 50f;
            float colSold = 50f;
            float colExpected = 60f;
            float colPhysical = 60f;
            float colTotal = 70f;
            float colNote = 100f;

            float y;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                y = PDRectangle.A4.getHeight() - 50;
                cs.setFont(bold, 18);
                cs.beginText();
                cs.newLineAtOffset(50, y);
                cs.showText("Caballo Restaurant - Daily Sales");
                cs.endText();

                y -= 30;
                cs.setFont(regular, 12);
                cs.beginText();
                cs.newLineAtOffset(50, y);
                cs.showText("Date: " + date);
                cs.endText();

                y -= 20;
                cs.beginText();
                cs.newLineAtOffset(50, y);
                cs.showText(String.format("Total Revenue: %.2f BAM", total));
                cs.endText();

                y -= 30;
                cs.setFont(bold, 14);
                cs.beginText();
                cs.newLineAtOffset(50, y);
                cs.showText("Dnevni pregled artikala");
                cs.endText();

                y -= 25;
                cs.setFont(bold, 10);

                cs.beginText();
                cs.newLineAtOffset(marginLeft, y);
                cs.showText("Artikal");
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(marginLeft + colItemName, y);
                cs.showText("Početni");
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(marginLeft + colItemName + colOpening, y);
                cs.showText("Primljeno");
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived, y);
                cs.showText("Prodano");
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived + colSold, y);
                cs.showText("Očekivano");
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived + colSold + colExpected, y);
                cs.showText("Fizičko");
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived + colSold + colExpected + colPhysical, y);
                cs.showText("Ukupno (KM)");
                cs.endText();

                cs.beginText();
                cs.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived + colSold + colExpected + colPhysical + colTotal, y);
                cs.showText("Napomena");
                cs.endText();

                y -= rowHeight;
            }

            java.util.function.BiFunction<PDPage, Float, Float> drawHeader = (pg, startY) -> {
                try (PDPageContentStream csHeader = new PDPageContentStream(doc, pg)) {
                    csHeader.setFont(bold, 10);

                    csHeader.beginText();
                    csHeader.newLineAtOffset(marginLeft, startY);
                    csHeader.showText("Artikal");
                    csHeader.endText();

                    csHeader.beginText();
                    csHeader.newLineAtOffset(marginLeft + colItemName, startY);
                    csHeader.showText("Početni");
                    csHeader.endText();

                    csHeader.beginText();
                    csHeader.newLineAtOffset(marginLeft + colItemName + colOpening, startY);
                    csHeader.showText("Primljeno");
                    csHeader.endText();

                    csHeader.beginText();
                    csHeader.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived, startY);
                    csHeader.showText("Prodano");
                    csHeader.endText();

                    csHeader.beginText();
                    csHeader.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived + colSold, startY);
                    csHeader.showText("Očekivano");
                    csHeader.endText();

                    csHeader.beginText();
                    csHeader.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived + colSold + colExpected, startY);
                    csHeader.showText("Fizičko");
                    csHeader.endText();

                    csHeader.beginText();
                    csHeader.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived + colSold + colExpected + colPhysical, startY);
                    csHeader.showText("Ukupno (KM)");
                    csHeader.endText();

                    csHeader.beginText();
                    csHeader.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived + colSold + colExpected + colPhysical + colTotal, startY);
                    csHeader.showText("Napomena");
                    csHeader.endText();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return startY - rowHeight;
            };

            float currentY = y;

            for (DailyItemReport r : items) {
                if (currentY < 60) {
                    page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    currentY = PDRectangle.A4.getHeight() - 80;
                    currentY = drawHeader.apply(page, currentY);
                }

                try (PDPageContentStream rowStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true)) {
                    rowStream.setFont(regular, 10);

                    rowStream.beginText();
                    rowStream.newLineAtOffset(marginLeft, currentY);
                    rowStream.showText(r.getItemName() != null ? r.getItemName() : "");
                    rowStream.endText();

                    rowStream.beginText();
                    rowStream.newLineAtOffset(marginLeft + colItemName, currentY);
                    rowStream.showText(String.valueOf(r.getOpeningQty()));
                    rowStream.endText();

                    rowStream.beginText();
                    rowStream.newLineAtOffset(marginLeft + colItemName + colOpening, currentY);
                    rowStream.showText(String.valueOf(r.getReceivedQty()));
                    rowStream.endText();

                    rowStream.beginText();
                    rowStream.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived, currentY);
                    rowStream.showText(String.valueOf(r.getSoldQty()));
                    rowStream.endText();

                    rowStream.beginText();
                    rowStream.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived + colSold, currentY);
                    rowStream.showText(String.valueOf(r.getExpectedClosingQty()));
                    rowStream.endText();

                    rowStream.beginText();
                    rowStream.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived + colSold + colExpected, currentY);
                    rowStream.showText(String.valueOf(r.getPhysicalClosingQty()));
                    rowStream.endText();

                    rowStream.beginText();
                    rowStream.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived + colSold + colExpected + colPhysical, currentY);
                    rowStream.showText(String.format("%.2f", r.getSoldTotalAmount()));
                    rowStream.endText();

                    rowStream.beginText();
                    rowStream.newLineAtOffset(marginLeft + colItemName + colOpening + colReceived + colSold + colExpected + colPhysical + colTotal, currentY);
                    rowStream.showText(r.getNote() != null ? r.getNote() : "");
                    rowStream.endText();
                }

                currentY -= rowHeight;
            }

            doc.save(output.toFile());
        }
    }
}
