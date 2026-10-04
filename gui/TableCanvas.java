package gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Jednostavna tabela crtana rucno na Canvas-u.
 * Cist AWT nema JTable, pa se zaglavlje i redovi iscrtavaju u paint().
 */
public class TableCanvas extends Canvas {

    private static final long serialVersionUID = 1L;

    private static final int ROW_HEIGHT = 22;
    private static final int PADDING = 6;

    private String[] headers;
    private int[] columnWidths;
    private List<String[]> rows = new ArrayList<String[]>();

    public TableCanvas(String[] headers, int[] columnWidths) {
        this.headers = headers;
        this.columnWidths = columnWidths;
        setBackground(Color.WHITE);
    }

    /** Zamenjuje sve redove i osvezava prikaz. */
    public void setRows(List<String[]> rows) {
        this.rows = rows;
        setSize(getPreferredSize());
        invalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        int width = 0;
        for (int w : columnWidths) width += w;
        int height = (rows.size() + 1) * ROW_HEIGHT + 2;
        return new Dimension(width, Math.max(height, 120));
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public void paint(Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int totalWidth = 0;
        for (int w : columnWidths) totalWidth += w;

        // zaglavlje
        g.setColor(new Color(220, 220, 220));
        g.fillRect(0, 0, totalWidth, ROW_HEIGHT);
        g.setColor(Color.BLACK);
        g.setFont(getFont().deriveFont(Font.BOLD));
        drawRow(g, headers, 0, fm);

        // redovi
        g.setFont(getFont().deriveFont(Font.PLAIN));
        for (int i = 0; i < rows.size(); i++) {
            drawRow(g, rows.get(i), i + 1, fm);
        }

        // linije mreze
        g.setColor(Color.GRAY);
        int totalHeight = (rows.size() + 1) * ROW_HEIGHT;
        for (int i = 0; i <= rows.size() + 1; i++) {
            g.drawLine(0, i * ROW_HEIGHT, totalWidth, i * ROW_HEIGHT);
        }
        int x = 0;
        for (int i = 0; i < columnWidths.length; i++) {
            g.drawLine(x, 0, x, totalHeight);
            x += columnWidths[i];
        }
        g.drawLine(totalWidth, 0, totalWidth, totalHeight);
    }

    private void drawRow(Graphics g, String[] cells, int rowIndex, FontMetrics fm) {
        int x = PADDING;
        int y = rowIndex * ROW_HEIGHT + ROW_HEIGHT - PADDING;
        for (int i = 0; i < cells.length && i < columnWidths.length; i++) {
            g.drawString(clip(cells[i], columnWidths[i] - 2 * PADDING, fm), x, y);
            x += columnWidths[i];
        }
    }

    /** Skracuje tekst ako ne staje u kolonu. */
    private String clip(String text, int maxWidth, FontMetrics fm) {
        if (text == null) return "";
        if (fm.stringWidth(text) <= maxWidth) return text;
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (fm.stringWidth(sb.toString() + c + "...") > maxWidth) break;
            sb.append(c);
        }
        return sb.toString() + "...";
    }
}
