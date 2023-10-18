package other_metrics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.util.Map;
import java.util.HashMap;

public class EntropyCalculator {

    public static void main(String[] args) throws IOException {
        
        BufferedImage originalImage1 = ImageIO.read(new File("../Original/1254659.png"));
        BufferedImage compressedImage1 = ImageIO.read(new File("../Decompressed/10188041.png"));

        double entropyLoss1 = calculateEntropyLoss(originalImage1, compressedImage1);
        System.out.println("Entropy Loss 1 (delta H): " + entropyLoss1);


        BufferedImage originalImage2 = ImageIO.read(new File("../Original/1254659.png"));
        double entropyLoss2 = calculateEntropyLoss(originalImage2, originalImage2);
        System.out.println("Entropy Loss 2 (delta H): " + entropyLoss2);
    }

    public static double calculateEntropyLoss(BufferedImage original, BufferedImage decompressed) {
        return Math.abs(calculateEntropy(original) - calculateEntropy(decompressed));
    }

    private static double calculateEntropy(BufferedImage img){
        Map<Integer, Integer> freq = new HashMap<>();
        for (int y = 0; y < img.getHeight(); ++y){
            for (int x = 0; x < img.getWidth(); ++x){
                int pixel = img.getRGB(x, y);
                freq.putIfAbsent(pixel, 0);
                freq.put(pixel, freq.get(pixel) + 1); 
            }
        }
        double entropy = 0.0;
        double numPixels = img.getHeight() * img.getWidth() * 3;

        for (Map.Entry<Integer,Integer> entry: freq.entrySet()){
            double probOfPixel = (double) entry.getValue() / numPixels;
            entropy += probOfPixel * (Math.log(probOfPixel) / Math.log(2));
        }
        
        return -entropy;
    }
}