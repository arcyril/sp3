package generators;
import java.util.Random;

public class SpojityEmpirickyGenerator {
    private Random random;
    private double[] cumulativeProbs;
    private double[] minValues;
    private double[] maxValues;

    public SpojityEmpirickyGenerator(double[] probabilities, double[] minValues, double[] maxValues, int seed) {
        this.random = new Random(seed);
        this.minValues = minValues;
        this.maxValues = maxValues;
        
        this.cumulativeProbs = new double[probabilities.length];
        double sum = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            this.cumulativeProbs[i] = sum;
        }
    }

    public double sample() {
        double val = random.nextDouble();
        
        for (int i = 0; i < cumulativeProbs.length; i++) {
            if (val < cumulativeProbs[i]) {
                double min = minValues[i];
                double max = maxValues[i];
                return min + (random.nextDouble() * (max - min));
            }
        }
        
        double minFallback = minValues[minValues.length - 1];
        double maxFallback = maxValues[maxValues.length - 1];
        return minFallback + (random.nextDouble() * (maxFallback - minFallback));
    }
}