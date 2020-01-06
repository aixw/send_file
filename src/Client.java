import java.net.Socket;

/**
 * 客户端启动
 * 可多线程上传多个文件
 * @author xiongqiqmeng
 * @since 2019/12/20 16:33
 */
public class Client {
    /**
     * 服务端ip
     */
    public static final String SERVER_HOST = "172.16.246.179";
    /**
     * 服务端端口
     */
    public static final int SERVER_POST = 1111;
    /**
     * 上传文件 path
     */
    public static final String FILE_NAME = "order-application.log";

    public static void main(String[] args) throws Exception {
        //每次文件上传单独启动线程
        new Thread(new TransFileClientRunnable(new Socket(SERVER_HOST, SERVER_POST), FILE_NAME)).start();
    }

}
