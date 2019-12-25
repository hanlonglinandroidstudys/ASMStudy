package com.dgplugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author: DragonForest
 * time: 2019/12/23
 */
public class AsmUtil {

    public static void main(String arg[]) {
//        AsmUtil asmUtil = new AsmUtil();
//        asmUtil.inject();
    }

    /**
     * 使用ASM 向class中的方法插入记录代码
     */
    public static void inject(File srcFile,File dstFile) {

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            /*
                1. 准备待插桩的class
             */
            fis = new FileInputStream(srcFile);
            /*
                2. 执行分析与插桩
             */
            // 字节码的读取与分析引擎
            ClassReader cr = new ClassReader(fis);
            // 字节码写出器，COMPUTE_FRAMES 自动计算所有的内容，后续操作更简单
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            // 分析，处理结果写入cw EXPAND_FRAMES:栈图以扩展形式进行访问
            cr.accept(new ClassAdapterVisitor(cw), ClassReader.EXPAND_FRAMES);

            /*
                3.获得新的class字节码并写出
             */
            byte[] newClassBytes = cw.toByteArray();
            fos = new FileOutputStream(dstFile);
            fos.write(newClassBytes);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("执行字节码插桩失败！" + e.getMessage());
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
