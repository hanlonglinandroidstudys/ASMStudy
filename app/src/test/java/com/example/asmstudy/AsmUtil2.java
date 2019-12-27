package com.example.asmstudy;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;
import static org.objectweb.asm.Opcodes.LSUB;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_7;

/**
 * ASM插桩测试类 用来测试
 *
 * author: DragonForest
 * time: 2019/12/23
 */
public class AsmUtil2 {

    public static void main(String arg[]) {
        AsmUtil2 asmUtil = new AsmUtil2();
        asmUtil.inject();
    }

    /**
     * 使用ASM 向InjectTest.class中插入日志代码
     */
    public void inject() {
        String classPathIn = "D:\\projects\\DragonForest\\ASMStudy\\app\\src\\test\\java\\com\\example\\asmstudy\\InjectTest.class";
        String classPathOutDir = "D:\\projects\\DragonForest\\ASMStudy\\app\\src\\test\\java\\com\\example\\asmstudy2";
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(classPathIn);
            fos = new FileOutputStream(classPathOutDir + File.separator + "InjectTest.class");
            /*
                3.获得新的class字节码并写出
             */
            File outDir = new File(classPathOutDir);
            if (!outDir.exists()) {
                outDir.mkdir();
            }
            byte[] newClassBytes = dump();
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


    public static byte[] dump() throws Exception {

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;

        classWriter.visit(V1_7, ACC_PUBLIC | ACC_SUPER, "com/example/asmstudy/InjectTest", null, "java/lang/Object", null);

        classWriter.visitSource("InjectTest.java", null);

        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(7, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "Lcom/example/asmstudy/InjectTest;", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/InterruptedException");
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(9, label3);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            methodVisitor.visitLdcInsn("\u4eca\u665a\u4e0a\u5c71\u6253\u8001\u864e");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(11, label0);
            methodVisitor.visitLdcInsn(new Long(2000L));
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(14, label1);
            Label label4 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label4);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(12, label2);
            methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/InterruptedException"});
            methodVisitor.visitVarInsn(ASTORE, 1);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLineNumber(13, label5);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/InterruptedException", "printStackTrace", "()V", false);
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(16, label4);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitTypeInsn(NEW, "com/example/asmstudy/InjectTest");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/example/asmstudy/InjectTest", "<init>", "()V", false);
            methodVisitor.visitVarInsn(ASTORE, 1);
            Label label6 = new Label();
            methodVisitor.visitLabel(label6);
            methodVisitor.visitLineNumber(17, label6);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/example/asmstudy/InjectTest", "sayGo", "()V", false);
            Label label7 = new Label();
            methodVisitor.visitLabel(label7);
            methodVisitor.visitLineNumber(18, label7);
            methodVisitor.visitInsn(RETURN);
            Label label8 = new Label();
            methodVisitor.visitLabel(label8);
            methodVisitor.visitLocalVariable("e", "Ljava/lang/InterruptedException;", null, label5, label4, 1);
            methodVisitor.visitLocalVariable("arg", "[Ljava/lang/String;", null, label3, label8, 0);
            methodVisitor.visitLocalVariable("injectTest", "Lcom/example/asmstudy/InjectTest;", null, label6, label8, 1);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "sayHello", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(22, label0);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            methodVisitor.visitVarInsn(LSTORE, 1);
            // 去掉打印 大家好
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(23, label1);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            methodVisitor.visitLdcInsn("haha I am be modified");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(24, label2);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            methodVisitor.visitVarInsn(LSTORE, 3);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(25, label3);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            methodVisitor.visitLdcInsn("\u65b9\u6cd5\u8017\u65f6\uff1a");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitVarInsn(LLOAD, 3);
            methodVisitor.visitVarInsn(LLOAD, 1);
            methodVisitor.visitInsn(LSUB);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(26, label4);
            methodVisitor.visitInsn(RETURN);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLocalVariable("this", "Lcom/example/asmstudy/InjectTest;", null, label0, label5, 0);
            methodVisitor.visitLocalVariable("start", "J", null, label1, label5, 1);
            methodVisitor.visitLocalVariable("end", "J", null, label3, label5, 3);
            methodVisitor.visitMaxs(6, 5);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "sayGo", "()V", null, null);
            {
                annotationVisitor0 = methodVisitor.visitAnnotation("Lcom/example/asmstudy/ASMTest;", false);
                annotationVisitor0.visitEnd();
            }
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(30, label0);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            methodVisitor.visitLdcInsn("\u5927\u5bb6\u597daaaaaaaaaaaa");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(31, label1);
            methodVisitor.visitInsn(RETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "Lcom/example/asmstudy/InjectTest;", null, label0, label2, 0);
            methodVisitor.visitMaxs(2, 1);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }

}
