package utility_basic;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utility {
    // Inner class for representing Huffman nodes
    private static class HuffmanNode implements Serializable {
        private int color;
        private int frequency;
        private HuffmanNode left;
        private HuffmanNode right;

        // constructor, getters and setters
        HuffmanNode(int color, int frequency) {
            this.color = color;
            this.frequency = frequency;
        }

        public int getColor() {
            return color;
        }

        public HuffmanNode getLeft() {
            return left;
        }

        public HuffmanNode getRight() {
            return right;
        }

        boolean hasLeaf() {
            return left == null && right == null;
        }
    }

    // Inner class for representing a Huffman tree
    private static class HuffmanTree implements Serializable {
        // Fields for image dimensions, color depth, and the root node of the Huffman tree

        private final int imageWidth;
        private final int imageHeight;
        private final int colorDepth;
        private HuffmanNode root;

        // Constructor to build the Huffman tree
        public HuffmanTree(int[][][] imagePixels, Map<Integer, Integer> colorFrequencies) {
            // Initialize image dimensions and color depth
            this.imageWidth = imagePixels.length;
            this.imageHeight = imagePixels[0].length;
            this.colorDepth = imagePixels[0][0].length;

            // Build the Huffman tree
            this.root = buildHuffmanTree(imagePixels, colorFrequencies);
        }

        // Getters for image dimensions and color depth
        public int getImageWidth() {
            return imageWidth;
        }

        public int getImageHeight() {
            return imageHeight;
        }

        public int getColorDepth() {
            return colorDepth;
        }

        // Getter for the root node of the Huffman tree
        public HuffmanNode getRoot() {
            return root;
        }

        // Private method to build the Huffman tree
        private HuffmanNode buildHuffmanTree(int[][][] imagePixels, Map<Integer, Integer> colorFrequencies) {
            // Create a priority queue to hold Huffman nodes
            PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>((a, b) -> a.frequency - b.frequency);

            // Populate the priority queue with leaf nodes
            for (Entry<Integer, Integer> entry : colorFrequencies.entrySet()) {
                HuffmanNode node = new HuffmanNode(entry.getKey(), entry.getValue());
                priorityQueue.offer(node);
            }

            // Build the Huffman tree by combining nodes
            while (priorityQueue.size() > 1) {
                HuffmanNode left = priorityQueue.poll();
                HuffmanNode right = priorityQueue.poll();
                HuffmanNode parent = new HuffmanNode(0, left.frequency + right.frequency);
                parent.left = left;
                parent.right = right;
                priorityQueue.offer(parent);
            }

            // Return the root of the Huffman tree
            return priorityQueue.poll();
        }

        // Public method to generate Huffman codes for each color value
        public Map<Integer, String> generateHuffmanCodes() {
            Map<Integer, String> huffmanCodes = new HashMap<>();
            if (root != null) {
                StringBuilder code = new StringBuilder("");
                generateHuffmanCodes(root, code, huffmanCodes);
            }
            return huffmanCodes;
        }

        // Private recursive method to generate Huffman codes for tree nodes
        private void generateHuffmanCodes(HuffmanNode node, StringBuilder code, Map<Integer, String> huffmanCodes) {
            if (node.hasLeaf()) {
                // If it's a leaf node, store the Huffman code
                huffmanCodes.put(node.color, code.toString());
            } else {
                if (node.left != null) {
                    // Traverse left with '0' appended to the code
                    code.append("0");
                    generateHuffmanCodes(node.left, code, huffmanCodes);
                    code.deleteCharAt(code.length() - 1);
                }
                if (node.right != null) {
                    // Traverse right with '1' appended to the code
                    code.append("1");
                    generateHuffmanCodes(node.right, code, huffmanCodes);
                    code.deleteCharAt(code.length() - 1);
                }
            }
        }
    }

    // Public class for representing a point in a multi-dimensional space
    public class Point {
        private int[] coordinates;
        private int dimension = 3;

        public Point(int[] coordinates) {
            this.coordinates = coordinates;
        }

        // Method to get the coordinate value at a specific dimension
        public int get(int dimension) {
            return coordinates[dimension];
        }
    }

    private static class KDNode {
        Point point;
        KDNode left;
        KDNode right;

        KDNode(Point point) {
            this.point = point;
        }
    }

    private static class KDTree {
        private KDNode root;
        private int maxNodesToVisit;
        private double maxDepth;

        public KDTree(List<Point> points, int maxNodesToVisit, double maxDepth) {
            this.maxNodesToVisit = maxNodesToVisit;
            this.maxDepth = maxDepth;
            // Build the KD-tree from the given list of points
            root = buildKDTree(points, 0);
        }

        private KDNode buildKDTree(List<Point> points, int depth) {
            if (points.isEmpty()) {
                return null;
            }
            int axis = depth % points.get(0).dimension;

            // Sort the points based on the current axis and find the median
            Comparator<Point> comparator = Comparator.comparing(point -> point.get(axis));
            points.sort(comparator);

            int medianIndex = points.size() / 2;
            KDNode root = new KDNode(points.get(medianIndex));

            // Create the left and right subtrees using the points to the left and right of
            // the median
            root.left = buildKDTree(points.subList(0, medianIndex), depth + 1);
            root.right = buildKDTree(points.subList(medianIndex + 1, points.size()), depth + 1);

            return root;
        }

        // Method to find the nearest neighbor to a given target point
        public Point findNearestNeighbor(Point target) {
            return findNearestNeighbor(root, target, 0, maxNodesToVisit).point;
        }

        // Recursive method to find the nearest neighbor within the KD-tree
        private KDNode findNearestNeighbor(KDNode node, Point target, int depth, int nodesLeft) {
            if (node == null || nodesLeft <= 0 || depth == this.maxDepth) {
                return null;
            }

            int axis = depth % target.dimension;

            KDNode nextBranch = null;
            KDNode oppositeBranch = null;

            if (target.get(axis) < node.point.get(axis)) {
                nextBranch = node.left;
                oppositeBranch = node.right;
            } else {
                nextBranch = node.right;
                oppositeBranch = node.left;
            }

            KDNode best = minDistance(node, findNearestNeighbor(nextBranch, target, depth + 1, nodesLeft - 1), target);

            if (distance(best.point, target) > Math.abs(node.point.get(axis) - target.get(axis))) {
                best = minDistance(best, findNearestNeighbor(oppositeBranch, target, depth + 1, nodesLeft - 1), target);
            }

            return best;
        }

        // Helper method to find the node with the minimum distance to a target point
        private KDNode minDistance(KDNode a, KDNode b, Point target) {
            if (a == null) {
                return b;
            } else if (b == null) {
                return a;
            } else if (distance(a.point, target) < distance(b.point, target)) {
                return a;
            } else {
                return b;
            }
        }

        // Helper method to calculate the Euclidean distance between two points
        private double distance(Point a, Point b) {
            double sum = 0;
            for (int i = 0; i < a.dimension; i++) {
                double diff = a.get(i) - b.get(i);
                sum += diff * diff;
            }
            return sum;
        }
    }

    // Method to perform k-d tree quantization on image pixels
    public int[][][] kdQuantization(int[][][] imagePixels, int maxNodes, double maxDepth) {
        // Convert the image pixels to a list of points
        List<Point> points = Arrays.stream(imagePixels)
                .flatMap(Arrays::stream)
                .map(pixel -> new Point(Arrays.stream(pixel).toArray()))
                .collect(Collectors.toList());

        // Build the k-d tree
        KDTree kdTree = new KDTree(points, maxNodes, maxDepth);

        // Quantize the image pixels
        IntStream.range(0, imagePixels.length).parallel().forEach(x -> {
            IntStream.range(0, imagePixels[0].length).parallel().forEach(y -> {
                int[] coordinates = Arrays.stream(imagePixels[x][y]).toArray();
                Point point = new Point(coordinates);
                Point nearestNeighbor = kdTree.findNearestNeighbor(point);
                IntStream.range(0, imagePixels[0][0].length).forEach(z -> {
                    imagePixels[x][y][z] = nearestNeighbor.get(z);
                });
            });
        });

        return imagePixels;
    }

    // uniform quantization
    public int[][][] uniformQuantization(int[][][] imagePixels, int numberOfColors) {
        int binSize = imagePixels[0].length / numberOfColors;
        for (int x = 0; x < imagePixels.length; x++) {
            for (int y = 0; y < imagePixels[0].length; y++) {
                for (int z = 0; z < imagePixels[0][0].length; z++) {
                    int color = imagePixels[x][y][z];
                    int binIndex = color / binSize;
                    int quantizedColor = binSize * binIndex + binSize / 2;
                    imagePixels[x][y][z] = quantizedColor;
                }
            }
        }
        return imagePixels;
    }

    public int[][][] adaptiveQuantization(int[][][] imagePixels) {
        // Calculate the standard deviation of the color values
        int numberOfColors = 0;
        double stdDev = calculateStandardDeviation(imagePixels);

        // Determine the number of colors based on the standard deviation
        if (stdDev < 50) {
            // Simple image
            numberOfColors = 8;
            int maxNodesToVisit = 11;
            int maxDepth = 11;

            int[][][] quantizedImagePixels =  uniformQuantization(imagePixels, numberOfColors);
            return kdQuantization(quantizedImagePixels, maxNodesToVisit, maxDepth);
            
        } else {
            // Complex image
            numberOfColors = 8;

            return uniformQuantization(imagePixels, numberOfColors);
        }
    }

    private double calculateStandardDeviation(int[][][] imagePixels) {
        int sum = 0;
        int count = 0;
        for (int[][] row : imagePixels) {
            for (int[] pixel : row) {
                for (int color : pixel) {
                    sum += color;
                    count++;
                }
            }
        }
        double mean = (double) sum / count;

        double sumOfSquares = 0;
        for (int[][] row : imagePixels) {
            for (int[] pixel : row) {
                for (int color : pixel) {
                    sumOfSquares += Math.pow(color - mean, 2);
                }
            }
        }

        return Math.sqrt(sumOfSquares / count);
    }

    // Method to compress image data and save it to a file
    public void Compress(final int[][][] imagePixels, final String outputFileName) throws IOException {
        // Quantize the image data
        int[][][] quantizedImagePixels = adaptiveQuantization(imagePixels);
        
        // quantizedImagePixels = kdQuantization(quantizedImagePixels, numberOfColors, maxNodesToVisit, maxDepth);

        // Calculate color frequencies in the image
        Map<Integer, Integer> colorFrequencies = calculateColorFrequencies(quantizedImagePixels);

        // Build Huffman tree based on color frequencies
        HuffmanTree huffmanTree = new HuffmanTree(quantizedImagePixels, colorFrequencies);

        // Generate Huffman codes for each color value
        Map<Integer, String> huffmanCodes = huffmanTree.generateHuffmanCodes();

        // Encode the image data using Huffman codes
        List<String> encodedImageData = encodeImageData(imagePixels, huffmanCodes);

        // Build the compressed data string
        String compressedDataString = buildCompressedDataString(encodedImageData);

        // Convert the binary string to bytes
        byte[] compressedDataBytes = convertBinaryStringToBytes(compressedDataString);

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputFileName)))) {
            // Save the Huffman tree and compressed data
            oos.writeObject(huffmanTree);
            oos.writeObject(compressedDataBytes);
        }
    }

    // Method to decompress image data from a file
    public int[][][] Decompress(final String inputFileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(inputFileName)))) {
            // Read the Huffman tree from the input file
            HuffmanTree huffmanTree = (HuffmanTree) ois.readObject();

            // Read the compressed data as a byte array
            byte[] compressedDataByteArray = (byte[]) ois.readObject();

            // Reconstruct the original image pixels
            int[][][] quantizedImagePixels = reconstructImagePixels(huffmanTree, compressedDataByteArray);

            // no need to dequantize the image pixels
            return quantizedImagePixels;
        }
    }

    // Convert a binary string to bytes
    public static byte[] convertBinaryStringToBytes(final String binaryString) {
        int binaryStringLength = binaryString.length();
        int byteCount = (binaryStringLength + 7) / 8;
        byte[] bytes = new byte[byteCount];

        for (int i = 0; i < byteCount; i++) {
            int start = i * 8;
            int end = Math.min(start + 8, binaryStringLength);
            String chunk = binaryString.substring(start, end);
            bytes[i] = (byte) Integer.parseInt(chunk, 2);
        }

        return bytes;
    }

    // Calculate color frequencies in the image
    private Map<Integer, Integer> calculateColorFrequencies(int[][][] imagePixels) {
        Map<Integer, Integer> colorFrequencies = new HashMap<>();
        for (int[][] row : imagePixels) {
            for (int[] pixel : row) {
                for (int color : pixel) {
                    colorFrequencies.put(color, colorFrequencies.getOrDefault(color, 0) + 1);
                }
            }
        }
        return colorFrequencies;
    }

    // Encode image data using Huffman codes
    private List<String> encodeImageData(int[][][] imagePixels, Map<Integer, String> huffmanCodes) {
        List<String> encodedImageData = new ArrayList<>();
        for (int[][] row : imagePixels) {
            for (int[] pixel : row) {
                for (int color : pixel) {
                    encodedImageData.add(huffmanCodes.get(color));
                }
            }
        }
        return encodedImageData;
    }

    // Build the compressed data string
    private String buildCompressedDataString(List<String> encodedData) {
        return encodedData.stream().collect(Collectors.joining());
    }

    // Reconstruct the image pixels from compressed data
    private int[][][] reconstructImagePixels(HuffmanTree huffmanTree, byte[] compressedDataBytes) {
        int currentBit = 0;
        HuffmanNode currentNode = huffmanTree.getRoot();

        int imageWidth = huffmanTree.getImageWidth();
        int imageHeight = huffmanTree.getImageHeight();
        int colorDepth = huffmanTree.getColorDepth();
        int[][][] imagePixels = new int[imageWidth][imageHeight][colorDepth];

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                for (int z = 0; z < colorDepth; z++) {
                    currentNode = huffmanTree.getRoot();
                    while (true) {
                        if (currentBit >= compressedDataBytes.length * 8) {
                            break;
                        }
                        int currentByte = compressedDataBytes[currentBit >> 3];
                        int bit = (currentByte >> (7 - (currentBit % 8))) & 1;
                        currentBit++;

                        currentNode = (bit == 0) ? currentNode.getLeft() : currentNode.getRight();

                        if (currentNode.hasLeaf()) {
                            imagePixels[x][y][z] = currentNode.getColor();
                            break;
                        }
                    }
                }
            }
        }
        return imagePixels;
    }
}
