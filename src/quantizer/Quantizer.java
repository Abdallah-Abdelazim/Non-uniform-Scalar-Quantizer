package quantizer;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Quantizer {
    private int average = 0;
    private ArrayList<Integer> pixelsList = new ArrayList<>();
    private Quantizer left = null;
    private Quantizer right = null;

    private ArrayList<Integer> averagesList = new ArrayList<>(); // only for root quantizer

    public ArrayList<Integer> getAveragesList() {
        return averagesList;
    }

    public void setAveragesList(ArrayList<Integer> averagesList) {
        this.averagesList = averagesList;
    }


    public void compress(int n, int[][] originalPixels, int width, int height, String outputFileLocation)
                                                                throws IOException {
        Quantizer quantizer = new Quantizer();  // root

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                quantizer.pixelsList.add(originalPixels[j][i]);
            }
        }

        // calculate average
        int sum = 0;
        for (int i = 0; i < quantizer.pixelsList.size(); i++) { // sum all values to get average next
            sum += quantizer.pixelsList.get(i);
        }
        quantizer.average = (sum / quantizer.pixelsList.size());


        setTree(n, quantizer);

        preorder(quantizer);  // fills the averagesList


        // generate Intervals ranges
        ArrayList<Integer> ranges = new ArrayList<>();
        ranges.add(0);
        for (int i = 0; i < averagesList.size() - 1; i++) {
            int c = (averagesList.get(i) + averagesList.get(i + 1)) / 2;
            ranges.add(c);
        }
        ranges.add(255); // full scale = 255 = 11111111 , (270)

        int[][] quantizedPixels = new int[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int x = 0; x < (ranges.size() - 1); x++) {
                    if (originalPixels[j][i] >= ranges.get(x) && originalPixels[j][i] < ranges.get(x + 1)) {
                        quantizedPixels[j][i] = x;   // add index of the interval
                    }
                }
            }
        }


        //write quantized pixels in file

        FileWriter fwriter = new FileWriter(outputFileLocation, false);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                fwriter.write(quantizedPixels[j][i] + " ");  // quantizedPixels
            }
        }
        fwriter.close();
    }


    private void preorder(Quantizer root) {
        if ((root.left.left == null && root.left.right == null) || (root.right.left == null && root.right.right == null)) {
            //averagesList.add(root.average);
            averagesList.add(root.left.average);
            averagesList.add(root.right.average);
            return;
        }
        //System.out.println(root.average+ " ");
        preorder(root.left);
        preorder(root.right);
    }


    private void setTree(int n, Quantizer q) {
        if (n == 0) return; // base case
        else {
            int a = q.average - 1;  // left
            int b = q.average + 1;  // right

            q.left = new Quantizer();
            q.right = new Quantizer();

            // split
            for (int i = 0; i < q.pixelsList.size(); i++) {
                int d1, d2;
                d1 = Math.abs(a - q.pixelsList.get(i));
                d2 = Math.abs(b - q.pixelsList.get(i));
                if (d2 > d1) {
                    q.left.pixelsList.add(q.pixelsList.get(i));  // value is closer to the left
                } else {
                    q.right.pixelsList.add(q.pixelsList.get(i));
                }  // value is closer to the right
            }

            //calculate average for left
            if (q.left.pixelsList.size() == 0){
                q.left.average = q.average; // it's not actually q.average .. just for preOrder
            }else{
                int sumLeft = 0;
                for (int i = 0; i < q.left.pixelsList.size(); i++) { // sum all values to get average next
                    sumLeft += q.left.pixelsList.get(i);
                }
                q.left.average = (sumLeft / q.left.pixelsList.size());
            }

            // calculate average for right
            if (q.right.pixelsList.size() == 0){
                q.right.average = q.average;  // it's not actually q.average .. just for preOrder
            }else{
                int sumRight = 0;
                for (int i = 0; i < q.right.pixelsList.size(); i++) { // sum all values to get average next
                    sumRight += q.right.pixelsList.get(i);
                }
                q.right.average = (sumRight / q.right.pixelsList.size());
            }

            n--;
            setTree(n, q.left);
            setTree(n, q.right);
        }
    }


    public int[][] decompress(File sourceFile, int width, int height) throws  FileNotFoundException{  // dequantize
        //read from file
        ArrayList<Integer> quantizedPixelsList = new ArrayList<>();  // numbers from file in ArrayList

        Scanner scanner = new Scanner(sourceFile);
        while (scanner.hasNextInt()) {
            quantizedPixelsList.add(scanner.nextInt());
        }


        int[][] dequantizedPixels = new int[height][width];
        int c = 0;
        for (int i = 0; i < width; i++) {
            for (int y = 0; y < height; y++) {
                dequantizedPixels[y][i] = quantizedPixelsList.get(c);  // numbers from file in 2D array
                c++;
            }

        }

        // replace each number from file (Interval index) with the Interval Average
        for (int i = 0; i < width; i++) {
            for (int y = 0; y < height; y++) {
                dequantizedPixels[y][i] = averagesList.get(dequantizedPixels[y][i]);
            }
        }
        return dequantizedPixels;

    }

}
