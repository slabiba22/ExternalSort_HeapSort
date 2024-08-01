// On my honor:
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or unmodified.
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
// - I have not discussed coding details about this project with
// anyone other than the instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.IOException;

// -------------------------------------------------------------------------
/**
 * Sorts the items in the file using the buffer pool
 * 
 * @author labibasajjad
 * @version Jun 25, 2024
 */
public class HeapSort {

    private BufferPool pool;

    // ----------------------------------------------------------
    /**
     * Create a new HeapSort object.
     * 
     * @param pool
     *            is the buffer pool that needs to be sorted
     */
    public HeapSort(BufferPool pool) {
        this.pool = pool;
    }


    // ----------------------------------------------------------
    /**
     * Main program that runs the program
     * 
     * @param args
     *            arguments taken my main to run the program
     */
    public static void main(String[] args) {

        if (args.length != 3) {
            System.err.println("Incorrect Paramaters");
            System.exit(1);
        }

        String dataFileName = args[0];
        int buffNum = Integer.parseInt(args[1]);
        String statFileName = args[2];

        File dataFile = new File(dataFileName);
        BufferPool pool = new BufferPool(dataFile, buffNum);
        HeapSort heapSort = new HeapSort(pool);

        long sTime = System.currentTimeMillis();
        heapSort.sort();
        long eTime = System.currentTimeMillis();

        pool.flush();
        heapSort.writeStatistics(statFileName, dataFileName, eTime - sTime);
        // heapSort.printStatistics(statFileName);

    }


    // ----------------------------------------------------------
    /**
     * Main heap sort method
     */
    public void sort() {
        // calculates the no.of records to be sorted
        int numRec = (int)pool.getFileSize() / 4;

        // build heap
        for (int i = numRec / 2 - 1; i >= 0; i--) {
            heapify(numRec, i);

        }

        // extract from heap one by one
        for (int i = numRec - 1; i > 0; i--) {
            swap(0, i); // move root to end
            heapify(i, 0); // call heapify
        }
    }


    private void heapify(int n, int i) {
        while (true) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            // if leff child is ;arger than root
            if (left < n && getKey(left) > getKey(largest)) {
                largest = left;
            }

            // if right child is larger than root
            if (right < n && getKey(right) > getKey(largest)) {
                largest = right;
            }

            // if largest is not root
            if (largest == i) {
                break;
            }

            // swap the root with the largest item
            swap(i, largest);
            // continue heapifying
            i = largest;
        }
    }


    private int getKey(int i) {
        int blckIndx = i / 1024;
        // offset within the block
        int offset = (i % 1024) * 4;
        // get the buffer for the block
        Buffer buffer = pool.acquireBuffer(blckIndx);
        byte[] data = buffer.getDataPointer();

        // extract the first 2 bytes which is the key
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }


    private void swap(int i, int j) {

        int blockI = i / 1024;
        int blockJ = j / 1024;

        int offI = (i % 1024) * 4;
        int offJ = (j % 1024) * 4;

        Buffer bufferI = pool.acquireBuffer(blockI);
        Buffer bufferJ = pool.acquireBuffer(blockJ);

        byte[] dataI = bufferI.getDataPointer();
        byte[] dataJ = bufferJ.getDataPointer();

        // swap al;l 4 of the bytes
        boolean modified = false;
        for (int k = 0; k < 4; k++) {
            byte temp = dataI[offI + k];
            dataI[offI + k] = dataJ[offJ + k];
            dataJ[offJ + k] = temp;
            modified = true;
        }

        if (modified) {
            bufferI.markDirty();
            bufferJ.markDirty();
        }

    }


    private void writeStatistics(
        String statFileName,
        String dataFileName,
        long elapsedTime) {
        try (PrintStream out = new PrintStream(new FileOutputStream(
            statFileName, true))) {
            out.println("Standard sort on " + dataFileName);
            out.println("Cache hits: " + pool.getCacheHits());
            out.println("Disk reads: " + pool.getDiskReads());
            out.println("Disk writes: " + pool.getDiskWrites());
            out.println("Execution time: " + elapsedTime + " ms");
            out.println();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    // ----------------------------------------------------------
    /**
     * Prints the stats on the console for testing
     * 
     * @param statFileName
     *            the file to be printed
     */
    public static void printStatistics(String statFileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(
            statFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
