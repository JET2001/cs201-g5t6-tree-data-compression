package utility_basic;

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

        public void setColor(int color) {
            this.color = color;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        public HuffmanNode getLeft() {
            return left;
        }

        public void setLeft(HuffmanNode left) {
            this.left = left;
        }

        public HuffmanNode getRight() {
            return right;
        }

        public void setRight(HuffmanNode right) {
            this.right = right;
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

        public void setRoot(HuffmanNode root) {
            this.root = root;
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

    final int numberOfColors = 52; // range from 2 - 256 (52 is the sweet spot)

    public int[][][] quantize(int[][][] imagePixels, int numberOfColors) {
        int binSize = (int)Math.ceil(256 / numberOfColors);
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
        //Quantize the image data
        int[][][] quantizedImagePixels = quantize(imagePixels, numberOfColors);

        // Calculate color frequencies in the image
        Map<Integer, Integer> colorFrequencies = calculateColorFrequencies(quantizedImagePixels);

        // Build Huffman tree based on color frequencies
        HuffmanTree huffmanTree = buildHuffmanTree(imagePixels, colorFrequencies);

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
            return reconstructImagePixels(huffmanTree, compressedDataByteArray);
        }
    }

    // Convert a binary string to bytes
    public static byte[] convertBinaryStringToBytes(final String binaryString) {
        int binaryStringLength = binaryString.length();
        int byteCount = (binaryStringLength + 6) / 8;
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
        int currentByte = compressedDataBytes[currentBit >> 3];

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
                        if (currentBit % 8 == 0) {
                            currentByte = compressedDataBytes[currentBit >> 3];
                        }
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
