import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class ChartDisplay {

    public static void showChart(String csvFile) {
        SwingUtilities.invokeLater(() -> {
            try {
                Map<String, Integer> data = readResultsFromCSV(csvFile);
                JFrame frame = new JFrame("Desempenho dos Algoritmos");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);

                JPanel panel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);

                        int width = getWidth();
                        int height = getHeight();
                        int padding = 50;
                        int barWidth = (width - 2 * padding) / data.size();
                        int maxValue = Collections.max(data.values());

                        int x = padding;
                        for (Map.Entry<String, Integer> entry : data.entrySet()) {
                            int barHeight = (int) ((double) entry.getValue() / maxValue * (height - 2 * padding));
                            g.setColor(Color.BLUE);
                            g.fillRect(x, height - padding - barHeight, barWidth - 10, barHeight);

                            g.setColor(Color.BLACK);
                            g.drawString(entry.getKey(), x + 5, height - padding + 20);
                            g.drawString(entry.getValue() + " ms", x + 5, height - padding - barHeight - 10);

                            x += barWidth;
                        }

                        g.setColor(Color.BLACK);
                        g.drawLine(padding, height - padding, width - padding, height - padding); 
                        g.drawLine(padding, height - padding, padding, padding); 
                    }
                };

                frame.add(panel);
                frame.setVisible(true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static Map<String, Integer> readResultsFromCSV(String csvFile) throws IOException {
        Map<String, Integer> data = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;

            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String label = values[0]; 
                int time = Integer.parseInt(values[2].trim()); 
                data.put(label, time);
            }
        }
        return data;
    }
}
