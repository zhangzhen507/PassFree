package com.zzhen.app.passfree.util;

import java.util.Random;

/**
 * Created by zhangzhen on 2017/6/27.
 */

public class AutoGenCode {

    private static AutoGenCode instance;

    private Random random;
    private String strPool = "0123456789abcdefghijklmnopqrstuvwxyz@#*&";
    private char[] chars;

    public static AutoGenCode getInstance(){
        if(instance == null){
            synchronized (AutoGenCode.class){
                instance = new AutoGenCode();
            }
        }
        return instance;
    }

    private AutoGenCode(){
        random = new Random();
        chars = new char[60];
        chars = strPool.toCharArray();
    }

    public String genCode(int length){

        char[] data = new char[length];

        for(int i = 0; i < length; i++){
            int index = random.nextInt(chars.length);
            data[i] = chars[index];
        }
        String str = new String(data);
        return str;
    }
}
