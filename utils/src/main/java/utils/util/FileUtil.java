package utils.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

    public static void main(String[] args) throws IOException {
        FiletoHexStringTxtFile("D:\\engine_demo_table0.ibd","D:\\engine_demo_table0.txt",16);
        FiletoHexStringTxtFile("D:\\engine_demo_table1.ibd","D:\\engine_demo_table1.txt",16);
    }
    
    /**
     * 将文件以16进制的形式存储在txt中
     * @param src
     * @param tar
     * @param newLine 换行每行字符长度，小于1时不换行
     * @throws IOException
     */
    public static void FiletoHexStringTxtFile(String src, String tar,int newLine) throws IOException {
        String bytesToHexString = StringUtil.bytesToHexString(fileToBytes(src));
        StringBuffer sb = new StringBuffer();
        if(newLine>0) {
            for(int i=0,len=bytesToHexString.length();i<len;i+=newLine) {
                sb.append(bytesToHexString.substring(i, len<i+newLine?len:i+newLine));
                sb.append("\r\n");
            }
        }
        BytesToFile(sb.toString().getBytes(), tar);
    }

    public static byte[] fileToBytes(String fileName) throws IOException {
        return fileToBytes(new File(fileName));
    }

    public static byte fileToBytes(File file)[] throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);){
            flow(in, out);
            return out.toByteArray();
        }
    }

    public static void BytesToFile(byte[] bytes, String fileName) throws IOException {
        try (FileOutputStream out = new FileOutputStream(fileName);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);){
            flow(in, out);
        }
    }
    
    public static void flow(InputStream in, OutputStream out) throws IOException {
        byte[] temp = new byte[1024];
        int size = 0;
        while ((size = in.read(temp)) != -1) {
            out.write(temp, 0, size);
        }
        out.flush();
    }
}
