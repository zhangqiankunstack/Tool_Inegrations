package com.rengu.toolintegrations.websocket;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName Demo
 * @Description TODO
 * @Author yyc
 * @Date 2020/8/26 19:26
 * @Version 1.0
 */
public class Demo {

    public static void main(String[] args) {
        String[] strings=new String[]{"1","2","3","4"};

        List list= Arrays.asList(strings);
        for (int i=0;i<list.size();i++){
            System.out.println(strings[i]);
        }
    }
}
