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
        File file = new File("1.txt");
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        FileChannel fc = randomAccessFile.getChannel();
        MappedByteBuffer mappedByteBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, LEN);

        mappedByteBuffer.put(ByteBuffer.wrap("just a simple test for java nio mmap write a1".getBytes()));
        mappedByteBuffer.flip();

        while (mappedByteBuffer.hasRemaining()) {
            System.out.print((char)mappedByteBuffer.get());
        }

        fc.close();
        randomAccessFile.close();
    }

}
