package statistics;

/// Na Vytvorenie Kódu Bol Použitý LLM /// 
public class GlobalStatistic {
    private double sum = 0.0;
    private double sumSquared = 0.0;
    private double absoluteMax = 0.0;
    private int count = 0;

    public void addReplicationData(double replicationAvg, double replicationMax) {
        sum += replicationAvg;
        sumSquared += (replicationAvg * replicationAvg);
        count++;
        if (replicationMax > absoluteMax) {
            absoluteMax = replicationMax;
        }
    }

    public double getGlobalAverage() {
        return count == 0 ? 0 : sum / count;
    }

    public double getStandardDeviation() {
        if (count <= 1) return 0;
        double variance = (sumSquared - (sum * sum) / count) / (count - 1);
        return Math.sqrt(Math.max(0, variance));
    }

    public double getConfidenceIntervalHalfWidth() {
        if (count <= 1) return 0;
        return 1.96 * (getStandardDeviation() / Math.sqrt(count));
    }

    public double getAbsoluteMax() { 
        return absoluteMax; 
    }
    
    public void clear() {
        sum = 0.0; sumSquared = 0.0; count = 0;
        absoluteMax = 0.0;
    }
}