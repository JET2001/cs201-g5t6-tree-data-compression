package utility_huffman;

import java.util.*;
import java.io.*;

public class HuffmanCompress {
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

    // Compress the input file and save it as the output file
    public void compress(File inputFile, File outputFile) {
        Compare comp = new Compare();
        queue = new PriorityQueue<HuffmanTree>(12, comp);
        HashMap<Byte, String> map = new HashMap<Byte, String>();

        int i, char_kinds = 0;
        int char_temp, file_len = 0;

        FileInputStream fis = null;
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

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

        try {
            fis = new FileInputStream(inputFile);
            fos = new FileOutputStream(outputFile);
            oos = new ObjectOutputStream(fos);

            // Calculate character frequencies
            while ((char_temp = fis.read()) != -1) {
                ++tmp_nodes[char_temp].weight;
                ++file_len;
            }
            fis.close();
            Arrays.sort(tmp_nodes);

            // Calculate the number of different characters
            for (i = 0; i < 256; ++i) {
                if (tmp_nodes[i].weight == 0)
                    break;
            }
            char_kinds = i;

            if (char_kinds == 1) {
                // Handle the case with only one character
                oos.writeInt(char_kinds);
                oos.writeByte(tmp_nodes[0].bit);
                oos.writeInt(tmp_nodes[0].weight);
            } else {
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
                tmp_nodes = null;

                for (; i < node_num; ++i) {
                    huf_tree[i] = new HuffmanTree();
                    huf_tree[i].parent = 0;
                }

                // Create Huffman tree
                createTree(huf_tree, char_kinds, node_num, queue);
                // Generate Huffman codes
                hufCode(huf_tree, char_kinds);
                oos.writeInt(char_kinds);
                for (i = 0; i < char_kinds; ++i) {
                    oos.writeByte(huf_tree[i].bit);
                    oos.writeInt(huf_tree[i].weight);
                    map.put(huf_tree[i].bit, huf_tree[i].code);
                }
                oos.writeInt(file_len);
                fis = new FileInputStream(inputFile);
                code_buf = "";

                // Save Huffman code which has been converted into binary
                while ((char_temp = fis.read()) != -1) {
                    code_buf += map.get((byte) char_temp);

                    while (code_buf.length() >= 8) {
                        char_temp = 0;
                        for (i = 0; i < 8; ++i) {
                            char_temp <<= 1;
                            if (code_buf.charAt(i) == '1')
                                char_temp |= 1;
                        }
                        oos.writeByte((byte) char_temp);
                        code_buf = code_buf.substring(8);
                    }
                }

                // If the length of the last code is not 8 bits, add 0 to fill up
                if (code_buf.length() > 0) {
                    char_temp = 0;
                    for (i = 0; i < code_buf.length(); ++i) {
                        char_temp <<= 1;
                        if (code_buf.charAt(i) == '1')
                            char_temp |= 1;
                    }
                    char_temp <<= (8 - code_buf.length());
                    oos.writeByte((byte) char_temp);
                }
            }
            oos.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// public static int[][][] Decompress(String inputFileName) throws IOException, ClassNotFoundException {
//     File decompressedFile = new File(inputFileName + ".bin")
// }
    // 
    // Decompress the input file and save it as the output file
    public void decompress(File inputFile, File outputFile) {
        Compare comp = new Compare();
        queue = new PriorityQueue<HuffmanTree>(12, comp);
        int i;
        int file_len = 0;
        int writen_len = 0;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        ObjectInputStream ois = null;

        int char_kinds = 0;
        int node_num;
        HuffmanTree[] huf_tree = null;
        byte code_temp;
        int root;
        try {
            fis = new FileInputStream(inputFile);
            ois = new ObjectInputStream(fis);
            fos = new FileOutputStream(outputFile);

            char_kinds = ois.readInt();

            if (char_kinds == 1) {
                code_temp = ois.readByte();
                file_len = ois.readInt();
                while ((file_len--) != 0) {
                    fos.write(code_temp);
                }
            } else {
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
                    code_temp = ois.readByte();
                    for (i = 0; i < 8; ++i) {
                        if ((code_temp & 128) == 128) {
                            root = huf_tree[root].rchild;
                        } else {
                            root = huf_tree[root].lchild;
                        }
                        if (root < char_kinds) {
                            fos.write(huf_tree[root].bit);
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
            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
