import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 服务端启动
 * 文件传输 version 0.1
 * @author xiongqiqmeng
 * @since 2019/12/20 16:33
 */
public class Server {
    /**
     * 服务端文件存储 path
     */
    public static String SERVER_DIR;

    /**
     * 服务端端口
     */
    public static int SERVER_PORT;

    /**
     * buffer size
     */
    private static int BUFFER_SIZE = 1024  * 1024 * 10;

    public static void main(String[] args) throws Exception {
        while (SERVER_DIR == null) {
            getConnectParams(args);
        }

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
        serverSocketChannel.configureBlocking(false);

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel == null) {
                continue;
            }

            //创建存储路径
            File dir = new File(SERVER_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            //获取文件名
            String fileName = readString(socketChannel);
            System.out.println("fileName is " + fileName);
            sendString(socketChannel, "ok");
            File savePath = new File(SERVER_DIR + File.separator + fileName);
            System.out.println("savePath is " + savePath.getPath());
            if (!savePath.exists()) {
                savePath.createNewFile();
            }

            //创建接收文件流
            RandomAccessFile randomAccessFile = new RandomAccessFile(savePath, "rw");
            FileChannel fc = randomAccessFile.getChannel();

            //数据接收
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while ((socketChannel.read(buffer)) != -1) {
                buffer.flip();
                fc.write(buffer);
                buffer.clear();
            }

            //文件流关闭
            randomAccessFile.close();
            fc.close();
            socketChannel.close();
            System.out.println("receive file success");

        }

    }

    private static void getConnectParams(String[] args) throws Exception {
        if (args == null || args.length < 2) {
            throw new Exception("需传入参数服务器文件存储路径、端口号");
        }

        SERVER_DIR = args[0];
        SERVER_PORT = Integer.parseInt(args[1]);

        System.out.println(SERVER_PORT + "|" + SERVER_DIR);
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
