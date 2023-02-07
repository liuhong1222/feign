package cn.feignclient.credit_feign_web.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @description: 文件操作工具类
 * @author: rivers
 * @since: 2020-03-06 10:51
 */
public class FileUtil {

	private final static Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 快速获取文件行数
     *
     * @param filePath
     * @return
     */
    public static int getFileLineNum(String filePath) {
        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filePath))) {
            lineNumberReader.skip(Long.MAX_VALUE);
            int lineNumber = lineNumberReader.getLineNumber();
            return lineNumber;//实际上是读取换行符数量 , 因为最后一行没有换行符所以需要+1,(这里读取的文件最后一行都是空白行，所以不需要+1)
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * 读取文件最后几行 <br>
     * 相当于Linux系统中的tail命令 读取大小限制是2GB
     *
     * @param filename 文件名
     * @param charset  文件编码格式,传null默认使用defaultCharset
     * @param rows     读取行数
     * @throws IOException
     */
    public static String readLastRows(String filename, Charset charset, int rows) throws IOException {
        charset = charset == null ? Charset.defaultCharset() : charset;
        String lineSeparator = System.getProperty("line.separator");
        try (RandomAccessFile rf = new RandomAccessFile(filename, "r")) {
            // 每次读取的字节数要和系统换行符大小一致
            byte[] c = new byte[lineSeparator.getBytes().length];
            // 在获取到指定行数和读完文档之前,从文档末尾向前移动指针,遍历文档每一个字节
            for (long pointer = rf.length(), lineSeparatorNum = 0; pointer >= 0 && lineSeparatorNum < rows; ) {
                // 移动指针
                rf.seek(pointer--);
                // 读取数据
                int readLength = rf.read(c);
                if (readLength != -1 && new String(c, 0, readLength).equals(lineSeparator)) {
                    lineSeparatorNum++;
                }
                //扫描完依然没有找到足够的行数,将指针归0
                if (pointer == -1 && lineSeparatorNum < rows) {
                    rf.seek(0);
                }
            }
            byte[] tempbytes = new byte[(int) (rf.length() - rf.getFilePointer())];
            rf.readFully(tempbytes);
            return new String(tempbytes, charset);
        }
    }

    public static boolean saveFileFromInputStream(InputStream inputStream, String saveFilePath) {
        File f = new File(saveFilePath);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                log.error("创建文件异常，文件路径：{}, {}", saveFilePath, e.getMessage());
                return false;
            }
        }
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            while ((index = inputStream.read(bytes)) != -1) {
                log.info(new String(bytes));
                out.write(new String(bytes).getBytes(StandardCharsets.UTF_8), 0, index);
                out.flush();
            }
            inputStream.close();
            out.close();
            return true;
        } catch (Exception e) {
            log.error("写入文件异常，文件路径：{}，{}", saveFilePath, e.getMessage());
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("关闭文件流异常，文件路径：{}, {}", saveFilePath, e.getMessage());
                }
            }
        }
    }
    
    /**
     * 保存数据到文本
     *
     * @param lines       数据
     * @param filePath    文件路径
     * @param charsetName 文件编码
     * @param append      是否追加
     * @throws IOException
     */
    public static void saveTxt(Set<String> lines, String filePath, String charsetName, boolean append) throws IOException {
        try (BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath), append), charsetName))) {
            for (String line : lines) {
                output.append(line).append("\r\n");
            }
        }
    }

    /**
     * 获取文件夹大小
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSize(File f) throws Exception {
        long size = 0;
        if (f.isDirectory()) {
            File[] flist = f.listFiles();
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileSize(flist[i]);
                } else {
                    size = size + flist[i].length();
                }
            }
        } else {
            size = size + f.length();
        }

        return size;
    }

    /**
     * 迭代删除文件夹
     *
     * @param dirPath 文件夹路径
     */
    public static void deleteDir(String dirPath) {
        File file = new File(dirPath);
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            if (files == null) {
                file.delete();
            } else {
                for (int i = 0; i < files.length; i++) {
                    deleteDir(files[i].getAbsolutePath());
                }
                file.delete();
            }
        }
    }

    /**
     * 根据一个文件名，读取完文件，干掉bom头。
     *
     * @param fileName
     * @throws IOException
     */
    public static void trimBom(String fileName) throws IOException {

        FileInputStream fin = new FileInputStream(fileName);
        // 开始写临时文件
        InputStream in = getInputStream(fin);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[4096];

        int len = 0;
        while (in.available() > 0) {
            len = in.read(b, 0, 4096);
            //out.write(b, 0, len);
            bos.write(b, 0, len);
        }

        in.close();
        fin.close();
        bos.close();

        //临时文件写完，开始将临时文件写回本文件。
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(bos.toByteArray());
        out.close();
    }

    /**
     * 读取流中前面的字符，看是否有bom，如果有bom，将bom头先读掉丢弃
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static InputStream getInputStream(InputStream in) throws IOException {

        PushbackInputStream testin = new PushbackInputStream(in);
        int ch = testin.read();
        if (ch != 0xEF) {
            testin.unread(ch);
        } else if ((ch = testin.read()) != 0xBB) {
            testin.unread(ch);
            testin.unread(0xef);
        } else if ((ch = testin.read()) != 0xBF) {
            throw new IOException("错误的UTF-8格式文件");
        } else {
            // 不需要做，这里是bom头被读完了
            // System.out.println("still exist bom");
        }
        return testin;

    }


}
