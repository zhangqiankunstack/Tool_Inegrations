package com.rengu.toolintegrations.Task;

import java.io.File;
import java.util.ArrayList;

/**
 * Author: Zhangqiankun
 * Date: 2020/3/18 14:58
 */
public class Demo {
    public void main(String[] args) {
        String path="C:\\Users\\XY\\Desktop\\nginx-1.12.2";
        ArrayList<String> fileNameList = new ArrayList<>();
        getAllFileName(path,fileNameList);

    }

    public static void getAllFileName(String path, ArrayList<String> fileNameList) {
        //ArrayList<String> files = new ArrayList<String>();
        boolean flag = false;
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
              System.out.println("文     件：" + tempList[i]);
                //fileNameList.add(tempList[i].toString());
                fileNameList.add(tempList[i].getName());
            }
            if (tempList[i].isDirectory()) {
              System.out.println("文件夹：" + tempList[i]);
                getAllFileName(tempList[i].getAbsolutePath(), fileNameList);
            }
        }
        return;
    }
}
