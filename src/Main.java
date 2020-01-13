import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * java nio mmap read & write
 * @author xiongqimeng
 * @version 1.0
 * @date 2020/1/7 17:16
 */
public class Main {

    public static final int LEN = 512;

    public static void main(String[] args) throws Exception {
        String str = "";
        System.out.println(isEmpty(str));
    }

    private static boolean isEmpty(String str) {
        if (str == null || str.trim().equalsIgnoreCase("")) {
            return true;
        }
        return false;
    }

}
