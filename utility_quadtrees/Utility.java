package utility_quadtrees;

import java.io.*;
import java.util.*;

interface QuadTreeConstants {
    int RMSE_THRESHOLD = 20;
}

public class Utility {
    class OctreeNode {
        int pixelCount;
        int red;
        int green;
        int blue;
        OctreeNode[] children;
    }

    class Octree {
        OctreeNode root;
        int numNodes;
        int maxNodes;
        List<List<OctreeNode>> levelBuckets;

        public Octree(int maxNodes) {
            root = new OctreeNode();
            this.maxNodes = maxNodes;
            levelBuckets = new ArrayList<>();
            for (int i = 0; i <= 8; i++) {
                levelBuckets.add(new ArrayList<>());
            }
        }

        public void addColor(int red, int green, int blue) {
            OctreeNode node = root;
            for (int level = 1; level <= 8; level++) {
                node.pixelCount++;
                node.red += red;
                node.green += green;
                node.blue += blue;
                int shift = 8 - level;
                int index = ((red & (0x1 << shift)) > 0 ? 4 : 0) |
                        ((green & (0x1 << shift)) > 0 ? 2 : 0) |
                        ((blue & (0x1 << shift)) > 0 ? 1 : 0);
                if (node.children == null) {
                    node.children = new OctreeNode[8];
                }
                if (node.children[index] == null) {
                    node.children[index] = new OctreeNode();
                    numNodes++;
                    levelBuckets.get(level).add(node.children[index]);
                }
                node = node.children[index];
            }
            node.pixelCount++;
            node.red += red;
            node.green += green;
            node.blue += blue;
        }

        public void reduceTree() {
            for (int level = 8; level >= 0; level--) {
                if (levelBuckets.get(level).size() > 0) {
                    OctreeNode node = levelBuckets.get(level).remove(0);
                    node.pixelCount /= 8;
                    node.red /= 8;
                    node.green /= 8;
                    node.blue /= 8;
                    node.children = null;
                    numNodes -= 7;
                    if (numNodes <= maxNodes) {
                        return;
                    }
                }
            }
        }

        public int[] findColor(int red, int green, int blue) {
            OctreeNode node = root;
            for (int level = 1; level <= 8; level++) {
                if (node.children == null) {
                    break;
                }
                int shift = 8 - level;
                int index = ((red & (0x1 << shift)) > 0 ? 4 : 0) |
                        ((green & (0x1 << shift)) > 0 ? 2 : 0) |
                        ((blue & (0x1 << shift)) > 0 ? 1 : 0);
                node = node.children[index];
            }
            return new int[] { node.red / node.pixelCount, node.green / node.pixelCount, node.blue / node.pixelCount };
        }
    }

    public int[][][] octreeQuantization(int[][][] imagePixels, int numberOfColors) {
        // Create an Octree and add all colors from the image
        Octree octree = new Octree(numberOfColors);
        for (int[][] row : imagePixels) {
            for (int[] pixel : row) {
                octree.addColor(pixel[0], pixel[1], pixel[2]);
            }
        }

        // Reduce the Octree to the desired number of colors
        octree.reduceTree();

        // Replace each pixel's color with the color of the Octree node that it belongs
        // to
        for (int x = 0; x < imagePixels.length; x++) {
            for (int y = 0; y < imagePixels[0].length; y++) {
                int[] color = octree.findColor(imagePixels[x][y][0], imagePixels[x][y][1],
                        imagePixels[x][y][2]);
                imagePixels[x][y] = color;
            }
        }

        return imagePixels;
    }

    //uniform quantisation
    public int[][][] quantization(int[][][] imagePixels, int numberOfColors) {
        int binSize = (int) Math.ceil(256 / numberOfColors);
        for (int x = 0; x < imagePixels.length; x++) {
            for (int y = 0; y < imagePixels[0].length; y++) {
                for (int z = 0; z < imagePixels[0][0].length; z++) {
                    //retrieves the color value of the current color channel of the current pixel.
                    int color = imagePixels[x][y][z];
                    //calculates the new color value, which is the midpoint of the bin.
                    int binIndex = color / binSize;
                    //calculates the new color value, which is the midpoint of the bin.
                    int quantizedColor = binSize * binIndex + binSize / 2;
                    //replaces the original color value with the quantized color value.
                    imagePixels[x][y][z] = quantizedColor;
                }
            }
        }
        return imagePixels;
    }

    final int numberOfColors = 24; // ranging 2 - 256 distinct color

    public void Compress(int[][][] pixels, String outputFileName) throws IOException {
        // The following is a bad implementation that we have intentionally put in the
        // function to make App.java run, you should
        // write code to reimplement the function without changing any of the input
        // parameters, and making sure the compressed file
        // gets written into outputFileName
        pixels = quantization(pixels,numberOfColors );
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
                return quantization(pixels,numberOfColors );

            } else {
                throw new IOException("Invalid object type in the input file");
            }
        }
    }
}

/**
 * In the end, we want to save a tree of quadrant objects, 
 * which should be as small as possible.
 * Leaving all the utility for quadtrees outside this class.
 * Getters and setters might take up space, so I declared all 
 * variables as package-private.
 * Might possibly reduce the size of the object. 
 */
class Quadrant implements Serializable {
    // Top left indices of box
    short topLeftX, topLeftY;
    // Bottom right indices of box
    short lowerRightX, lowerRightY;

    // Children
    Quadrant[] children = null;

    // Average colours
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
        return "Quadrant [TopLeft(x,y) = (" + topLeftX + ", " + topLeftY + "), BottomRight(x,y) = (" + lowerRightX
                + ", " + lowerRightY
                + ", children = " + Arrays.toString(children) + ", avgRGB= [" + avgRed + ", " + avgGreen + ", "
                + avgBlue + "] ]";
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
                (short) (totalBlue / totalPixelsInRegion) };
    }
}

/*
 * All QuadTreeImplementation from this class
 */
class QuadTreeUtils {

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
        return RGBErrorUtils.computeRMSE(avgRGBQuad, avgRGBSub) > QuadTreeConstants.RMSE_THRESHOLD;
    }

    /*
     * Given a quadrant, return an array of subquadrants
     */
    public static Quadrant[] getSubQuadrants(Quadrant quad, int[][][] pixels){
        // Check if there are enough rows / columns before we make a quadrant
        if(QuadTreeUtils.countPixelsInCol(quad) <= 2 || QuadTreeUtils.countPixelsInRow(quad) <= 2) return new Quadrant[0];

        short[] middlePoint = getMiddle(quad);
        
        Quadrant topLeft = new Quadrant(quad.topLeftX, quad.topLeftY, (short)(middlePoint[0]-1), (short)(middlePoint[1]-1), pixels);
        
        Quadrant topRight = new Quadrant(middlePoint[0], quad.topLeftY, quad.lowerRightX, (short)(middlePoint[1]-1), pixels);
        
        Quadrant bottomLeft = new Quadrant(quad.topLeftX, middlePoint[1], middlePoint[0], quad.lowerRightY, pixels);

        Quadrant bottomRight = new Quadrant(middlePoint[0], middlePoint[1], quad.lowerRightX, quad.lowerRightY, pixels);
        
        return new Quadrant[]{topLeft, topRight, bottomLeft, bottomRight};
    }

    /*
     * Given a quadrant, find the middle point, or the point 
     * slightly to the left and above the middle point. This
     * is necessary for images which are non-square.
     */
    public static short[] getMiddle(Quadrant quad){
        short middleX = (short)((quad.topLeftX + quad.lowerRightX) / 2);
        short middleY = (short)((quad.topLeftY + quad.lowerRightY) / 2);
        return new short[]{middleX, middleY};
    }
    /*
     * The below two methods count the number of pixels in
     * a region, row and column. These take in
     * different arguments.
     */
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
    /*
     * This is used for the restoration of images, in the
     * Decompress method.
     */
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

/*
 * This class implements various error metrics that
 * can be used for QuadTree threshold.
 */
class RGBErrorUtils {
    private RGBErrorUtils(){}
    /*
     * This computes the RMSE between the avg colours 
     * defined in rgb1 and rgb2.
     * Arrays should be of same size and have a length of 3:
     * with index 0 = avgRed, index 1 = avgGreen, index 2 =
     * avgBlue.
     */
    public static double computeRMSE(short[] rgb1, short[] rgb2){
        double error = 0.0;
        for (int i = 0; i < rgb1.length; ++i) error += Math.pow((rgb1[i] - rgb2[i]),2);
        return Math.sqrt(error);
    }
}