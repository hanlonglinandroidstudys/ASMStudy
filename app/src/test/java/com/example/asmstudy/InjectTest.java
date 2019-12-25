package com.example.asmstudy;

/**
 * author: DragonForest
 * time: 2019/12/23
 */
public class InjectTest {
    public static void main(String arg[]) {
        System.out.println("今晚上山打老虎");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        InjectTest injectTest = new InjectTest();
        injectTest.sayGo();
    }


    public void sayHello() {
        long start = System.currentTimeMillis();
        System.out.println("大家好");
        long end = System.currentTimeMillis();
        System.out.println("方法耗时：" + (end - start));
    }

    @ASMTest
    public void sayGo() {
        System.out.println("大家好aaaaaaaaaaaa");
    }
}
