package generators;

import java.util.Random;

public class ExponencialnyGenerator {
    private Random random;
    private double lambda;

    public ExponencialnyGenerator(double lambda, int seed) {
        this.random = new Random(seed);
        this.lambda = lambda;
    }

    public double sample() {
        return Math.log(1 - random.nextDouble()) / (-lambda);
    }
}