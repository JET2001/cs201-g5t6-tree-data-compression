import java.util.*;

public class App {

    public static void main(String... args){
        short[][][] img = {
           { 
              {1, -2, 3}, 
              {2, 3, 4}
            }, 
            {
              {-4, -5, 6, 9}, 
              {1}, 
              {2, 3}
            },
            { 
              {-4, -5, 6, 9}, 
              {1}, 
              {2, 3}
            }
        };

        short[][][] compressed = UtilityTest.Compress(img);

        System.out.println("compressed = " + compressed);

        short[][][] decompressed = UtilityTest.Decompress(compressed);
        System.out.println("uncompressed = " + Arrays.toString(decompressed));
    }
}