package com.zjfgh.bluedhook.simple;

import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileToJsonConverter {

    public JSONObject convertFilesToJson() {
        // 获取下载目录下的"小蓝抽奖记录"文件夹
        File baseDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "小蓝抽奖记录"
        );

        JSONObject resultJson = new JSONObject();

        if (baseDir.exists() && baseDir.isDirectory()) {
            try {
                // 遍历第一层目录（日期文件夹）
                File[] dateDirs = baseDir.listFiles();
                if (dateDirs != null) {
                    for (File dateDir : dateDirs) {
                        if (dateDir.isDirectory()) {
                            String date = dateDir.getName();
                            JSONObject dateJson = new JSONObject();

                            // 遍历第二层目录（txt文件）
                            File[] txtFiles = dateDir.listFiles();
                            if (txtFiles != null) {
                                for (File txtFile : txtFiles) {
                                    if (txtFile.isFile() && txtFile.getName().endsWith(".txt")) {
                                        String fileName = txtFile.getName();
                                        JSONArray fileContentArray = readFileContentAsArray(txtFile);

                                        // 将文件名和内容数组添加到日期JSON对象中
                                        try {
                                            // 尝试将每一行解析为JSON（如果内容是JSON格式）
                                            JSONArray parsedArray = new JSONArray();
                                            for (int i = 0; i < fileContentArray.length(); i++) {
                                                String line = fileContentArray.getString(i);
                                                try {
                                                    parsedArray.put(new JSONObject(line));
                                                } catch (Exception e) {
                                                    parsedArray.put(line);
                                                }
                                            }
                                            dateJson.put(fileName, parsedArray);
                                        } catch (Exception e) {
                                            // 如果解析失败，直接存储原始行数组
                                            dateJson.put(fileName, fileContentArray);
                                        }
                                    }
                                }
                            }

                            // 将日期文件夹添加到结果JSON中
                            resultJson.put(date, dateJson);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultJson;
    }

    private JSONArray readFileContentAsArray(File file) throws IOException {
        JSONArray contentArray = new JSONArray();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                // 跳过空行
                if (!line.trim().isEmpty()) {
                    contentArray.put(line);
                }
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return contentArray;
    }
}