import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * 客户端传输实现线程类
 * 文件传输 version 0.1
 * @author xiongqiqmeng
 * @since 2019/12/20 16:33
 */
public class TransFileServerRunnable implements Runnable {
    private static final Logger log = Logger.getLogger(TransFileServerRunnable.class.getName());

    /**
     * socket
     */
    private Socket socket;

    /**
     * 文件根目录
     */
    private String rootDir;

    public TransFileServerRunnable(Socket socket, String rootDir) {
        this.socket = socket;
        this.rootDir = rootDir;
    }

    @Override
    public void run() {
        DataInputStream inputStream;
        DataOutputStream clientOutputStream;
        RandomAccessFile randomAccessFile;
        try {
            log.info("客户端" + socket.getInetAddress() + "已连接，当前线程为" + Thread.currentThread().getName());
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            clientOutputStream = new DataOutputStream(socket.getOutputStream());

            //传输完成的数据长度
            long doneLen = 0;
            //初始文件长度
            long fileLen;

            //读取文件名
            String clientFileName = inputStream.readUTF();
            log.info("客户端传送文件名为" + clientFileName);
            //读取文件长度
            fileLen = inputStream.readLong();
            log.info("读取当前文件长度 fileLen=" + fileLen);

            String fileName = rootDir + File.separatorChar + clientFileName;
            String tempFile = fileName + ".temp";
            File file = new File(tempFile);
            long size = 0;

            //若传输文件存在，则为断点续传，给客户端传递当前文件已传输size
            if (file.exists() && file.isFile()) {
                size = file.length();
                doneLen = size;
            }
            clientOutputStream.writeLong(size);
            clientOutputStream.flush();

            //可写RandomAccessFile,用于断点续传
            randomAccessFile = new RandomAccessFile(tempFile, "rw");
            //移动文件指针
            randomAccessFile.seek(size);
            byte[] byteArray = new byte[8192];
            int readCount;
            while ((readCount = inputStream.read(byteArray)) >= 0) {
                randomAccessFile.write(byteArray, 0, readCount);
                doneLen += readCount;
                log.info(clientFileName + "文件接收了" + (doneLen * 100 / fileLen) + "%");
            }

            //若已接收文件size大于等于实际文件size，则传输成功
            if (doneLen >= fileLen) {
                randomAccessFile.close();
                File reFile = new File(fileName);
                //若之前该文件已上传过，则重命名文件
                if (reFile.exists()) {
                    reFile = new File(rootDir + File.separatorChar + UUID.randomUUID() + "_"  + clientFileName);
                }

                //重命名之后删除临时文件
                if(file.renameTo(reFile)) {
                    log.info("接收完成，文件存为" + reFile);
                    file.delete();
                }

                //对zip文件解压缩
                ZipUtil.unCompress(reFile, rootDir);

                //IO流关闭
                clientOutputStream.close();
                inputStream.close();
                socket.close();
                if (reFile.exists()) {
                    reFile.delete();
                }
            } else {
                log.info(socket.getInetAddress() + "发来的" + fileName + "传输过程中失去连接");
            }
        } catch (SocketException ex) {
            log.info("客户端" + socket.getInetAddress() +"断开连接");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
