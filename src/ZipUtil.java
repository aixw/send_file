import java.io.*;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * zip压缩工具类
 * @author xiongqimeng
 * @version 1.0
 * @date 2019/12/20 14:43
 */
public class ZipUtil {

    private static final Logger log = Logger.getLogger(ZipUtil.class.getName());

    /**
     * 文件压缩，用BufferedOutputStream 字节缓冲区
     * @param outFile
     * @param inputFile
     */
    public static void compress(File outFile, File inputFile){
        try {
            long start = System.currentTimeMillis();
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outFile));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zipOutputStream);
            FileInputStream inputStream = new FileInputStream(inputFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            zipOutputStream.putNextEntry(new ZipEntry(inputFile.getName()));
            int count;
            byte[] buffer = new byte[8192];
            while ((count = bufferedInputStream.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, count);
            }

            bufferedInputStream.close();
            bufferedOutputStream.flush();
            zipOutputStream.close();
            long end = System.currentTimeMillis();
            log.info("压缩文件" + inputFile.getName() + "耗时" + (end - start) + "毫秒");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 解压缩
     * @param inputFile 需加压文件
     * @param outPath 输出目录
     */
    public static void unCompress(File inputFile, String outPath) {
        try {
            long start = System.currentTimeMillis();
            ZipFile zipFile = new ZipFile(inputFile);
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = e.nextElement();
                String fileName = entry.getName();
                File targetFile = new File(outPath + File.separator + fileName);
                if (!targetFile.exists()) {
                    targetFile.createNewFile();
                }
                InputStream is = zipFile.getInputStream(entry);
                FileOutputStream fos = new FileOutputStream(targetFile);
                int len;
                byte[] buf = new byte[8192];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }

                is.close();
                fos.flush();
                fos.close();
            }
            zipFile.close();
            long end = System.currentTimeMillis();
            log.info("解压缩文件" + inputFile.getName() + "完成耗时" + (end - start) + "毫秒");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
