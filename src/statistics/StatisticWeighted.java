package statistics;

public class StatisticWeighted {
    private double areaSum;
    private double lastChangeTime;
    private double lastValue;

    public StatisticWeighted() {
        this.areaSum = 0.0;
        this.lastChangeTime = 0.0;
        this.lastValue = 0.0;
    }

    public void update(double currentTime, double newValue) {
        areaSum += lastValue * (currentTime - lastChangeTime);
        lastValue = newValue;
        lastChangeTime = currentTime;
    }

    public double getAverage(double totalTime) {
        if (totalTime == 0) return 0;
        return areaSum / totalTime;
    }

    public void clear() {
        this.areaSum = 0.0;
        this.lastChangeTime = 0.0;
        this.lastValue = 0.0;
    }
    
    public void warmUp(double currentTime, double currentValue) {
        this.areaSum = 0.0;
        this.lastChangeTime = currentTime;
        this.lastValue = currentValue;
    }
}