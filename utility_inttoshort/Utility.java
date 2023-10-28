package utility_inttoshort;

import java.io.*;
import java.util.*;

public class Utility {

    public void Compress(int[][][] pixels, String outputFileName) throws IOException {
        // The following is a bad implementation that we have intentionally put in the function to make App.java run, you should 
        // write code to reimplement the function without changing any of the input parameters, and making sure the compressed file
        // gets written into outputFileName

        short[][] compressedPixels = new short[pixels.length][pixels[0].length];

        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                int red = pixels[i][j][0];
                int green = pixels[i][j][1];
                int blue = pixels[i][j][2];

                red /= 16;
                red = (red << 8) & 0x0F00;

                green /= 16;
                green = (green << 4) & 0x00F0;

                blue /= 16;
                blue = blue & 0x000F;

                short color = (short) (red | green | blue);

                compressedPixels[i][j] = color;
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFileName))) { 
            oos.writeObject(compressedPixels); 
        }
    }

    public int[][][] Decompress(String inputFileName) throws IOException, ClassNotFoundException {
        // The following is a bad implementation that we have intentionally put in the function to make App.java run, you should 
        // write code to reimplement the function without changing any of the input parameters, and making sure that it returns
        // an int [][][]



        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFileName))) {
            Object object = ois.readObject();
            System.out.println(object.getClass());
            if (object instanceof short[][]) {
                short[][] compressedPixels = (short[][]) object;
                int[][][] pixels = new int[compressedPixels.length][compressedPixels[0].length][3];

                for (int i = 0 ; i < compressedPixels.length ; i++) {
                    for (int j = 0 ; j < compressedPixels[0].length ; j++) {
                        short color = compressedPixels[i][j];

                        Random rand = new Random();
                        int rand1 = rand.nextInt(16);
                        int rand2 = rand.nextInt(16);
                        int rand3 = rand.nextInt(16);

                        short temp = color;
                        int red = temp >>> 8;
                        red *= 16;
                        red += rand1;

                        temp = color;
                        int green = (temp << 8) >>> 12;
                        green *= 16;
                        green += rand2;

                        temp = color;
                        int blue = (temp << 12) >>> 12;
                        blue *= 16;
                        blue += rand3;

                        pixels[i][j][0] = red;
                        pixels[i][j][1] = green;
                        pixels[i][j][2] = blue;
                    
                    }
                }
                return pixels;
            } else {
                throw new IOException("Invalid object type in the input file");
            }
        }
    }

}