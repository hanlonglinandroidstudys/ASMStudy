package com.example.asmstudy;

import android.content.Intent;
import android.util.Log;

/**
 * author: DragonForest
 * time: 2019/12/24
 */
public class InjectTest {
    public void sayHello(){
        Log.e("InjectTest","你好啊 啊啊啊啊");
        System.out.println("你好");
        Intent i=new Intent();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
