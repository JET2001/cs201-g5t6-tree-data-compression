package utility_huffman_octree_quantised;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Utility {
    private static class HuffmanNode implements Serializable {
        private int color;
        private int frequency;
        private HuffmanNode left;
        private HuffmanNode right;

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

    private static class HuffmanTree implements Serializable {
        private final int imageWidth;
        private final int imageHeight;
        private final int colorDepth;
        private HuffmanNode root;

        public HuffmanTree(int[][][] imagePixels, Map<Integer, Integer> colorFrequencies) {
            this.imageWidth = imagePixels.length;
            this.imageHeight = imagePixels[0].length;
            this.colorDepth = imagePixels[0][0].length;
            this.root = buildHuffmanTree(imagePixels, colorFrequencies);
        }

        public int getImageWidth() {
            return imageWidth;
        }

        public int getImageHeight() {
            return imageHeight;
        }

        public int getColorDepth() {
            return colorDepth;
        }

        public HuffmanNode getRoot() {
            return root;
        }

        private HuffmanNode buildHuffmanTree(int[][][] imagePixels, Map<Integer, Integer> colorFrequencies) {
            PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>((a, b) -> a.frequency - b.frequency);

            for (Entry<Integer, Integer> entry : colorFrequencies.entrySet()) {
                HuffmanNode node = new HuffmanNode(entry.getKey(), entry.getValue());
                priorityQueue.offer(node);
            }

            while (priorityQueue.size() > 1) {
                HuffmanNode left = priorityQueue.poll();
                HuffmanNode right = priorityQueue.poll();
                HuffmanNode parent = new HuffmanNode(0, left.frequency + right.frequency);
                parent.left = left;
                parent.right = right;
                priorityQueue.offer(parent);
            }

            return priorityQueue.poll();
        }

        public Map<Integer, String> generateHuffmanCodes() {
            Map<Integer, String> huffmanCodes = new HashMap<>();
            if (root != null) {
                String code = "";
                generateHuffmanCodes(root, code, huffmanCodes);
            }
            return huffmanCodes;
        }

        private void generateHuffmanCodes(HuffmanNode node, String code, Map<Integer, String> huffmanCodes) {
            if (node.hasLeaf()) {
                huffmanCodes.put(node.color, code);
            } else {
                if (node.left != null) {
                    generateHuffmanCodes(node.left, code + "0", huffmanCodes);
                }
                if (node.right != null) {
                    generateHuffmanCodes(node.right, code + "1", huffmanCodes);
                }
            }
        }
    }

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

    final int numberOfColors = 24; // range from 2 - 256 (24 is the sweet spot)

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

    public int[][][] dither(int[][][] imagePixels) {
        int[][][] ditheredImagePixels = new int[imagePixels.length][imagePixels[0].length][imagePixels[0][0].length];

        for (int y = 0; y < imagePixels[0].length; y++) {
            for (int x = 0; x < imagePixels.length; x++) {
                for (int z = 0; z < imagePixels[0][0].length; z++) {
                    int oldPixel = imagePixels[x][y][z];
                    int newPixel = oldPixel < 128 ? 0 : 255;
                    ditheredImagePixels[x][y][z] = newPixel;
                    int quantError = oldPixel - newPixel;

                    if (x + 1 < imagePixels.length) {
                        imagePixels[x + 1][y][z] += quantError * 7 / 16;
                    }
                    if (y + 1 < imagePixels[0].length) {
                        if (x - 1 >= 0) {
                            imagePixels[x - 1][y + 1][z] += quantError * 3 / 16;
                        }
                        imagePixels[x][y + 1][z] += quantError * 5 / 16;
                        if (x + 1 < imagePixels.length) {
                            imagePixels[x + 1][y + 1][z] += quantError * 1 / 16;
                        }
                    }
                }
            }
        }

        return ditheredImagePixels;
    }

    // uniform quantization
    public int[][][] quantization(int[][][] imagePixels, int numberOfColors) {
        int binSize = (int) Math.ceil(256 / numberOfColors);
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

    // Method to compress image data and save it to a file
    public void Compress(final int[][][] imagePixels, final String outputFileName) throws IOException {
        // Quantize the image data
        int[][][] quantizedImagePixels = quantization(imagePixels, numberOfColors);
        quantizedImagePixels = octreeQuantization(quantizedImagePixels, 8);

        // Calculate color frequencies in the image
        Map<Integer, Integer> colorFrequencies = calculateColorFrequencies(quantizedImagePixels);

        // Build Huffman tree based on color frequencies
        HuffmanTree huffmanTree = buildHuffmanTree(quantizedImagePixels, colorFrequencies);

        // Generate Huffman codes for each color value
        Map<Integer, String> huffmanCodes = huffmanTree.generateHuffmanCodes();

        // Encode the image data using Huffman codes
        List<String> encodedImageData = encodeImageData(quantizedImagePixels, huffmanCodes);

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

            // Dequantize the image pixels
            // return quantization(quantizedImagePixels, numberOfColors);
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

    // Build a Huffman tree based on color frequencies
    private HuffmanTree buildHuffmanTree(int[][][] imagePixels, Map<Integer, Integer> colorFrequencies) {
        return new HuffmanTree(imagePixels, colorFrequencies);
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
        StringBuilder compressedDataBuilder = new StringBuilder();
        for (String code : encodedData) {
            compressedDataBuilder.append(code);
        }
        return compressedDataBuilder.toString();
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
