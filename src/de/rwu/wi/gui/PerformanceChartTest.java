package de.rwu.wi.gui;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PerformanceChartTest {
    @Test
    void chartsShowOnlyCompletedResultsInAlgorithmOrderAndClearBetweenRuns() {
        PerformanceChart runtime = PerformanceChart.runtime();
        PerformanceChart comparisons = PerformanceChart.comparisons();
        PerformanceRunner.Result first = new PerformanceRunner.Result(0, 0);
        PerformanceRunner.Result last = new PerformanceRunner.Result(1_234_000_000L, 3_000_000);
        for (PerformanceChart chart : List.of(runtime, comparisons)) {
            chart.setEntry(5, "Last", last);
            chart.setEntry(1, "First", first);
            chart.setEntry(3, "Aborted", null);
            assertEquals(List.of("First", "Last"), chart.entries().stream()
                    .map(PerformanceChart.Entry::label).toList());
            assertEquals(List.of(first, last), chart.entries().stream()
                    .map(PerformanceChart.Entry::result).toList());

            chart.setSize(500, 300);
            BufferedImage image = new BufferedImage(500, 300, BufferedImage.TYPE_INT_ARGB);
            chart.paint(image.getGraphics());
            chart.setEntry(1, "First", null);
            assertEquals(List.of("Last"), chart.entries().stream()
                    .map(PerformanceChart.Entry::label).toList());
            chart.clear();
            assertTrue(chart.entries().isEmpty());
            chart.paint(image.getGraphics());
        }
    }

    @Test
    void zeroValuesArePaintedWithoutDivisionErrors() {
        PerformanceChart chart = PerformanceChart.runtime();
        chart.setEntry(0, "Empty input", new PerformanceRunner.Result(0, 0));
        chart.setSize(400, 150);
        BufferedImage image = new BufferedImage(400, 150, BufferedImage.TYPE_INT_ARGB);
        chart.paint(image.getGraphics());
        assertEquals(1, chart.entries().size());
    }
}
