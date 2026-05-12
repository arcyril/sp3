package generators;
import java.util.Random;

//** Na Vytvorenie Kódu Bol Použitý LLM */
public class NormalnyGenerator {
    private Random random;
    private double mu;
    private double sigma;

    public NormalnyGenerator(double mu, double sigma, int seed) {
        this.random = new Random(seed);
        this.mu = mu;
        this.sigma = sigma;
    }

    public double sample() {
        double normalValue = mu + sigma * random.nextGaussian();
        return Math.max(0.0, normalValue); 
    }
}