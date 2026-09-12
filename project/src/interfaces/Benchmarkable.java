package interfaces;

public interface Benchmarkable {
    String getBenchmarkName();
    String getCourseOutcome();
    long runBenchmark(int iterations); // returns elapsed time in microseconds
    String getTheoreticalComplexity();
}
