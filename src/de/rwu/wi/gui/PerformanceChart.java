package de.rwu.wi.gui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.ToDoubleFunction;
import java.util.function.Function;

final class PerformanceChart extends JPanel {
    private static final int ROW_HEIGHT = 28;
    private final String title;
    private final Color barColor;
    private final ToDoubleFunction<PerformanceRunner.Result> value;
    private final Function<PerformanceRunner.Result, String> format;
    private final List<Entry> entries = new ArrayList<>();

    record Entry(String label, PerformanceRunner.Result result) {}

    PerformanceChart(String title, Color barColor,
                     ToDoubleFunction<PerformanceRunner.Result> value,
                     Function<PerformanceRunner.Result, String> format) {
        this.title = title;
        this.barColor = barColor;
        this.value = value;
        this.format = format;
        setBackground(Color.WHITE);
        updatePreferredSize();
    }

    static PerformanceChart runtime() {
        return new PerformanceChart("Runtime (ms)", new Color(50, 112, 184),
                result -> result.elapsedNanos() / 1_000_000.0,
                result -> String.format(Locale.US, "%,.3f", result.elapsedNanos() / 1_000_000.0));
    }

    static PerformanceChart comparisons() {
        return new PerformanceChart("Comparisons", new Color(45, 145, 105),
                PerformanceRunner.Result::comparisons,
                result -> String.format(Locale.US, "%,d", result.comparisons()));
    }

    void setEntry(int row, String label, PerformanceRunner.Result result) {
        while (entries.size() <= row) {
            entries.add(null);
        }
        entries.set(row, result == null ? null : new Entry(label, result));
        updatePreferredSize();
        revalidate();
        repaint();
    }

    void clear() {
        entries.clear();
        updatePreferredSize();
        revalidate();
        repaint();
    }

    List<Entry> entries() {
        return entries.stream().filter(entry -> entry != null).toList();
    }

    private void updatePreferredSize() {
        setPreferredSize(new Dimension(420, Math.max(130, 45 + entries().size() * ROW_HEIGHT)));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(getForeground());
            g.drawString(title, 10, 20);
            List<Entry> shown = entries();
            if (shown.isEmpty()) {
                g.drawString("No completed results yet.", 10, 48);
                return;
            }
            FontMetrics metrics = g.getFontMetrics();
            int labelWidth = 0;
            int valueWidth = 0;
            double max = 0;
            for (Entry entry : shown) {
                labelWidth = Math.max(labelWidth, metrics.stringWidth(entry.label()));
                valueWidth = Math.max(valueWidth, metrics.stringWidth(format.apply(entry.result())));
                max = Math.max(max, value.applyAsDouble(entry.result()));
            }
            int start = 10 + labelWidth + 10;
            int barWidth = Math.max(0, getWidth() - start - valueWidth - 25);
            int y = 40;
            for (Entry entry : shown) {
                double current = value.applyAsDouble(entry.result());
                int width = max > 0 ? (int) Math.round(barWidth * (current / max)) : 0;
                g.setColor(getForeground());
                g.drawString(entry.label(), 10, y + 13);
                g.setColor(barColor);
                g.fillRect(start, y, width, 16);
                g.setColor(getForeground());
                g.drawString(format.apply(entry.result()), start + width + 5, y + 13);
                y += ROW_HEIGHT;
            }
        } finally {
            g.dispose();
        }
    }
}
