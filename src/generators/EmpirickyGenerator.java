package generators;

import java.util.Random;

//** LLM usage, to specify
public class EmpirickyGenerator {
    private Random random;
    private double[] minBounds;
    private double[] maxBounds;
    private double[] cumulativeProbs;

    public EmpirickyGenerator(int seed) {
        this.random = new Random(seed);
        
        this.minBounds = new double[]{
            56.00, 86.96, 117.92, 148.88, 179.84, 210.80, 241.76, 272.72, 303.68, 334.64,
            365.60, 396.56, 427.52, 458.48, 489.44, 520.40, 551.36, 582.32, 613.28, 644.24,
            675.20, 706.16, 737.12, 768.08, 799.04
        };
        
        this.maxBounds = new double[]{
            86.96, 117.92, 148.88, 179.84, 210.80, 241.76, 272.72, 303.68, 334.64, 365.60,
            396.56, 427.52, 458.48, 489.44, 520.40, 551.36, 582.32, 613.28, 644.24, 675.20,
            706.16, 737.12, 768.08, 799.04, 830.00
        };
        
        double[] rawProbs = {
            0.0014, 0.0095, 0.0142, 0.0474, 0.0562, 0.0847, 0.0949, 0.0928, 0.0874, 0.0955,
            0.0874, 0.0745, 0.0596, 0.0434, 0.0400, 0.0339, 0.0230, 0.0129, 0.0136, 0.0095,
            0.0054, 0.0047, 0.0054, 0.0020, 0.0007
        };
        
        this.cumulativeProbs = new double[rawProbs.length];
        
        double sum = 0.0;
        for (int i = 0; i < rawProbs.length; i++) {
            sum += rawProbs[i];
            this.cumulativeProbs[i] = sum;
        }
    }

    //** LLM usage, to specify
    public double sample() {
        double roll = random.nextDouble();
        
        int selectedIndex = 0;
        
        for (int i = 0; i < cumulativeProbs.length; i++) {
            if (roll <= cumulativeProbs[i]) {
                selectedIndex = i;
                break;
            }
        }
        
        double min = minBounds[selectedIndex];
        double max = maxBounds[selectedIndex];
        
        return min + (random.nextDouble() * (max - min));
    }
}