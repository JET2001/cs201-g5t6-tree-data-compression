package basic_huffman;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class Utility {
    private int[][][] pixels;

    public void Compress(int[][][] pixels, String outputFileName) throws IOException {
        this.pixels = pixels;

        // Convert the 3D pixel array to a 1D array of pixel values
        List<Integer> pixelValues = new ArrayList<>();
        for (int[][] row : pixels) {
            for (int[] pixel : row) {
                for (int value : pixel) {
                    pixelValues.add(value);
                }
            }
        }

        // Build the frequency map of pixel values
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int value : pixelValues) {
            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
        }

        // Build the Huffman tree
        HuffmanTree huffmanTree = new HuffmanTree(frequencyMap);

        // Encode the pixel values using the Huffman tree
        StringBuilder encodedData = new StringBuilder();
        for (int value : pixelValues) {
            encodedData.append(huffmanTree.encode(value));
        }

        // Write the encoded data to a compressed zip file
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFileName);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zipOutputStream)) {
            zipOutputStream.putNextEntry(new ZipEntry("compressed_data.bin"));
            BitOutputStream bitOutputStream = new BitOutputStream(bufferedOutputStream);
            bitOutputStream.writeBits(encodedData.toString());
            bitOutputStream.close();
        } catch (IOException e) {
            System.err.println("Error compressing data: " + e.getMessage());
            throw e;
        }
    }

    public int[][][] Decompress(String inputFileName) throws IOException, ClassNotFoundException {
        // Read the compressed data from the zip file
        List<Integer> decodedValues = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(inputFileName);
             ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(zipInputStream)) {
            zipInputStream.getNextEntry();
            BitInputStream bitInputStream = new BitInputStream(bufferedInputStream);
            int bit;
            while ((bit = bitInputStream.readBit()) != -1) {
                decodedValues.add(bit);
            }
            bitInputStream.close();
        }
         catch (IOException e) {
            System.err.println("Error decompressing data: " + e.getMessage());
            throw e;
        }
                if (decodedValues.isEmpty()) {
            throw new IOException("Decoded values are empty");
        }

        // Build the Huffman tree
        HuffmanTree huffmanTree = new HuffmanTree();

        // Decode the encoded data using the Huffman tree
        List<Integer> pixelValues = huffmanTree.decode(decodedValues);

        // Convert the 1D array of pixel values to a 3D pixel array
        int width = pixels.length;
        int height = pixels[0].length;
        int[][][] decodedPixels = new int[width][height][3];
        int index = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int i = 0; i < 3; i++) {
                    decodedPixels[x][y][i] = pixelValues.get(index++);
                }
            }
        }

        return decodedPixels;
    }

    private static class HuffmanTree {
        private Node root;

        public HuffmanTree(Map<Integer, Integer> frequencyMap) {
            PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
            for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
                priorityQueue.offer(new Node(entry.getKey(), entry.getValue()));
            }

            while (priorityQueue.size() > 1) {
                Node left = priorityQueue.poll();
                Node right = priorityQueue.poll();
                Node parent = new Node(left, right);
                priorityQueue.offer(parent);
            }

            root = priorityQueue.poll();
        }

        public HuffmanTree() {
            // Default constructor for deserialization
        }

        public String encode(int value) {
            StringBuilder code = new StringBuilder();
            encode(root, value, code);
            return code.toString();
        }

        private void encode(Node node, int value, StringBuilder code) {
            if (node.isLeaf()) {
                if (node.value == value) {
                    return;
                }
            } else {
                if (node.left != null) {
                    code.append('0');
                    encode(node.left, value, code);
                    code.deleteCharAt(code.length() - 1);
                }
                if (node.right != null) {
                    code.append('1');
                    encode(node.right, value, code);
                    code.deleteCharAt(code.length() - 1);
                }
            }
        }

        public List<Integer> decode(List<Integer> encodedData) {
            List<Integer> decodedValues = new ArrayList<>();
            Node current = root;
            for (int bit : encodedData) {
                if (bit == 0) {
                    current = current.left;
                } else if (bit == 1) {
                    current = current.right;
                }

                if (current.isLeaf()) {
                    decodedValues.add(current.value);
                    current = root;
                }
            }

            return decodedValues;
        }

        private static class Node implements Comparable<Node> {
            private int value;
            private int frequency;
            private Node left;
            private Node right;

            public Node(int value, int frequency) {
                this.value = value;
                this.frequency = frequency;
                this.left = null;
                this.right = null;
            }

            public Node(Node left, Node right) {
                this.value = -1;
                this.frequency = left.frequency + right.frequency;
                this.left = left;
                this.right = right;
            }

            public boolean isLeaf() {
                return left == null && right == null;
            }

            @Override
            public int compareTo(Node other) {
                return frequency - other.frequency;
            }
        }
    }

    private static class BitOutputStream implements Closeable {
        private OutputStream outputStream;
        private int buffer;
        private int bufferLength;

        public BitOutputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
            this.buffer = 0;
            this.bufferLength = 0;
        }

        public void writeBit(int bit) throws IOException {
            buffer = (buffer << 1) | bit;
            bufferLength++;

            if (bufferLength == 8) {
                outputStream.write(buffer);
                buffer = 0;
                bufferLength = 0;
            }
        }

        public void writeBits(String bits) throws IOException {
            for (char bit : bits.toCharArray()) {
                writeBit(bit - '0');
            }
        }

        @Override
        public void close() throws IOException {
            if (bufferLength > 0) {
                buffer <<= (8 - bufferLength);
                outputStream.write(buffer);
            }
            outputStream.close();
        }
    }

    private static class BitInputStream implements Closeable {
        private InputStream inputStream;
        private int buffer;
        private int bufferLength;

        public BitInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            this.buffer = 0;
            this.bufferLength = 0;
        }

        public int readBit() throws IOException {
            if (bufferLength == 0) {
                buffer = inputStream.read();
                if (buffer == -1) {
                    return -1;
                }
                bufferLength = 8;
            }

            int bit = (buffer >> (bufferLength - 1)) & 1;
            bufferLength--;

            return bit;
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }
    }

    // Rest of the Utility class...
}
