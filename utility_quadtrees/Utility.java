package utility_quadtrees;

import java.io.*;
import java.util.*;

interface Constants {
    int RGB_ERROR_THRESHOLD = 20;
}

public class Utility {

    public void Compress(int[][][] pixels, String outputFileName) throws IOException {
        // The following is a bad implementation that we have intentionally put in the
        // function to make App.java run, you should
        // write code to reimplement the function without changing any of the input
        // parameters, and making sure the compressed file
        // gets written into outputFileName
        Quadrant image = new Quadrant((short)0, (short)0, (short)(pixels.length-1), (short)(pixels[0].length-1), pixels);

        QuadTreeUtils.splitQuadrant(image, pixels);

        // System.out.println("image ="+ image);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFileName))) {
            oos.writeObject(image);
        }
    }

    public int[][][] Decompress(String inputFileName) throws IOException, ClassNotFoundException {
        // The following is a bad implementation that we have intentionally put in the
        // function to make App.java run, you should
        // write code to reimplement the function without changing any of the input
        // parameters, and making sure that it returns
        // an int [][][]
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFileName))) {
            Object object = ois.readObject();

            if (object instanceof Quadrant) {
                Quadrant image = (Quadrant) object;
                int width = QuadTreeUtils.countPixelsInRow(image);
                int height = QuadTreeUtils.countPixelsInCol(image);
                int[][][] pixels = new int[width][height][3];

                QuadTreeUtils.restoreImage(image, pixels);
                return pixels;

            } else {
                throw new IOException("Invalid object type in the input file");
            }
        }
    }
}

/**
 * In the end, we want to save a tree of quadrant objects, 
 * which should be as
 * small as possible.
 * Leaving all the utility for quadtrees outside this class.
 */
class Quadrant implements Serializable {
    // Indices of the top left corner (all inclusive)
    short topLeftX, topLeftY;
    
    // Indices of the top right corner (all inclusive)
    short lowerRightX, lowerRightY;

    // Children
    Quadrant[] children = null;

    // Average colours for quadrant
    short avgRed, avgGreen, avgBlue;

    public Quadrant(short tlX, short tlY, short lrX, short lrY, int[][][] pixels) {
        this.topLeftX = tlX;
        this.topLeftY = tlY;
        this.lowerRightX = lrX;
        this.lowerRightY = lrY;

        short[] avgColors = ImageUtils.getAverageColour(pixels, topLeftX, topLeftY, lowerRightX, lowerRightY);
        avgRed = avgColors[0];
        avgGreen = avgColors[1];
        avgBlue = avgColors[2];
    }

    public String toString() {
        // return "Quadrant [TopLeft(x,y) = (" + topLeftX + ", " + topLeftY + "), BottomRight(x,y) = (" + lowerRightX
        //         + ", " + lowerRightY
        //         + ", children = " + Arrays.toString(children) + ", avgRGB= [" + avgRed + ", " + avgGreen + ", "
        //         + avgBlue + "] ]";
        return "Quadrant [TopLeft(x,y) = ("+ topLeftX +", "+ topLeftY+"), BottomRight(x,y) = ("+ lowerRightX + ", " + lowerRightY +")]";
    }
}

class ImageUtils {
    private ImageUtils() {
    }

    public static short[] getAverageColour(int[][][] pixels, short tlX, short tlY, short lrX, short lrY) {
        // System.out.println("tlX, tlY, lrX, lrY "+ tlX +" " + tlY + " " + lrX + " " + lrY);
        int totalRed = 0, totalGreen = 0, totalBlue = 0;
        for (int x = tlX; x <= lrX; ++x) {
            for (int y = tlY; y <= lrY; ++y) {
                totalRed += pixels[x][y][0];
                totalGreen += pixels[x][y][1];
                totalBlue += pixels[x][y][2];
            }
        }
        int totalPixelsInRegion = QuadTreeUtils.countOfPixelsInRegion(tlX, tlY, lrX, lrY);
        return new short[] { (short) (totalRed / totalPixelsInRegion), (short) (totalGreen / totalPixelsInRegion),
                (short) (totalBlue / totalPixelsInRegion) }; // short[3]{avgRed, avgGreen, avgBlue}
    }
}

/*
 * All QuadTreeImplementation from this class
 */
class QuadTreeUtils {
    // private static final short THRESHOLD = 20;

    private QuadTreeUtils() {
    }

    public static void splitQuadrant(Quadrant quad, int[][][] pixels) {
        // Base case - if quadrant has less than 4 pixels, return.
        // Scanner sc = new Scanner(System.in);
        if (QuadTreeUtils.countOfPixelsInRegion(quad) < 4) return;

        Quadrant[] subQuadrants = getSubQuadrants(quad, pixels);
        boolean quadSplit = false;
        for (Quadrant subQuad : subQuadrants){
            if (errorAboveThreshold(quad, subQuad)){
                quad.children = subQuadrants;
                quadSplit = true;
                break;
            }
        }
        if (!quadSplit || isLeaf(quad)) return;
        // System.out.println("Splitting "+ quad);
        // System.out.println("Children of quad = " + Arrays.toString(quad.children));
        // Recursively call split quadrant on the children
        for (Quadrant subQuad : quad.children){
            splitQuadrant(subQuad, pixels);
        }
    }

    /*
     * This returns a boolean if the RGB of one of the quadrants is above the 
     * threshold. For now, this just uses Root Mean Squared Error of RGB
     */
    public static boolean errorAboveThreshold(Quadrant quad, Quadrant subQuad){
        short[] avgRGBQuad = {quad.avgRed, quad.avgGreen, quad.avgBlue};
        short[] avgRGBSub = {subQuad.avgRed, subQuad.avgGreen, subQuad.avgBlue};
        return RGBErrorUtils.computeRMSE(avgRGBQuad, avgRGBSub) > Constants.RGB_ERROR_THRESHOLD;
    }

    public static Quadrant[] getSubQuadrants(Quadrant quad, int[][][] pixels){
        // Check if there are enough rows / columns before we make a quadrant
        if(QuadTreeUtils.countPixelsInCol(quad) <= 2 || QuadTreeUtils.countPixelsInRow(quad) <= 2) return new Quadrant[0];

        short[] middlePoint = getMiddle(quad);
        
        Quadrant topLeft = new Quadrant(quad.topLeftX, quad.topLeftY, (short)(middlePoint[0]-1), (short)(middlePoint[1]-1), pixels);
        
        Quadrant topRight = new Quadrant(middlePoint[0], quad.topLeftY, quad.lowerRightX, (short)(middlePoint[1]-1), pixels);
        
        Quadrant bottomLeft = new Quadrant(quad.topLeftX, middlePoint[1], middlePoint[0], quad.lowerRightY, pixels);

        Quadrant bottomRight = new Quadrant(middlePoint[0], middlePoint[1], quad.lowerRightX, quad.lowerRightY, pixels);
        
        Quadrant[] subQuadrants = new Quadrant[]{topLeft, topRight, bottomLeft, bottomRight};

        // Check if any quadrant overflow the bounds of the parent boundary
        for (Quadrant subQuad: subQuadrants){
            if (!withinBoundary(quad, subQuad)) return new Quadrant[0];
        }
        return subQuadrants;
    }

    /*
     * Checks if the quadrant "inner" is within the quadrant "outer"
     */
    public static boolean withinBoundary(Quadrant outer, Quadrant inner){
        System.out.println("outer = " + outer);
        System.out.println("inner = " + inner);
        boolean withinBound = !((inner.topLeftX < outer.topLeftX) || (inner.lowerRightX > outer.lowerRightX) || (inner.topLeftY < outer.topLeftY) || (inner.lowerRightY > outer.lowerRightY));

        System.out.println("within boundary = " + withinBound);
        return withinBound;

        // return !((inner.topLeftX < outer.topLeftX) || (inner.lowerRightX > outer.lowerRightX) || (inner.topLeftY < outer.topLeftY) || (inner.lowerRightY > outer.lowerRightY)); 
    }

    public static short[] getMiddle(Quadrant quad){
        short middleX = (short)((quad.topLeftX + quad.lowerRightX) / 2);
        short middleY = (short)((quad.topLeftY + quad.lowerRightY) / 2);
        return new short[]{middleX, middleY};
    }

    public static int countOfPixelsInRegion(short tlX, short tlY, short lrX, short lrY){
        return (lrX - tlX + 1) * (lrY - tlY + 1);
    }

    public static int countOfPixelsInRegion(Quadrant quad){
        return countOfPixelsInRegion(quad.topLeftX, quad.topLeftY, quad.lowerRightX, quad.lowerRightY);
    }

    public static int countPixelsInRow(Quadrant quad){
        return (quad.lowerRightX - quad.topLeftX + 1);
    }

    public static int countPixelsInCol(Quadrant quad){
       return (quad.lowerRightY - quad.topLeftY + 1); 
    }

    public static void restoreImage(Quadrant quad, int[][][] pixels){
        if (quad == null) return;
        int width = countPixelsInRow(quad), height = countPixelsInCol(quad);

        // Only use the RGB values of the leaf nodes
        if (isLeaf(quad)){
            // Fill pixels with the average RGB value
            for (int x = quad.topLeftX; x <= quad.lowerRightX; ++x){
                for (int y = quad.topLeftY; y <= quad.lowerRightY; ++y){
                    pixels[x][y][0] = quad.avgRed;
                    pixels[x][y][1] = quad.avgGreen;
                    pixels[x][y][2] = quad.avgBlue;
                }
            }
        } else {
            for (Quadrant subQuad: quad.children){
                restoreImage(subQuad, pixels);
            }
        }
    }

    public static boolean isLeaf(Quadrant quad){
        return quad.children == null || Arrays.equals(quad.children, new Quadrant[0]);
    }
}

class RGBErrorUtils {
    private RGBErrorUtils(){}
    public static double computeRMSE(short[] rgb1, short[] rgb2){
        double error = 0.0;
        for (int i = 0; i < rgb1.length; ++i) error += Math.pow((rgb1[i] - rgb2[i]),2);
        return Math.sqrt(error);
    }
}