package app;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

// ====== Import the correct utility file here =====
// import utility_sample.*;
import utility_huffman.*;
// import basic_huffman.Utility;

// =================================================

public class App {
    
    public static void Compress(int[][][] pixels, String outputFileName) throws IOException {
                        // Convert the 3D array into a 1D array
        int width = pixels.length;
        int height = pixels[0].length;
        byte[] pixelData1D = new byte[width * height * 3 + 8]; // Add 8 bytes for width and height

        // Store the width and height at the beginning of the array
        ByteBuffer buffer = ByteBuffer.wrap(pixelData1D);
        buffer.putInt(width);
        buffer.putInt(height);

        int index = 8; // Start after the width and height
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixelData1D[index++] = (byte) pixels[x][y][0]; // Red
                pixelData1D[index++] = (byte) pixels[x][y][1]; // Green
                pixelData1D[index++] = (byte) pixels[x][y][2]; // Blue
            }
        }

        // Create a temporary file to store the pixel data
        File tempFile = File.createTempFile("temp", null);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) { 
            fos.write(pixelData1D);
        }

        // Create an instance of HuffmanCompress and use it to compress the data
        HuffmanCompress huffman = new HuffmanCompress();
        huffman.compress(tempFile, new File(outputFileName));

        // Delete the temporary file
        tempFile.delete();
    }

    public static int[][][] Decompress(String inputFileName) throws IOException, ClassNotFoundException {
        // Create an instance of HuffmanCompress and use it to decompress the data
        HuffmanCompress huffman = new HuffmanCompress();
        File decompressedFile = new File(inputFileName + ".decompressed");
        huffman.decompress(new File(inputFileName), decompressedFile);

        // Read the decompressed data into a 1D array of bytes
        byte[] pixelData1D;
        try (FileInputStream fis = new FileInputStream(decompressedFile)) {
            pixelData1D = fis.readAllBytes();
        }

        // Retrieve the width and height from the beginning of the array
        ByteBuffer buffer = ByteBuffer.wrap(pixelData1D);
        int width = buffer.getInt();
        int height = buffer.getInt();

        // Convert the 1D array back into a 3D array
        int[][][] result = new int[width][height][3];
        int index = 8; // Start after the width and height
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                result[x][y][0] = pixelData1D[index++]; // Red
                result[x][y][1] = pixelData1D[index++]; // Green
                result[x][y][2] = pixelData1D[index++]; // Blue
            }
        }

        // Delete the decompressed file
        decompressedFile.delete();

        return result;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException{

        //Create an instance of Utility
        // Utility Utility = new Utility();

        //Define original file directory to loop through
        String ImageDirectory = "../Original/";
        // List all files in the directory
        File directory = new File(ImageDirectory);
        File[] files = directory.listFiles();

        if (files != null) {
            System.out.println("hello");
            for (File file : files) {
                if (file.isFile()) {
                    String imageName = file.getName();

                    //Converting image to pixels

                    ImagetoPixelConverter ImagetoPixelConverter = new ImagetoPixelConverter(ImageDirectory + imageName);

                    //Converting the image to pixels

                    int[][][] pixelData = ImagetoPixelConverter.getPixelData();
                    int width = ImagetoPixelConverter.getWidth();
                    int height = ImagetoPixelConverter.getHeight();

                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            int red = pixelData[x][y][0];
                            int green = pixelData[x][y][1];
                            int blue = pixelData[x][y][2];
                        }
                    }

                    // Now you have the image data in 'pixelData' that will be taken in by Compress

                    // Define location and name for the compressed file to be created
                    String compressed_file_name = "../Compressed/" + imageName.substring(0, imageName.lastIndexOf('.')) + ".bin";

                    // start compress timer
                    long compressStartTime = System.currentTimeMillis();
                    
                    //call compress function
                    // Utility.
                    Compress(pixelData, compressed_file_name);
                    
                    //end timer for compress and record the total time passed
                    long compressEndTime = System.currentTimeMillis();
                    long compressExecutionTime = compressEndTime - compressStartTime;
                    System.out.println("Compress Execution Time for "+ imageName + " : " + compressExecutionTime + " milliseconds");

                    //Check the original file size
                    File originalFile = new File(ImageDirectory + imageName);
                    long originalFileSize = originalFile.length();
                    System.out.println("Size of the original file for " + imageName + ": " + originalFileSize + " bytes"); 
                    
                    // Check size of the compressed file
                    File compressedFile = new File(compressed_file_name);
                    long compressedFileSize = compressedFile.length();
                    System.out.println("Size of the compressed file for " + imageName + ": " + compressedFileSize + " bytes"); 
                    
                    //Find the Difference
                    long differenceInFileSize = originalFileSize - compressedFileSize;
                    System.out.println("Bytes saved from compression of " + imageName + ": " + differenceInFileSize + " bytes"); 

                    // start decompress timer
                    long decompressStartTime = System.currentTimeMillis();

                    // call decompress function
                    // int [][][] newPixelData = Utility.Decompress(compressed_file_name);
                    int [][][] newPixelData = Decompress(compressed_file_name);
                    
                    //end timer for decompress and record the total time passed
                    long decompressEndTime = System.currentTimeMillis();
                    long decompressExecutionTime = decompressEndTime - decompressStartTime;
                    System.out.println("Decompress Execution Time for " + imageName + " : " + decompressExecutionTime + " milliseconds");
                    

                    //convert back to image for visualisation
                    PixeltoImageConverter PixeltoImageConverter = new PixeltoImageConverter(newPixelData);
                    PixeltoImageConverter.saveImage("../Decompressed/" + imageName, "png");

                    //Get the two bufferedimages for calculations
                    BufferedImage originalimage = ImageIO.read(new File(ImageDirectory + imageName));
                    BufferedImage decompressedimage = ImageIO.read(new File("../Decompressed/" + imageName)); 

                    //calculate MAE
                    double MAE = MAECalculator.calculateMAE(originalimage, decompressedimage);
                    System.out.println("Mean Absolute Error of :" + imageName + " is " + MAE) ;

                    //calculate MSE
                    double MSE = MSECalculator.calculateMSE(originalimage, decompressedimage);
                    System.out.println("Mean Squared Error of :" + imageName + " is " + MSE) ;                  

                    //calculate PSNR
                    double PSNR = PSNRCalculator.calculatePSNR(originalimage, decompressedimage);
                    System.out.println("PSNR of :" + imageName + " is " + PSNR);   

                }
            }
        }

    }
}


        
