package app;

import java.util.*;

import utility.*;
import utility.compression_utility.*; 

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
        Utility utility = UtilityFactory.createUtility();

        short[][][] compressed = utility.Compress(img);
        System.out.println("compressed = " + Arrays.toString(compressed));

        short[][][] decompressed = utility.Decompress(compressed);
        System.out.println("uncompressed = " + Arrays.toString(decompressed));
    }

    private static class UtilityFactory {
        static Utility createUtility(){
            return new UtilityTest(); // change to future utility class.
        }
    }
}