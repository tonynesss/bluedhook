package com.zjfgh.bluedhook.simple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ChatHelperV4 {
    public static boolean a(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.skip(fileInputStream.available() - 1);
            int[] iArr = {fileInputStream.read(), fileInputStream.read(), fileInputStream.read(), fileInputStream.read(), fileInputStream.read()};
            fileInputStream.close();
            if (iArr[0] == 71 && iArr[1] == 73 && iArr[2] == 70 && iArr[3] == 56) {
                return iArr[4] == 59;
            }
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e2) {
            e2.printStackTrace();
            return false;
        } catch (Exception e3) {
            e3.printStackTrace();
            return false;
        }
    }

}
