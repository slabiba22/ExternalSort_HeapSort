import java.io.IOException;
import java.io.RandomAccessFile;

// -------------------------------------------------------------------------
/**
 * A Single buffer object
 * 
 * @author labibasajjad
 * @version Jun 25, 2024
 */
public class Buffer implements BufferADT {

    // ~ Fields ................................................................
    private RandomAccessFile file;
    private byte[] data;
    private int blockIndex;
    private boolean dirty;

    // ----------------------------------------------------------
    /**
     * Create a new Buffer object.
     * 
     * @param file
     *            the file from which the buffer will read from
     * @param blockIndex
     *            index of block for random access file.
     */
    public Buffer(RandomAccessFile file, int blockIndex) {
        this.file = file;
        this.data = new byte[4096]; // initialize a buffer to a hold a 4096 byte
                                    // block
        this.blockIndex = blockIndex;
        this.dirty = false;
        readBlock(); // load data from file to buffer
    }


    // Read the block of data from the file into the buffer
    @Override
    public byte[] readBlock() {
        try {

            // calculate where to start reading data from and then point to that
            // index and fina;;y read the data and returns it
            long fileOffset = (long)blockIndex * 4096;
            file.seek(fileOffset);
            file.readFully(data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return data;

    }


    // returns pointer to the buffer data
    @Override
    public byte[] getDataPointer() {
        return data;
    }


    // marks a buffer dirty which is needed after modificatioms.
    @Override
    public void markDirty() {
        this.dirty = true;

    }


    // ----------------------------------------------------------
    /**
     * Checks is buffer has been modified.
     * 
     * @return the status of modification of the buffer
     */
    public boolean isDirty() {
        return dirty;
    }


    // ----------------------------------------------------------
    /**
     * Writes back to the file after it has been modified.
     */
    public void writeBuffer() {
        if (dirty) {

            // calculate where to start writing from and writes to file
            long fileOffset = (long)blockIndex * 4096;
            try {
                file.seek(fileOffset);
                file.write(data);
            }
            catch (IOException e) {

                e.printStackTrace();
            }

            // mark buffer as not ndirty after writing to file
            dirty = false;
        }
    }


    /**
     * Getter method for the block index
     * 
     * @return the block index
     * 
     */
    public int getBlockIndex() {
        return blockIndex;
    }

}
