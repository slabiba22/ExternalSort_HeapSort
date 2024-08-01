// -------------------------------------------------------------------------
/**
 * Bufferpool interface
 * 
 * @author labibasajjad
 * @version Jun 25, 2024
 */
public interface BufferPoolADT {

    // ----------------------------------------------------------
    /**
     * Relate a block to a buffer, returning a pointer to a buffer object
     * 
     * @param blockIndex
     *            buffer to be acquired
     * 
     * @return the buffer acquired
     */
    Buffer acquireBuffer(int blockIndex);


    /**
     * Flushes the buffers at the end to update file correctly
     */
    public void flush();


    /**
     * Getter for the number of cache hits.
     * 
     * @return the number of cache hits
     */
    public int getCacheHits();


    /**
     * Getter for the number of disk writes.
     * 
     * @return the number of disk writes
     */
    public int getDiskWrites();


    /**
     * Getter for the number of disk reads.
     * 
     * @return the number of disk reads
     */
    public int getDiskReads();


    /**
     * Getter method to get the num of max buffs
     * 
     * @return the no.of buffs allowed in the pool
     */
    public int getMaxBuff();
}
