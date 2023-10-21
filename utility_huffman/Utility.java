package utility_huffman;

import java.util.*;
import java.io.*;

public class Utility {
    class HuffmanTree {
        public byte bit; // The character (byte) represented by this node
        public int weight; // Frequency of the character
        public String code; // Huffman code representing this character

        public int index; // Index of this node in the Huffman tree
        public int parent, lchild, rchild; // Indices of parent and children nodes
    }

    class Compare implements Comparator<HuffmanTree> {
        @Override
        public int compare(HuffmanTree o1, HuffmanTree o2) {
            if (o1.weight < o2.weight)
                return -1;
            else if (o1.weight > o2.weight)
                return 1;
            return 0;
        }
    }

    class Node implements Comparable<Node> {
        public byte bit; // The character (byte)
        public int weight; // Frequency of the character

        @Override
        public int compareTo(Node arg0) {
            if (this.weight < arg0.weight)
                return 1;
            else if (this.weight > arg0.weight)
                return -1;
            return 0;
        }
    }

    private PriorityQueue<HuffmanTree> queue = null;

    // Compress the input pixel data and save it as the output file
    public void Compress(int[][][] pixels, String outputFileName) throws IOException {
        Compare comp = new Compare();
        queue = new PriorityQueue<HuffmanTree>(12, comp);
        HashMap<Byte, String> map = new HashMap<Byte, String>();

        int i, char_kinds = 0;
        int file_len = 0;

        int node_num;
        HuffmanTree[] huf_tree = null;
        String code_buf = null;
        Node[] tmp_nodes = new Node[256];

        // Initialize frequency array for each character
        for (i = 0; i < 256; ++i) {
            tmp_nodes[i] = new Node();
            tmp_nodes[i].weight = 0;
            tmp_nodes[i].bit = (byte) i;
        }

        // Calculate character frequencies from pixel data
        for (int[][] row : pixels) {
            for (int[] pixel : row) {
                byte red = (byte) pixel[0];
                byte green = (byte) pixel[1];
                byte blue = (byte) pixel[2];
                
                tmp_nodes[red & 0xFF].weight++;
                tmp_nodes[green & 0xFF].weight++;
                tmp_nodes[blue & 0xFF].weight++;
                
                file_len += 3;
            }
        }

        try {
            // Create Huffman tree and generate codes
            node_num = 2 * char_kinds - 1;
            huf_tree = new HuffmanTree[node_num];
            for (i = 0; i < char_kinds; ++i) {
                huf_tree[i] = new HuffmanTree();
                huf_tree[i].bit = tmp_nodes[i].bit;
                huf_tree[i].weight = tmp_nodes[i].weight;
                huf_tree[i].parent = 0;
                huf_tree[i].index = i;
                queue.add(huf_tree[i]);
            }
            for (; i < node_num; ++i) {
                huf_tree[i] = new HuffmanTree();
                huf_tree[i].parent = 0;
            }
            createTree(huf_tree, char_kinds, node_num, queue);
            hufCode(huf_tree, char_kinds);

            // Start saving compressed data
            FileOutputStream fos = new FileOutputStream(outputFileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Write character kinds, character frequencies, and file length
            oos.writeInt(char_kinds);
            for (i = 0; i < char_kinds; ++i) {
                oos.writeByte(huf_tree[i].bit);
                oos.writeInt(huf_tree[i].weight);
                map.put(huf_tree[i].bit, huf_tree[i].code);
            }
            oos.writeInt(file_len);

            // Write Huffman-coded data
            int currentByte = 0;
            int bitsFilled = 0;

            for (int[][] row : pixels) {
                for (int[] pixel : row) {
                    byte char_temp = (byte) pixel[0];
                    String code = map.get(char_temp);
                    for (char bit : code.toCharArray()) {
                        currentByte <<= 1;
                        if (bit == '1') {
                            currentByte |= 1;
                        }
                        bitsFilled++;
                        if (bitsFilled == 8) {
                            oos.writeByte((byte) currentByte);
                            currentByte = 0;
                            bitsFilled = 0;
                        }
                    }
                }
            }

            // Flush any remaining bits
            if (bitsFilled > 0) {
                currentByte <<= (8 - bitsFilled);
                oos.writeByte((byte) currentByte);
            }

            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Decompress the input file and return the decompressed pixel data
    public int[][][] Decompress(String inputFileName) throws IOException, ClassNotFoundException {
        Compare comp = new Compare();
        queue = new PriorityQueue<HuffmanTree>(12, comp);
    
        int i;
        int file_len = 0;
        int writen_len = 0;
    
        int char_kinds = 0;
        int node_num = 0;
        HuffmanTree[] huf_tree = null;
        int root;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
        try {
            FileInputStream fis = new FileInputStream(inputFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
    
            char_kinds = ois.readInt();
    
            if (char_kinds == 1) {
                byte code_temp = ois.readByte();
                file_len = ois.readInt();
                while (file_len-- != 0) {
                    baos.write(code_temp);
                }
            } else {
                // Create Huffman tree for decompression
                node_num = 2 * char_kinds - 1;
                huf_tree = new HuffmanTree[node_num];
                for (i = 0; i < char_kinds; ++i) {
                    huf_tree[i] = new HuffmanTree();
                    huf_tree[i].bit = ois.readByte();
                    huf_tree[i].weight = ois.readInt();
                    huf_tree[i].parent = 0;
                    huf_tree[i].index = i;
                    queue.add(huf_tree[i]);
                }
                for (; i < node_num; ++i) {
                    huf_tree[i] = new HuffmanTree();
                    huf_tree[i].parent = 0;
                }
                createTree(huf_tree, char_kinds, node_num, queue);
                file_len = ois.readInt();
                root = node_num - 1;
    
                while (true) {
                    byte code_temp = ois.readByte(); //error here
                    for (i = 0; i < 8; ++i) {
                        if ((code_temp & 128) == 128) {
                            root = huf_tree[root].rchild;
                        } else {
                            root = huf_tree[root].lchild;
                        }
                        if (root < char_kinds) {
                            baos.write(huf_tree[root].bit);
                            ++writen_len;
                            if (writen_len == file_len)
                                break;
                            root = node_num - 1;
                        }
                        code_temp <<= 1;
                    }
                    if (writen_len == file_len)
                        break;
                }
            }
    
            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        // Convert the ByteArrayOutputStream to an array of pixel data
        int[][][] pixels = new int[file_len / 3][1][3]; //error here
        int pixelIndex = 0;
        byte[] byteArray = baos.toByteArray();
    
        for (byte code_temp : byteArray) {
            int temp_root = node_num - 1;
            for (int j = 7; j >= 0; j--) {
                if ((code_temp & (1 << j)) != 0) {
                    temp_root = huf_tree[temp_root].rchild;
                } else {
                    temp_root = huf_tree[temp_root].lchild;
                }
                if (temp_root < char_kinds) {
                    pixels[pixelIndex][0][0] = huf_tree[temp_root].bit;
                    temp_root = node_num - 1;
                } else if (temp_root < 2 * char_kinds) {
                    pixels[pixelIndex][0][1] = huf_tree[temp_root].bit;
                    temp_root = node_num - 1;
                } else if (temp_root < 3 * char_kinds) {
                    pixels[pixelIndex][0][2] = huf_tree[temp_root].bit;
                    pixelIndex++;
                    temp_root = node_num - 1;
                }
            }
        }
        return pixels;
    }
    

    public void createTree(HuffmanTree[] huf_tree, int char_kinds, int node_num, PriorityQueue<HuffmanTree> queue) {
        int i;
        int[] arr = new int[2];
        for (i = char_kinds; i < node_num; ++i) {
            arr[0] = queue.poll().index;
            arr[1] = queue.poll().index;
            huf_tree[arr[0]].parent = huf_tree[arr[1]].parent = i;
            huf_tree[i].lchild = arr[1];
            huf_tree[i].rchild = arr[0];
            huf_tree[i].weight = huf_tree[arr[0]].weight + huf_tree[arr[1]].weight;
            huf_tree[i].index = i;
            queue.add(huf_tree[i]);
        }
    }

    public void hufCode(HuffmanTree[] huf_tree, int char_kinds) {
        int i;
        int cur, next;
        for (i = 0; i < char_kinds; ++i) {
            String code_tmp = "";
            for (cur = i, next = huf_tree[i].parent; next != 0; cur = next, next = huf_tree[next].parent) {
                if (huf_tree[next].lchild == cur)
                    code_tmp += "0";
                else
                    code_tmp += "1";
            }
            huf_tree[i].code = (new StringBuilder(code_tmp)).reverse().toString();
        }
    }
}
