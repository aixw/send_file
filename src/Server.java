import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * 服务端启动
 * 文件传输 version 0.1
 * 1、单服务端，支持多客户端同时连接连接（每一个连接独自开启子线程）
 * 2、支持断点续传
 * 3、客户端压缩后传输，服务端解压
 * @author xiongqiqmeng
 * @since 2019/12/20 16:33
 */
public class Server {
    private static final Logger log = Logger.getLogger(Server.class.getName());
    /**
     * 服务端文件存储 path
     */
    public static final String SERVER_DIR = "/data/webroot/";

    /**
     * 服务端端口
     */
    public static final int SERVER_POST = 1111;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(SERVER_POST);

        //根目录不存在，则创建
        File root = new File(SERVER_DIR);
        if (!root.exists()) {
            root.mkdirs();
        }

        int count = 0;
        while (true) {
            Socket socket = serverSocket.accept();
            //每个客户端连接创建新的线程
            new Thread(new TransFileServerRunnable(socket, SERVER_DIR)).start();
            count++;
            log.info("客户端数量：" + count);
        }

    }
}
