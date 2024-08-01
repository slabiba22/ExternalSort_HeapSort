import java.io.*;
import java.util.LinkedList;

// -------------------------------------------------------------------------
/**
 * Buffer pool that handles mulitple buffers
 * 
 * @author labibasajjad
 * @version Jun 23, 2024
 */
public class BufferPool implements BufferPoolADT {

    // ~ Fields ................................................................
    private RandomAccessFile rF;
    private Buffer[] bufferList;
    private LinkedList<Integer> lruList;
    private int maxBuff;
    private int cacheHits = 0;
    private int dReads = 0;
    private int dWrites = 0;

    // ----------------------------------------------------------
    /**
     * Create a new BufferPool object.
     * 
     * @param file
     *            write back to the file.
     * @param maxBuff
     *            max number of buffers in the pool
     */
    public BufferPool(File file, int maxBuff) {
        try {
            this.rF = new RandomAccessFile(file, "rw");
            this.maxBuff = maxBuff;
            this.bufferList = new Buffer[maxBuff];
            this.lruList = new LinkedList<>();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public Buffer acquireBuffer(int blockIndex) {

        int bufferIndex = findBuffIndex(blockIndex);

        // if buffer is already in the pool
        if (bufferIndex != -1) {
            updateLRU(bufferIndex);
            cacheHits++;
            return bufferList[bufferIndex];
        }
        // if buffer doesn't exist put it in the pool

        // find an open slot and if non is found it deletes the LRU buffer
        else {
            bufferIndex = findOpenPos();
            if (bufferIndex == -1) {
                // if no empty slot
                bufferIndex = removeLRUBuff();
            }

            // load data into new buffer, update list and return the buffer
            // added.
            bufferList[bufferIndex] = new Buffer(rF, blockIndex);
            updateLRU(bufferIndex);
            dReads++;
            return bufferList[bufferIndex];
        }

    }


    private void updateLRU(int bufferIndex) {
        lruList.remove((Integer)bufferIndex); // remove buff
        lruList.addLast(bufferIndex); // add to end to make it recently used

    }


    // find the buffer according to the given index
    private int findBuffIndex(int blockIndex) {
        // goes through all the buffers and returns of the index of the bufer
        // that matches block index.
        for (int i = 0; i < bufferList.length; i++) {
            if (bufferList[i] != null && bufferList[i]
                .getBlockIndex() == blockIndex) {
                return i;
            }
        }
        return -1;
    }


    // Finds the an open slot in the buffer list
    private int findOpenPos() {
        for (int i = 0; i < bufferList.length; i++) {
            if (bufferList[i] == null) {
                return i;
            }
        }
        return -1;
    }


    // removes the least recently used buffer
    private int removeLRUBuff() {
        // index of the least used buffer
        int lruIndex = lruList.removeFirst();
        // write back if it's dirty
        if (bufferList[lruIndex].isDirty()) {
            bufferList[lruIndex].writeBuffer();
            dWrites++;
        }
        // remove buff
        bufferList[lruIndex] = null;
        return lruIndex;

    }

    // update list to mark as last used


    // ----------------------------------------------------------
    /**
     * Returns the size of the file
     * 
     * @return file size in byte
     */
    public long getFileSize() {
        try {
            return rF.length();
        }
        catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }


    // ----------------------------------------------------------
    /**
     * Flushes the buffers at the end to update file properly
     */
    public void flush() {
        // interate through all the buffers in the buffer pool
        for (Buffer buffer : bufferList) {
            // if buffer is not null and it has been modfied write it back to
            // the file and update disk writes
            if (buffer != null && buffer.isDirty()) {
                buffer.writeBuffer();
                dWrites++;
            }
        }
    }


    /**
     * Getter for the number of cache hits.
     * 
     * @return the number of cache hits
     */
    public int getCacheHits() {
        return cacheHits;
    }


    /**
     * Getter for the number of disk writes.
     * 
     * @return the number of disk writes
     */
    public int getDiskWrites() {
        return dWrites;
    }


    /**
     * Getter for the number of disk reads.
     * 
     * @return the number of disk reads
     */
    public int getDiskReads() {
        return dReads;
    }


    /**
     * Getter method to get the num of max buffs
     * 
     * @return the no.of buffs allowed in the pool
     */
    public int getMaxBuff() {
        return maxBuff;
    }

}
