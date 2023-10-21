package misc.text;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;

class HuffmanNode {
    int occurrences;
    char character;
    HuffmanNode left;
    HuffmanNode right;
}

// For comparing the nodes
class HuffmanComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y) {
        return x.occurrences - y.occurrences;
    }
}
// Implementing the huffman algorithm
public class Huffman {
    private Map<Character,String> charToBinaryMap;
    private HuffmanNode root;

    //basic encoding of string from char to binary 
    //used once proper mapping determined
    public Huffman() {
        this.charToBinaryMap = new HashMap<>();
        this.root = null;
    }

    //root node to access of the tree
    public HuffmanNode getTree() {
        return this.root;
    }

    public String decode(String binaryString, HuffmanNode root) {
        HuffmanNode cur = root;
        StringBuilder sb = new StringBuilder();
        for (int i = 0;  i < binaryString.length(); i++) {
            //right is null and next step would be to go right || left is null and next step is left
            if ((cur.right == null && binaryString.charAt(i) == '1') || (cur.left == null && binaryString.charAt(i) == '0')) {
                i--;
                sb.append(cur.character);
                cur = root;
                continue;
            }

            if (binaryString.charAt(i) == '0') {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        sb.append(cur.character);

        return sb.toString();
    }

    public String encode(String humanReadableStr) {

        //create a map of character to occurrences in the string
        Map<Character, Integer> characterOccurrenceMap = new HashMap<>();
        for (Character ch:humanReadableStr.toCharArray()) {
            characterOccurrenceMap.put(ch, characterOccurrenceMap.getOrDefault(ch, 0) + 1);
        }

        int n = characterOccurrenceMap.size();

        //use the map to create a priority queue by the occurrences
        PriorityQueue<HuffmanNode> huffmanPriorityQueue = new PriorityQueue<>(n, new HuffmanComparator());
        characterOccurrenceMap.forEach((key, value) -> {
            HuffmanNode hn = new HuffmanNode();

            hn.character = key;
            hn.occurrences = value;
            hn.left = null;
            hn.right = null;

            huffmanPriorityQueue.add(hn);
        });

        //transform priority queue into a huffman tree
        while (huffmanPriorityQueue.size() > 1) {

            HuffmanNode x = huffmanPriorityQueue.peek();
            huffmanPriorityQueue.poll();

            HuffmanNode y = huffmanPriorityQueue.peek();
            huffmanPriorityQueue.poll();

            HuffmanNode dummy = new HuffmanNode();

            dummy.occurrences = x.occurrences + y.occurrences;
            dummy.character = '-';
            dummy.left = x;
            dummy.right = y;
            this.root = dummy;

            huffmanPriorityQueue.add(dummy);
        }

        //display freq map and create char to binary string map
        System.out.println(" Char | Huffman code | occurrences");
        System.out.println("----------------------------------");
        createCharToBinaryMap(this.root, "");

        // get output (binary) string
        StringBuilder sb = new StringBuilder();
        for (Character ch:humanReadableStr.toCharArray()) {
            sb.append(this.charToBinaryMap.get(ch));
        }
        String out = sb.toString();

        //display encoding stats
        int originalBits = humanReadableStr.getBytes().length * 8;
        int treeBits = characterOccurrenceMap.size() * 8 + humanReadableStr.length();
        int binaryBits = out.length();
        System.out.println("Original bits:" + originalBits);
        System.out.println("Binary Encoding: " + out);
        System.out.println("After bits (content): " + binaryBits);
        System.out.println("After bits (tree): " + treeBits);
        System.out.println("After bits (total): " + (treeBits + binaryBits));
        System.out.println("Compression ratio: " + ((1.0 * (treeBits + binaryBits))/originalBits));

        return out;
    }

    private void createCharToBinaryMap(HuffmanNode cur, String binary) {
        if (cur.left == null && cur.right == null) {
            this.charToBinaryMap.put(cur.character, binary);
            System.out.println(cur.character + "   |  " + binary +  "  |  " + cur.occurrences);
            return;
        }
        createCharToBinaryMap(cur.left, binary + "0");
        createCharToBinaryMap(cur.right, binary + "1");
    }

}