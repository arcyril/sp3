package generators;

import java.util.Random;

/// Na Vytvorenie Kódu Bol Použitý LLM. Kapitola 1.A /// 
public class TrojuholnikovyGenerator {
    private Random random;
    private double min;
    private double mode;
    private double max;

    public TrojuholnikovyGenerator(double min, double mode, double max, int seed) {
        this.random = new Random(seed);
        this.min = min;
        this.mode = mode;
        this.max = max;
    }

    public double sample() {
        double u = random.nextDouble();
        double f = (mode - min) / (max - min);
        if (u < f) {
            return min + Math.sqrt(u * (max - min) * (mode - min));
        } else {
            return max - Math.sqrt((1 - u) * (max - min) * (max - mode));
        }
    }
}