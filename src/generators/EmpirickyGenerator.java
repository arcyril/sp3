package generators;

import java.util.Random;

public class EmpirickyGenerator {
    private Random random;

    public EmpirickyGenerator(int seed) {
        this.random = new Random(seed);
    }

    public int sample() {
        double val = random.nextDouble();
        if (val < 0.15) return 0;
        if (val < 0.83) return 1;
        return 2;
    }
}