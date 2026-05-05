package statistics;

public class Statistic {
    private double sum;
    private double sumSquared;
    private int count;
    private double max;

    public Statistic() {
        this.sum = 0.0;
        this.sumSquared = 0.0;
        this.count = 0;
        this.max = 0.0;
    }

    public void addValue(double value) {
        this.sum += value;
        this.sumSquared += (value * value);
        this.count++;
        if (value > this.max) {
            this.max = value;
        }
    }

    public double getAverage() {
        if (count == 0) return 0;
        return sum / count;
    }

    public double getVariance() {
        if (count <= 1) return 0;
        return (sumSquared - (sum * sum) / count) / (count - 1);
    }

    public double getMax() { 
        return max; 
    }


    public void clear() {
        this.sum = 0.0;
        this.sumSquared = 0.0;
        this.count = 0;
        this.max = 0.0;
    }
}