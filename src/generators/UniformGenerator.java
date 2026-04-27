package generators;

import java.util.Random;

public class UniformGenerator {
    private Random random;
    private double min;
    private double max;

    public UniformGenerator(double min, double max, int seed) {
        this.random = new Random(seed);
        this.min = min;
        this.max = max;
    }

    public double sample() {
        return min + (max - min) * random.nextDouble();
    }
}