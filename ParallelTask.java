
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class ParallelTask extends RecursiveTask<Integer> {
    private static final int THRESHOLD = 100; 
    private final List<String> lines;
    private final String word;
    private final int start;
    private final int end;

    public ParallelTask(List<String> lines, String word, int start, int end) {
        this.lines = lines;
        this.word = word;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if ((end - start) <= THRESHOLD) {
            int occurrences = 0;
            for (int i = start; i < end; i++) {
                occurrences += countWordInLine(lines.get(i), word);
            }
            return occurrences;
        } else {
            // Divide a tarefa em duas
            int mid = (start + end) / 2;
            ParallelTask leftTask = new ParallelTask(lines, word, start, mid);
            ParallelTask rightTask = new ParallelTask(lines, word, mid, end);

            // Executa as tarefas em paralelo
            leftTask.fork(); 
            int rightResult = rightTask.compute(); 
            int leftResult = leftTask.join(); 

            return leftResult + rightResult;
        }
    }

    private int countWordInLine(String line, String word) {
        return (int) Arrays.stream(line.split("\\W+"))
                .filter(w -> w.equalsIgnoreCase(word))
                .count();
    }
}
