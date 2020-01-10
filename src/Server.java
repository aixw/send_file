import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

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

        //创建存储路径
        File dir = new File(SERVER_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int keyNum = selector.select();
//            System.out.println("keyNum ========" + keyNum);
            if (keyNum == 0) {
                continue;
            }

            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            Iterator<SelectionKey> selectionKeyIterator = selectionKeySet.iterator();
            while (selectionKeyIterator.hasNext()) {
                SelectionKey selectionKey = selectionKeyIterator.next();
                selectionKeyIterator.remove();
                if (selectionKey.isAcceptable()) {
                    System.out.println("accept come in");
//                    SocketChannel channel = serverSocketChannel.accept();
                    ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel)selectionKey.channel();
                    SocketChannel socketChannel = serverSocketChannel1.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    System.out.println("read come in");
                    doRead(selectionKey);
                } else if (selectionKey.isWritable()) {
                    System.out.println("write come in");
                    System.out.println(selectionKey.attachment());
                    doWrite(selectionKey, (String)selectionKey.attachment());
                }

            }
        }

    }

    private static void doWrite(SelectionKey selectionKey, String fileName) throws IOException {
        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
        File file = new File(SERVER_DIR + File.separator + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileChannel fc = new FileOutputStream(file).getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        while (socketChannel.read(buffer) != -1) {
            buffer.flip();
            fc.write(buffer);
            buffer.clear();
        }

        fc.close();
        socketChannel.close();
        System.out.println(fileName + " receive success");
    }

    private static String getTransType(SelectionKey selectionKey) {
        if (selectionKey == null) {
            return null;
        }

        return (String)selectionKey.attachment();
    }

    private static void doRead(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();

        String fileName = readString(socketChannel);
        System.out.println("fileName=========" + fileName);
        sendString(socketChannel, "ok");

        socketChannel.register(selectionKey.selector(), SelectionKey.OP_WRITE, fileName);

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
