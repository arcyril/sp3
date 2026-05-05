package generators;
import java.util.Random;

public class RovnomernyDisktretnyGenerator {
    private Random random;
    private int min;
    private int max;

    public RovnomernyDisktretnyGenerator(int min, int max, int seed) {
        this.random = new Random(seed);
        this.min = min;
        this.max = max;
    }

    public int sample() {
        return random.nextInt((max - min) + 1) + min;
    }
}