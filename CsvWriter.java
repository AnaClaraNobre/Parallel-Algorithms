
import java.io.*;
import java.util.List;

public class CsvWriter {

    public static void writeResults(String filePath, List<Result> results) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Algorithm,Occurrences,ExecutionTime(ms)");
            for (Result result : results) {
                writer.printf("%s,%d,%d%n",
                        result.getAlgorithm(),
                        result.getOccurrences(),
                        result.getExecutionTime());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
