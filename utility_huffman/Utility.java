package utility_huffman;

import java.util.*;
import java.io.*;
import java.util.Objects.*;

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

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Node)) return false;
        Node other = (Node) o;
        return other.bit == bit && other.weight == weight;
    }

    @Override
    public int hashCode(){
        return Objects.hash(bit, weight);
    }
}

public class Utility {

    // class Compare implements Comparator<HuffmanTree> {

    //     @Override
    //     public int compare(HuffmanTree o1, HuffmanTree o2) {
    //         if (o1.weight < o2.weight)
    //             return -1;
    //         else if (o1.weight > o2.weight)
    //             return 1;
    //         return 0;
    //     }
    // }

    // class Node implements Comparable<Node> {
    //     public byte bit; // The character (byte)
    //     public int weight; // Frequency of the character

    //     @Override
    //     public int compareTo(Node arg0) {
    //         if (this.weight < arg0.weight)
    //             return 1;
    //         else if (this.weight > arg0.weight)
    //             return -1;
    //         return 0;
    //     }
    // }

    private PriorityQueue<HuffmanTree> queue = null;

    // Compress the input pixels and save it as the output file
    public void Compress(int[][][] pixels, String outputFileName) throws IOException {
        Compare comp = new Compare();
        queue = new PriorityQueue<HuffmanTree>(12, comp);
        HashMap<Byte, String> map = new HashMap<>();
    
        int i, char_kinds = 0;
        int char_temp, file_len = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        
        int node_num;
        HuffmanTree[] huf_tree = null;
        String code_buf = null;
        Node[] tmp_nodes = new Node[256]; // Assuming 256 possible pixel values
        
        for (i = 0; i < 256; ++i) {
            tmp_nodes[i] = new Node();
            tmp_nodes[i].weight = 0;
            tmp_nodes[i].bit = (byte) i; // Pixel values are used as-is
        }
        
        for (int[][] pixel : pixels) {
            for (int[] rgb : pixel) {
                char_temp =(byte) rgb[0]; // Assuming pixel values are single integers
                ++tmp_nodes[char_temp & 0xFF].weight; // Ensure it's treated as a positive byte value
                ++file_len;
            }
        }
        
        Arrays.sort(tmp_nodes);
        
        for (i = 0; i < 256; ++i) {
            if (tmp_nodes[i].weight == 0)
                break;
        }
        char_kinds = i;
        
        if (char_kinds == 1) {
            oos.writeInt(char_kinds);
            oos.writeInt(tmp_nodes[0].bit);
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
            
            createTree(huf_tree, char_kinds, node_num, queue);
            hufCode(huf_tree, char_kinds);
            oos.writeInt(char_kinds);
            
            for (i = 0; i < char_kinds; ++i) {
                oos.writeInt(huf_tree[i].bit); // Store pixel values as integers
                oos.writeInt(huf_tree[i].weight);
                map.put(huf_tree[i].bit, huf_tree[i].code);
            }
            oos.writeInt(file_len);
            
            code_buf = "";
            
            for (int[][] pixel : pixels) {
                for (int[] rgb : pixel) {
                    char_temp = rgb[0]; // Assuming pixel values are single integers
                    code_buf += map.get(char_temp);
                    
                    while (code_buf.length() >= 8) {
                        char_temp = 0;
                        for (i = 0; i < 8; ++i) {
                            char_temp <<= 1;
                            if (code_buf.charAt(i) == '1') {
                                char_temp |= 1;
                            }
                        }
                        baos.write(char_temp);
                        code_buf = code_buf.substring(8);
                    }
                }
            }
            
            if (code_buf.length() > 0) {
                char_temp = 0;
                for (i = 0; i < code_buf.length(); ++i) {
                    char_temp <<= 1;
                    if (code_buf.charAt(i) == '1') {
                        char_temp |= 1;
                    }
                }
                char_temp <<= (8 - code_buf.length());
                baos.write(char_temp);
            }
        }
        
        FileOutputStream fos = new FileOutputStream(outputFileName);
        baos.writeTo(fos);
        oos.close();
        fos.close();
    }

    public int[][][] Decompress(String inputFileName) throws IOException, ClassNotFoundException {
        Compare comp = new Compare();
        queue = new PriorityQueue<HuffmanTree>(12, comp);
        int i;
        int file_len = 0;
        int writen_len = 0;
        try (FileInputStream fis = new FileInputStream(inputFileName);
            ByteArrayInputStream bais = new ByteArrayInputStream(fis.readAllBytes());
            ObjectInputStream ois = new ObjectInputStream(bais)) {

            int char_kinds = ois.readInt();
            int node_num;
            HuffmanTree[] huf_tree = null;
            int code_temp;
            int root;

            if (char_kinds == 1) {
                code_temp = ois.readInt();
                file_len = ois.readInt();
                if (file_len < 0) {
                    throw new IOException("Invalid file length: " + file_len);
                }
                int[][][] pixels = new int[file_len][1][1];

                for (int j = 0; j < file_len; j++) {
                    pixels[j][0][0] = code_temp;
                }
                return pixels;
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

                int[][][] pixels = new int[file_len][1][1];
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
                            pixels[writen_len][0][0] = huf_tree[root].bit;
                            ++writen_len;

                            if (writen_len == file_len) {
                                break;
                            }
                            root = node_num - 1;
                        }
                        code_temp <<= 1;
                    }

                    if (writen_len == file_len) {
                        break;
                    }
                }
                return pixels;
            }
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
