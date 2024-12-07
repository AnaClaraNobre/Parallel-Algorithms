
public class Result {
    private final String algorithm;
    private final int occurrences;
    private final long executionTime;

    public Result(String algorithm, int occurrences, long executionTime) {
        this.algorithm = algorithm;
        this.occurrences = occurrences;
        this.executionTime = executionTime;
    }

    @Override
    public String toString() {
        return String.format("%s: %d occurrences in %d ms", algorithm, occurrences, executionTime);
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
