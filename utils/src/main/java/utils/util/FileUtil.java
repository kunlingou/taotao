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
        FiletoHexStringTxtFile("D:\\engine_demo_table1.ibd","D:\\engine_demo_table1.txt");
    }
    
    /**
     * 将文件以16进制的形式存储在txt中
     * @throws IOException 
     */
    public static void FiletoHexStringTxtFile(String src, String tar) throws IOException {
        String bytesToHexString = StringUtil.bytesToHexString(fileToBytes(src));
        BytesToFile(bytesToHexString.getBytes(), tar);
    }

    public static byte[] fileToBytes(String fileName) throws IOException {
        return fileToBytes(new File(fileName));
    }

    public static byte[] fileToBytes(File file) throws IOException {
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
