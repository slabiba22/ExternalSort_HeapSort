import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import student.TestCase;

/**
 * @author Labiba Sajjad
 * @version 1.0
 */
public class HeapSortTest extends TestCase {

    /** Sets up the tests that follow. In general, used for initialization. */
    public void setUp() throws Exception {
        super.setUp();
        systemOut().clearHistory();
    }


    // ----------------------------------------------------------
    /**
     * Tests to see if file is generated correctly
     * 
     * @throws IOException
     */
    public void testFileGen() throws IOException {
        String fname = "threeBlock.txt";
        int blocks = 3;
        FileGenerator fg = new FileGenerator(fname, blocks);
        fg.setSeed(33333333); // a non-random number to make generation
                              // deterministic
        fg.generateFile(FileType.ASCII);

        File f = new File(fname);
        long fileNumBytes = f.length();
        long calcedBytes = blocks * FileGenerator.BYTES_PER_BLOCK;
        assertEquals(calcedBytes, fileNumBytes); // size is correct!

        RandomAccessFile raf = new RandomAccessFile(f, "r");
        short firstKey = raf.readShort(); // reads two bytes
        assertEquals(8273, firstKey); // first key looks like ' Q', translates
                                      // to 8273

        raf.seek(8); // moves to byte 8, which is beginning of third record
        short thirdKey = raf.readShort();
        assertEquals(8261, thirdKey); // third key looks like ' E', translates
                                      // to 8261

        raf.close();
    }


//
//
// // ----------------------------------------------------------
    /**
     * Checks if files are sorted
     * 
     * @throws Exception
     */
    public void testCheckFile() throws Exception {
        assertTrue(CheckFile.check("tinySorted.txt"));

        String fname = "tinyUnsorted.txt";
        FileGenerator fg = new FileGenerator(fname, 1);
        fg.setSeed(42);
        fg.generateFile(FileType.ASCII);
        // Notice we *re-generate* this file each time the test runs.
        // That file persists after the test is over

        assertFalse(CheckFile.check(fname));

        String[] args = new String[3];
        args[0] = fname; // the file to be sorted.
        args[1] = "1"; // number of buffers, can impact performance
        args[2] = "stats.txt"; // filename for sorting stats
        HeapSort.main(args);

        assertTrue(CheckFile.check(fname));
    }


//
//
    /**
     * This method is a demonstration of the file generator and file checker
     * functionality. It calls generateFile to create a small binary file. It
     * then calls the file checker to see if it is sorted (presumably not since
     * we don't call a sort method in this test, so we assertFalse).
     *
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSorting1() throws Exception {
        String fname = "checkme.txt";
        FileGenerator fg = new FileGenerator(fname, 1);
        fg.generateFile(FileType.BINARY);

        assertFalse(CheckFile.check(fname)); // file shouldn't be sorted

        String[] args = new String[3];
        args[0] = fname; // the file to be sorted.
        args[1] = "1"; // number of buffers, can impact performance
        args[2] = "stats.txt"; // filename for sorting stats
        HeapSort.main(args);
        // Now the file *should* be sorted, so lets check!

        assertTrue(CheckFile.check(fname));
    }


    // ----------------------------------------------------------
    /**
     * Tests with another file
     * 
     * @throws Exception
     */
    public void testSorting2() throws Exception {
        String fname = "tinyUnsorted1.txt";
        FileGenerator fg = new FileGenerator(fname, 10);
        fg.generateFile(FileType.ASCII);

        System.out.println("Files generated successfully:");
        System.out.println("ASCII File: " + fname);

        assertFalse(CheckFile.check(fname)); // file shouldn't be sorted

        String[] args = new String[3];
        args[0] = fname; // the file to be sorted.
        args[1] = "1"; // number of buffers, can impact performance
        args[2] = "stats.txt"; // filename for sorting stats
        HeapSort.main(args);
        // Now the file *should* be sorted, so lets check!

        assertTrue(CheckFile.check(fname));
    }


    // ----------------------------------------------------------
    /**
     * Tests a large scale 10 block 1 buff
     * 
     * @throws Exception
     */
    public void testSorting3() throws Exception {
        for (int i = 0; i <= 20; i++) {
            String fname = "tinyUnsorted2.txt";
            FileGenerator fg = new FileGenerator(fname, 10);
            fg.generateFile(FileType.BINARY);

            System.out.println("Binary File: " + fname);

            assertFalse(CheckFile.check(fname)); // file shouldn't be sorted

            String[] args = new String[3];
            args[0] = fname; // the file to be sorted.
            args[1] = "1"; // number of buffers, can impact performance
            args[2] = "stats2.txt"; // filename for sorting stats
            HeapSort.main(args);
            // Now the file *should* be sorted, so lets check!

            assertTrue(CheckFile.check(fname));
        }
    }


    // @Test(timeout = 30000)
    // ----------------------------------------------------------
    /**
     * Tests test 6 from webcat
     * 
     * @throws Exception
     */
    public void testSorting4() throws Exception

    {

        String fname = "CS3114_File6_TenBlockBin_OneBuff_GenerousLimit.bin";
// FileGenerator fg = new FileGenerator(fname, 100);
// fg.generateFile(FileType.BINARY);

        System.out.println("Binary File: " + fname);

     //   assertFalse(CheckFile.check(fname)); // file shouldn't be sorted

        String[] args = new String[3];
        args[0] = fname; // the file to be sorted.
        args[1] = "10"; // number of buffers, can impact performance
        args[2] = "stats1.txt"; // filename for sorting stats
        HeapSort.main(args);
        // Now the file *should* be sorted, so lets check!
        HeapSort.printStatistics("stats1.txt");

        assertTrue(CheckFile.check(fname));

    }
    

}
