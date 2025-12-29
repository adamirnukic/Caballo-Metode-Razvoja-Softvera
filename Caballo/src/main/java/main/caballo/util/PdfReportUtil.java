package main.caballo.util;

import main.caballo.model.DailyItemReport;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PdfReportUtil {
    private PdfReportUtil() {}

    private static final Color CAB_BG = new Color(0xF5, 0xF7, 0xFB);
    private static final Color CAB_SURFACE = new Color(0xFF, 0xFF, 0xFF);
    private static final Color CAB_BORDER = new Color(0xE2, 0xE8, 0xF0);
    private static final Color CAB_TEXT = new Color(0x0F, 0x17, 0x2A);
    private static final Color CAB_MUTED = new Color(0x64, 0x74, 0x8B);
    private static final Color CAB_PRIMARY = new Color(0x25, 0x63, 0xEB);
    private static final Color TABLE_HEADER_BG = new Color(0xF1, 0xF5, 0xF9);
    private static final Color ZEBRA_BG = new Color(0xFA, 0xFB, 0xFD);

    private static final float PAGE_MARGIN = 28f;

    private static final float FONT_TITLE = 18f;
    private static final float FONT_SUBTITLE = 10.5f;
    private static final float FONT_TABLE_HEADER = 9.5f;
    private static final float FONT_TABLE_BODY = 9.5f;
    private static final float FONT_FOOTER = 8.5f;

    private static final float LINE_GAP = 1.25f;

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void generateDailySales(LocalDate date, double total, List<DailyItemReport> items, Path output) throws IOException {
        LocalDate safeDate = date != null ? date : LocalDate.now();
        List<DailyItemReport> safeItems = items != null ? items : List.of();

        try (PDDocument doc = new PDDocument()) {
            try (InputStream fontStream = PdfReportUtil.class.getResourceAsStream("/fonts/DejaVuSans.ttf");
                 InputStream boldFontStream = PdfReportUtil.class.getResourceAsStream("/fonts/DejaVuSans-Bold.ttf")) {

                if (fontStream == null || boldFontStream == null) {
                    throw new IOException("Font resources not found under /fonts in classpath");
                }

                PDFont regular = PDType0Font.load(doc, fontStream, true);
                PDFont bold = PDType0Font.load(doc, boldFontStream, true);

                DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(Locale.forLanguageTag("bs-BA"));
                DecimalFormat moneyFmt = new DecimalFormat("#,##0.00", dfs);
                DecimalFormat qtyFmt = new DecimalFormat("0", dfs);

                PDRectangle pageSize = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
                float pageWidth = pageSize.getWidth();
                float pageHeight = pageSize.getHeight();
                float usableWidth = pageWidth - 2 * PAGE_MARGIN;

                float wOpening = 58f;
                float wReceived = 62f;
                float wSold = 52f;
                float wExpected = 70f;
                float wPhysical = 60f;
                float wTotal = 76f;
                float wItem;
                float wNote;

                float fixed = wOpening + wReceived + wSold + wExpected + wPhysical + wTotal;
                float remaining = usableWidth - fixed;
                wItem = Math.max(190f, remaining * 0.58f);
                wNote = Math.max(140f, remaining - wItem);

                if (wItem + wNote + fixed > usableWidth) {
                    float over = (wItem + wNote + fixed) - usableWidth;
                    wNote = Math.max(100f, wNote - over);
                }

                float[] colWidths = new float[]{wItem, wOpening, wReceived, wSold, wExpected, wPhysical, wTotal, wNote};
                String[] headers = new String[]{"Artikal", "Početni", "Primljeno", "Prodano", "Očekivano", "Fizičko", "Ukupno (KM)", "Napomena"};

                float tableTopY;
                float headerCardHeight;

                List<PDPage> pages = new ArrayList<>();

                PDPage page = new PDPage(pageSize);
                doc.addPage(page);
                pages.add(page);

                float cursorY;

                float footerHeight = 22f;

                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    drawPageBackground(cs, pageSize);

                    headerCardHeight = drawHeaderCard(
                            cs,
                            pageSize,
                            regular,
                            bold,
                            safeDate,
                            total,
                            moneyFmt
                    );

                    tableTopY = pageHeight - PAGE_MARGIN - headerCardHeight - 14f;

                    cursorY = drawTableHeaderRow(
                            cs,
                            regular,
                            bold,
                            headers,
                            colWidths,
                            PAGE_MARGIN,
                            tableTopY,
                            usableWidth
                    );

                    drawTableBody(
                            doc,
                            cs,
                            pageSize,
                            pages,
                            regular,
                            bold,
                            safeItems,
                            colWidths,
                            PAGE_MARGIN,
                            cursorY,
                            usableWidth,
                            footerHeight,
                            moneyFmt,
                            qtyFmt
                    );
                }

                int totalPages = pages.size();
                for (int i = 0; i < totalPages; i++) {
                    PDPage pg = pages.get(i);
                    try (PDPageContentStream footerStream = new PDPageContentStream(doc, pg, AppendMode.APPEND, true)) {
                        drawFooter(
                                footerStream,
                                pageSize,
                                regular,
                                safeDate,
                                i + 1,
                                totalPages
                        );
                    }
                }

                doc.save(output.toFile());
            }
        }
    }

    private static void drawPageBackground(PDPageContentStream cs, PDRectangle pageSize) throws IOException {
        cs.setNonStrokingColor(CAB_BG);
        cs.addRect(0, 0, pageSize.getWidth(), pageSize.getHeight());
        cs.fill();
    }

    private static float drawHeaderCard(
            PDPageContentStream cs,
            PDRectangle pageSize,
            PDFont regular,
            PDFont bold,
            LocalDate date,
            double total,
            DecimalFormat moneyFmt
    ) throws IOException {
        float cardX = PAGE_MARGIN;
        float cardYTop = pageSize.getHeight() - PAGE_MARGIN;

        float cardW = pageSize.getWidth() - 2 * PAGE_MARGIN;
        float cardH = 92f;
        float cardY = cardYTop - cardH;

        cs.setNonStrokingColor(CAB_SURFACE);
        cs.addRect(cardX, cardY, cardW, cardH);
        cs.fill();

        cs.setStrokingColor(CAB_BORDER);
        cs.setLineWidth(1f);
        cs.addRect(cardX, cardY, cardW, cardH);
        cs.stroke();

        cs.setStrokingColor(CAB_PRIMARY);
        cs.setLineWidth(2f);
        cs.moveTo(cardX, cardYTop);
        cs.lineTo(cardX + cardW, cardYTop);
        cs.stroke();

        float padding = 14f;
        float xLeft = cardX + padding;
        float xRight = cardX + cardW - padding;
        float y = cardYTop - padding - FONT_TITLE;

        cs.setNonStrokingColor(CAB_TEXT);
        drawText(cs, bold, FONT_TITLE, xLeft, y, "Caballo · Daily Sales");

        String dateStr = "Date: " + date;
        String genStr = "Generated: " + LocalDateTime.now().format(TS_FMT);

        float metaY = cardYTop - padding - FONT_SUBTITLE;
        cs.setNonStrokingColor(CAB_MUTED);
        drawRightAlignedText(cs, regular, FONT_SUBTITLE, xRight, metaY, dateStr);
        drawRightAlignedText(cs, regular, FONT_SUBTITLE, xRight, metaY - 14f, genStr);

        float totalY = cardY + 18f;
        cs.setNonStrokingColor(CAB_TEXT);
        drawText(cs, bold, 12.5f, xLeft, totalY, "Total revenue");
        drawText(cs, regular, 12.5f, xLeft + 105f, totalY, moneyFmt.format(total) + " BAM");

        cs.setNonStrokingColor(CAB_MUTED);
        drawText(cs, regular, FONT_SUBTITLE, xLeft, totalY + 18f, "Dnevni pregled artikala");

        return cardH;
    }

    private static float drawTableHeaderRow(
            PDPageContentStream cs,
            PDFont regular,
            PDFont bold,
            String[] headers,
            float[] colWidths,
            float startX,
            float startY,
            float tableWidth
    ) throws IOException {
        float rowH = 22f;

        cs.setNonStrokingColor(TABLE_HEADER_BG);
        cs.addRect(startX, startY - rowH, tableWidth, rowH);
        cs.fill();

        cs.setStrokingColor(CAB_BORDER);
        cs.setLineWidth(1f);
        cs.addRect(startX, startY - rowH, tableWidth, rowH);
        cs.stroke();

        cs.setNonStrokingColor(CAB_TEXT);
        float x = startX;
        float textY = startY - 15.5f;

        for (int i = 0; i < headers.length; i++) {
            float w = colWidths[i];

            float cellPadding = 6f;
            float textX = x + cellPadding;
            drawText(cs, bold, FONT_TABLE_HEADER, textX, textY, headers[i]);

            x += w;
            if (i < headers.length - 1) {
                cs.setStrokingColor(CAB_BORDER);
                cs.setLineWidth(0.8f);
                cs.moveTo(x, startY);
                cs.lineTo(x, startY - rowH);
                cs.stroke();
            }
        }

        return startY - rowH;
    }

    private static void drawTableBody(
            PDDocument doc,
            PDPageContentStream firstPageStream,
            PDRectangle pageSize,
            List<PDPage> pages,
            PDFont regular,
            PDFont bold,
            List<DailyItemReport> items,
            float[] colWidths,
            float startX,
            float startY,
            float tableWidth,
            float footerHeight,
            DecimalFormat moneyFmt,
            DecimalFormat qtyFmt
    ) throws IOException {
        float cursorY = startY;
        float bottomLimit = PAGE_MARGIN + footerHeight;

        if (items.isEmpty()) {
            float rowH = 28f;
            if (cursorY - rowH < bottomLimit) {
                PDPage next = new PDPage(pageSize);
                doc.addPage(next);
                pages.add(next);
                try (PDPageContentStream cs = new PDPageContentStream(doc, next)) {
                    drawPageBackground(cs, pageSize);
                    float yTop = pageSize.getHeight() - PAGE_MARGIN;
                    float yAfterHeader = yTop - 14f;
                    cursorY = drawTableHeaderRow(cs, regular, bold,
                            new String[]{"Artikal", "Početni", "Primljeno", "Prodano", "Očekivano", "Fizičko", "Ukupno (KM)", "Napomena"},
                            colWidths, startX, yAfterHeader, tableWidth);
                    drawEmptyRow(cs, regular, startX, cursorY, tableWidth);
                    return;
                }
            }

            drawEmptyRow(firstPageStream, regular, startX, cursorY, tableWidth);
            return;
        }

        PDPageContentStream cs = firstPageStream;

        int rowIndex = 0;

        for (DailyItemReport r : items) {
            String itemName = safeText(r.getItemName());
            String note = safeText(r.getNote());

            float cellPad = 6f;
            float maxItemWidth = colWidths[0] - 2 * cellPad;
            float maxNoteWidth = colWidths[7] - 2 * cellPad;

            List<String> itemLines = wrapText(itemName, regular, FONT_TABLE_BODY, maxItemWidth);
            List<String> noteLines = wrapText(note, regular, FONT_TABLE_BODY, maxNoteWidth);

            float lineH = FONT_TABLE_BODY * LINE_GAP;
            int maxLines = Math.max(1, Math.max(itemLines.size(), noteLines.size()));
            float rowH = Math.max(20f, maxLines * lineH + 10f);

            if (cursorY - rowH < bottomLimit) {
                if (cs != firstPageStream) {
                    cs.close();
                }

                PDPage currentPage = new PDPage(pageSize);
                doc.addPage(currentPage);
                pages.add(currentPage);

                cs = new PDPageContentStream(doc, currentPage);
                drawPageBackground(cs, pageSize);

                float yTop = pageSize.getHeight() - PAGE_MARGIN;
                float tableHeaderY = yTop - 14f;
                cursorY = drawTableHeaderRow(
                        cs,
                        regular,
                        bold,
                        new String[]{"Artikal", "Početni", "Primljeno", "Prodano", "Očekivano", "Fizičko", "Ukupno (KM)", "Napomena"},
                        colWidths,
                        startX,
                        tableHeaderY,
                        tableWidth
                );
                rowIndex = 0;
            }

            if (rowIndex % 2 == 1) {
                cs.setNonStrokingColor(ZEBRA_BG);
                cs.addRect(startX, cursorY - rowH, tableWidth, rowH);
                cs.fill();
            }

            cs.setStrokingColor(CAB_BORDER);
            cs.setLineWidth(0.8f);
            cs.moveTo(startX, cursorY);
            cs.lineTo(startX + tableWidth, cursorY);
            cs.stroke();

            float x = startX;
            for (int ci = 0; ci < colWidths.length - 1; ci++) {
                x += colWidths[ci];
                cs.moveTo(x, cursorY);
                cs.lineTo(x, cursorY - rowH);
            }
            cs.stroke();

            cs.setNonStrokingColor(CAB_TEXT);

            float textStartY = cursorY - 8f - FONT_TABLE_BODY;

            drawWrappedCellText(cs, regular, FONT_TABLE_BODY, startX + cellPad, textStartY, itemLines, lineH);

            float noteX = startX;
            for (int i = 0; i < 7; i++) noteX += colWidths[i];
            drawWrappedCellText(cs, regular, FONT_TABLE_BODY, noteX + cellPad, textStartY, noteLines, lineH);

            float colX = startX + colWidths[0];
            drawRightAlignedTextInCell(cs, regular, FONT_TABLE_BODY, colX, cursorY, colWidths[1], cellPad, qtyFmt.format(r.getOpeningQty()));
            colX += colWidths[1];
            drawRightAlignedTextInCell(cs, regular, FONT_TABLE_BODY, colX, cursorY, colWidths[2], cellPad, qtyFmt.format(r.getReceivedQty()));
            colX += colWidths[2];
            drawRightAlignedTextInCell(cs, regular, FONT_TABLE_BODY, colX, cursorY, colWidths[3], cellPad, qtyFmt.format(r.getSoldQty()));
            colX += colWidths[3];
            drawRightAlignedTextInCell(cs, regular, FONT_TABLE_BODY, colX, cursorY, colWidths[4], cellPad, qtyFmt.format(r.getExpectedClosingQty()));
            colX += colWidths[4];
            drawRightAlignedTextInCell(cs, regular, FONT_TABLE_BODY, colX, cursorY, colWidths[5], cellPad, qtyFmt.format(r.getPhysicalClosingQty()));
            colX += colWidths[5];
            drawRightAlignedTextInCell(cs, regular, FONT_TABLE_BODY, colX, cursorY, colWidths[6], cellPad, moneyFmt.format(r.getSoldTotalAmount()));

            cursorY -= rowH;
            rowIndex++;
        }

        cs.setStrokingColor(CAB_BORDER);
        cs.setLineWidth(1f);
        cs.moveTo(startX, cursorY);
        cs.lineTo(startX + tableWidth, cursorY);
        cs.stroke();

        if (cs != firstPageStream) {
            cs.close();
        }
    }

    private static float drawEmptyRow(PDPageContentStream cs, PDFont regular, float startX, float cursorY, float tableWidth) throws IOException {
        float rowH = 34f;

        cs.setNonStrokingColor(CAB_SURFACE);
        cs.addRect(startX, cursorY - rowH, tableWidth, rowH);
        cs.fill();

        cs.setStrokingColor(CAB_BORDER);
        cs.setLineWidth(1f);
        cs.addRect(startX, cursorY - rowH, tableWidth, rowH);
        cs.stroke();

        cs.setNonStrokingColor(CAB_MUTED);
        drawText(cs, regular, 10.5f, startX + 10f, cursorY - 21f, "Nema podataka za odabrani datum.");

        return cursorY - rowH;
    }

    private static void drawFooter(
            PDPageContentStream cs,
            PDRectangle pageSize,
            PDFont regular,
            LocalDate date,
            int page,
            int totalPages
    ) throws IOException {
        float y = PAGE_MARGIN - 12f;
        float rightX = pageSize.getWidth() - PAGE_MARGIN;

        cs.setNonStrokingColor(CAB_MUTED);
        drawText(cs, regular, FONT_FOOTER, PAGE_MARGIN, y, "Caballo · Daily Sales · " + date);
        drawRightAlignedText(cs, regular, FONT_FOOTER, rightX, y, "Page " + page + " / " + totalPages);

        cs.setStrokingColor(CAB_BORDER);
        cs.setLineWidth(0.8f);
        cs.moveTo(PAGE_MARGIN, PAGE_MARGIN);
        cs.lineTo(pageSize.getWidth() - PAGE_MARGIN, PAGE_MARGIN);
        cs.stroke();
    }

    private static String safeText(String s) {
        if (s == null) return "";
        return s.trim();
    }

    private static void drawText(PDPageContentStream cs, PDFont font, float size, float x, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text != null ? text : "");
        cs.endText();
    }

    private static void drawRightAlignedText(PDPageContentStream cs, PDFont font, float size, float rightX, float y, String text) throws IOException {
        String t = text != null ? text : "";
        float w = textWidth(font, size, t);
        drawText(cs, font, size, rightX - w, y, t);
    }

    private static void drawRightAlignedTextInCell(
            PDPageContentStream cs,
            PDFont font,
            float size,
            float cellX,
            float cursorY,
            float cellW,
            float padding,
            String text
    ) throws IOException {
        String t = text != null ? text : "";
        float baselineY = cursorY - 8f - size;
        float textW = textWidth(font, size, t);
        float x = cellX + cellW - padding - textW;
        drawText(cs, font, size, x, baselineY, t);
    }

    private static void drawWrappedCellText(
            PDPageContentStream cs,
            PDFont font,
            float size,
            float x,
            float firstLineBaselineY,
            List<String> lines,
            float lineHeight
    ) throws IOException {
        if (lines == null || lines.isEmpty()) {
            return;
        }
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            drawText(cs, font, size, x, firstLineBaselineY - i * lineHeight, line);
        }
    }

    private static float textWidth(PDFont font, float size, String text) throws IOException {
        if (text == null || text.isEmpty()) return 0f;
        return (font.getStringWidth(text) / 1000f) * size;
    }

    private static List<String> wrapText(String text, PDFont font, float size, float maxWidth) throws IOException {
        String t = text != null ? text.trim() : "";
        if (t.isEmpty()) return List.of("");
        if (maxWidth <= 5f) return List.of(t);

        String[] words = t.split("\\s+");
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String w : words) {
            if (current.isEmpty()) {
                current.append(w);
                continue;
            }

            String candidate = current + " " + w;
            if (textWidth(font, size, candidate) <= maxWidth) {
                current.append(' ').append(w);
            } else {
                lines.add(current.toString());
                current.setLength(0);

                if (textWidth(font, size, w) > maxWidth) {
                    lines.addAll(hardBreakWord(w, font, size, maxWidth));
                } else {
                    current.append(w);
                }
            }
        }

        if (!current.isEmpty()) {
            lines.add(current.toString());
        }

        return lines;
    }

    private static List<String> hardBreakWord(String word, PDFont font, float size, float maxWidth) throws IOException {
        List<String> out = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < word.length(); i++) {
            current.append(word.charAt(i));
            if (textWidth(font, size, current.toString()) > maxWidth) {
                char last = current.charAt(current.length() - 1);
                current.setLength(current.length() - 1);
                if (!current.isEmpty()) {
                    out.add(current.toString());
                }
                current.setLength(0);
                current.append(last);
            }
        }

        if (!current.isEmpty()) out.add(current.toString());
        return out;
    }
}
