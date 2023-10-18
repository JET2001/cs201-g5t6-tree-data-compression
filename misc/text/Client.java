package utility.text;

public class Client {
    public static void main(String[] args) {
        Huffman huffman = new Huffman();
        String binary = huffman.encode("I ate an apple at a bar");
        String humanReadable = huffman.decode(binary, huffman.getTree());
        System.out.println(humanReadable);
    }
}
