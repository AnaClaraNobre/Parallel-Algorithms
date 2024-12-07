import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filePath = "Dracula-165307.txt"; 
        String wordToSearch = "The"; 

        TextSearch textSearch = new TextSearch();

        // Serial
        Result serialResult = textSearch.searchSerial(filePath, wordToSearch);
        System.out.println(serialResult);

        // Paralela CPU
        Result parallelCpuResult = textSearch.searchParallelCpu(filePath, wordToSearch);
        System.out.println(parallelCpuResult);

        // Paralela GPU
        Result parallelGpuResult = textSearch.searchParallelGpu(filePath, wordToSearch);
        System.out.println(parallelGpuResult);

        //resultados CSV
        CsvWriter.writeResults("results.csv", Arrays.asList(serialResult, parallelCpuResult, parallelGpuResult));

        ChartDisplay.showChart("results.csv");

    }
}
