package generators;
import java.util.Random;

public class EmpirickyGenerator {
    //** LLM */
    private Random random;
    private double[] cumulativeProbs;
    private double[] values;

    public EmpirickyGenerator(double[] probabilities, double[] values, int seed) {
        this.random = new Random(seed);
        this.values = values;
        
        this.cumulativeProbs = new double[probabilities.length];
        double suma = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            suma += probabilities[i];
            this.cumulativeProbs[i] = suma;
        }
    }

    public double sample() {
        double val = random.nextDouble();
        
        for (int i = 0; i < cumulativeProbs.length; i++) {
            if (val < cumulativeProbs[i]) {
                return values[i];
            }
        }
        return values[values.length - 1]; 
    }
}