package simulator.graphics;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import simulator.main.Window;

public class ColorChart extends JFrame {
    private DefaultCategoryDataset dataset;
    private String filePath;
    private JFreeChart chart;

    public ColorChart(String title, String fileName, int width, int height) {
        String projectDir = System.getProperty("user.dir");
        String resDir = projectDir + File.separator + "res";
        filePath = resDir + File.separator + fileName;

        dataset = createDataset(filePath);

        chart = ChartFactory.createLineChart(
                "Evolução da Quantidade de Cores ao Longo dos Dias",
                "Dias",
                "Quantidade Total de Cores",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);

        int totalWidth = Window.frame.getWidth();
        int totalHeight = Window.frame.getHeight();
        int xPosition = (int) (totalWidth * (width / 100.0)); 
        int yPosition = (int) (totalHeight * (height / 100.0));
        chartPanel.setPreferredSize(new Dimension(800, 600));

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        for (int i = 0; i < dataset.getRowCount(); i++) {
            renderer.setSeriesStroke(i, new BasicStroke(1.0f));
        }

        setContentPane(chartPanel);
    }

    public void updateChart() {
        dataset.clear();
        DefaultCategoryDataset newDataset = createDataset(filePath);

        for (int row = 0; row < newDataset.getRowCount(); row++) {
            for (int column = 0; column < newDataset.getColumnCount(); column++) {
                dataset.addValue(newDataset.getValue(row, column), newDataset.getRowKey(row), newDataset.getColumnKey(column));
            }
        }

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDataset(dataset);
    }

    private DefaultCategoryDataset createDataset(String csvFilePath) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, ColorData> colorSeries = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(csvFilePath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                int day;
                try {
                    day = Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    System.err.println("Dia inválido (não é um número): " + parts[0]);
                    continue;
                }

                String colors = parts[4];

                String[] colorEntries = colors.split("\\|");
                for (String colorEntry : colorEntries) {
                    String[] colorParts = colorEntry.split("-");

                    int count, r, g, b;
                    try {
                        count = Integer.parseInt(colorParts[0]);
                        r = Integer.parseInt(colorParts[1]);
                        g = Integer.parseInt(colorParts[2]);
                        b = Integer.parseInt(colorParts[3]);
                    } catch (NumberFormatException e) {
                        System.err.println("Valor inválido na cor (não é um número): " + colorEntry);
                        continue;
                    }

                    String colorKey = String.format("RGB(%d,%d,%d)", r, g, b);
                    colorSeries.computeIfAbsent(colorKey, k -> new ColorData(r, g, b)).addData(day, count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Ordenar os dias
        Set<Integer> sortedDays = new TreeSet<>();
        for (Map.Entry<String, ColorData> entry : colorSeries.entrySet()) {
            ColorData colorData = entry.getValue();
            sortedDays.addAll(colorData.days);
        }

        // Adiciona os dados ao dataset na ordem correta
        for (Integer day : sortedDays) {
            for (Map.Entry<String, ColorData> entry : colorSeries.entrySet()) {
                ColorData colorData = entry.getValue();
                int index = colorData.days.indexOf(day);
                if (index != -1) {
                    dataset.addValue(colorData.quantities.get(index), entry.getKey(), day);
                }
            }
        }

        return dataset;
    }

}

class ColorData {
    int r, g, b;
    java.util.List<Integer> days = new java.util.ArrayList<>();
    java.util.List<Integer> quantities = new java.util.ArrayList<>();

    public ColorData(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void addData(int day, int quantity) {
        days.add(day);
        quantities.add(quantity);
    }
}
