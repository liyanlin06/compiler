package com.demo;

import java.io.*;

public class FileReader {
    // 这部分为文件读取阶段，我们读取相应的文件，以获得相应的处理序列
    public char[] file_text(String file_path) throws IOException {
        File file = new File(file_path);
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String content = "";
        String textLine = "";
        while ((textLine = bufferedReader.readLine()) != null) {
            content += textLine;
            content += '\n';
        }
        char chs[] = content.toCharArray();
        return chs;
    }
}
