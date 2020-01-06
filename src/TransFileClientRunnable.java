import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * 客户端传输实现线程类
 * 文件传输 version 0.1
 * @author xiongqiqmeng
 * @since 2019/12/20 16:33
 */
public class TransFileClientRunnable implements Runnable {
    private static final Logger log = Logger.getLogger(TransFileClientRunnable.class.getName());

    /**
     * socket
     */
    private Socket socket;

    /**
     * 文件名称
     */
    private String fileName;

    public TransFileClientRunnable(Socket socket, String fileName) {
        this.socket = socket;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            File file = new File(fileName);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            long startTime = System.currentTimeMillis();

            //对当前文件进行压缩
            File zipFile = new File(file.getName() + ".zip");
            ZipUtil.compress(zipFile, file);
            RandomAccessFile randomAccessFile = new RandomAccessFile(zipFile.getPath(), "r");

            dataOutputStream.writeUTF(zipFile.getName());
            dataOutputStream.flush();

            //将文件长度传输过去，便于服务端判断是否需要断点续传
            log.info("发送文件长度:" + zipFile.length());
            dataOutputStream.writeLong(zipFile.length());
            dataOutputStream.flush();

            //读取已传输size
            long size = dataInputStream.readLong();
            byte[] buffer = new byte[8192];
            int readCount;
            long total = 0;

            //获取服务端传过来文件已传输大小参数size
            if (size < randomAccessFile.length()) {
                if (size > 0) {
                    randomAccessFile.seek(size);
                    log.info("已发送文件长度:" + size + "断点续传开始");
                }

                while ((readCount = randomAccessFile.read(buffer)) >= 0) {
                    total += readCount;
                    dataOutputStream.write(buffer, 0, readCount);
                }
            }

            log.info(fileName + " 此次传输大小： " + total + ", 耗时： " + (System.currentTimeMillis() - startTime) + "毫秒");
            dataOutputStream.flush();
            dataOutputStream.close();
            dataInputStream.close();
            randomAccessFile.close();
            //上传完成之后删除zip文件
            if (zipFile.exists()) {
                zipFile.delete();
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
