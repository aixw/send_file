import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * 客户端启动
 * @author xiongqiqmeng
 * @since 2019/12/20 16:33
 */
public class Client {
    /**
     * 服务端ip
     */
    private static String SERVER_HOST;
    /**
     * 服务端端口
     */
    private static int SERVER_PROT;

    /**
     * 需传输文件路径
     */
    private static String FILE_PATH;

    private static final int BUFFER_SIZE = 1024  * 1024 * 10;

    public static void main(String[] args) throws Exception {
        while (FILE_PATH == null) {
            getConnectParams(args);
        }

        long start =System.currentTimeMillis();
        File inputFile = new File(FILE_PATH);
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        FileChannel fc = fileInputStream.getChannel();

        System.out.println("当前传出文件大小" + fc.size());

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PROT));

        //发送文件名
        sendString(socketChannel, inputFile.getName());

        while (true) {
            if (readString(socketChannel).equals("ok")) {
                break;
            }
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        while ((fc.read(byteBuffer)) != -1) {
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        fc.close();
        socketChannel.close();
        long end = System.currentTimeMillis();
        System.out.println("have spend " + (end - start) + "毫秒");
    }

    private static void getConnectParams(String[] args) throws Exception {
        if (args == null || args.length < 3) {
            throw new Exception("需传入参数服务器ip、端口号、传输文件路径");
        }

        SERVER_HOST = args[0];
        SERVER_PROT = Integer.parseInt(args[1]);
        FILE_PATH = args[2];

        System.out.println(SERVER_HOST + "|"  + SERVER_PROT + "|" + FILE_PATH);
    }

    private static void sendString(SocketChannel socketChannel, String str) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
        socketChannel.write(buffer);
    }

    private static String readString(SocketChannel socketChannel) throws IOException {
        StringBuffer sb = new StringBuffer();
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        while (byteBuffer.hasRemaining()) {
            sb.append((char) byteBuffer.get());
        }
        return sb.toString();
    }


}
