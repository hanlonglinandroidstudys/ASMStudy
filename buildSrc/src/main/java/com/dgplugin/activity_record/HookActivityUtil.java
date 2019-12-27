package com.dgplugin.activity_record;

import com.dgplugin.asmtest.ClassAdapterVisitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.InputStream;

/**
 * 完成在Activity中onCreate() 和 onDestory（）中插入埋点代码
 * <p>
 * author: DragonForest
 * time: 2019/12/27
 */
public class HookActivityUtil {

    public static byte[] getActivityByte(InputStream inputStream) {

        try {
            /*
                2. 执行分析与插桩
             */
            // 字节码的读取与分析引擎
            ClassReader cr = new ClassReader(inputStream);
            // 字节码写出器，COMPUTE_FRAMES 自动计算所有的内容，后续操作更简单
            ClassWriter cw = new ClassWriter(cr,ClassWriter.COMPUTE_MAXS);
            // 分析，处理结果写入cw EXPAND_FRAMES:栈图以扩展形式进行访问
            cr.accept(new ActivityClassVisitor(cw), ClassReader.EXPAND_FRAMES);
            /*
                3.获得新的class字节码并写出
             */
            return cw.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("执行字节码插桩失败！" + e.getMessage());
        }
        return null;
    }

}
