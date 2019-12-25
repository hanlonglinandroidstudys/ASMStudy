package com.example.asmstudy;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ASM插桩测试类 用来测试
 *
 * author: DragonForest
 * time: 2019/12/23
 */
public class AsmUtil {

    public static void main(String arg[]) {
        AsmUtil asmUtil = new AsmUtil();
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
            /*
                1. 准备待插桩的class
             */
            fis = new FileInputStream(classPathIn);
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
            File outDir = new File(classPathOutDir);
            if (!outDir.exists()) {
                outDir.mkdir();
            }
            byte[] newClassBytes = cw.toByteArray();
            fos = new FileOutputStream(outDir.getAbsolutePath() + File.separator + "InjectTest.class");
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


    public class ClassAdapterVisitor extends ClassVisitor {


        public ClassAdapterVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM7, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            System.out.println("方法名：" + name + ",签名：" + signature);
            MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            return new MethodAdapterVisitor(api, methodVisitor, access, name, descriptor);
        }
    }

    /**
     * AdviceAdapter 是 asm-commons 里的类
     * 对MethodVisitor进行了扩展，能让我们更轻松的进行分析
     */
    public class MethodAdapterVisitor extends AdviceAdapter {
        private int start;
        private int end;
        private boolean inject;

        /**
         * Constructs a new {@link AdviceAdapter}.
         *
         * @param api           the ASM API version implemented by this visitor. Must be one of {@link
         *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
         * @param methodVisitor the method visitor to which this adapter delegates calls.
         * @param access        the method's access flags (see {@link Opcodes}).
         * @param name          the method's name.
         * @param descriptor    the method's descriptor (see {@link Type Type}).
         */
        protected MethodAdapterVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            System.out.println("visitAnnotation, descriptor" + descriptor);
            if (Type.getDescriptor(ASMTest.class).equals(descriptor)) {
                inject = true;
            }
            return super.visitAnnotation(descriptor, visible);
        }

        /**
         * 整个方法最开始的时候的回调
         * 我们要在这里插入的逻辑就是 start=System.currentTimeMillis()
         * <p>
         * 使用ASMByteCodeViwer查看 上述代码的字节码：
         * LINENUMBER 19 L0
         * INVOKESTATIC java/lang/System.currentTimeMillis ()J
         * LSTORE 1
         */
        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
            System.out.println("onMethodEnter");
            if (inject) {
                invokeStatic(Type.getType("Ljava/lang/System;"),
                        new Method("currentTimeMillis", "()J"));
                // 创建本地local变量
                start = newLocal(Type.LONG_TYPE);
                // 方法执行的结果保存给创建的本地变量
                storeLocal(start);
            }
        }

        /**
         * 方法结束时的回调
         * 我们要在这里插入
         * long end = System.currentTimeMillis();
         * System.out.println("方法耗时："+(end-start));
         * <p>
         * 使用ASMByteCodeViwer查看上述字节码：
         * L2
         * LINENUMBER 21 L2
         * INVOKESTATIC java/lang/System.currentTimeMillis ()J
         * LSTORE 3
         * L3
         * LINENUMBER 22 L3
         * GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
         * NEW java/lang/StringBuilder
         * DUP
         * INVOKESPECIAL java/lang/StringBuilder.<init> ()V
         * LDC "\u65b9\u6cd5\u8017\u65f6\uff1a"
         * INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
         * LLOAD 3
         * LLOAD 1
         * LSUB
         * INVOKEVIRTUAL java/lang/StringBuilder.append (J)Ljava/lang/StringBuilder;
         * INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
         * INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
         *
         * @param opcode
         */
        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
            System.out.println("onMethodOuter");
            if (inject) {
                invokeStatic(Type.getType("Ljava/lang/System;"),
                        new Method("currentTimeMillis", "()J"));
                // 创建本地local变量
                end = newLocal(Type.LONG_TYPE);
                // 方法执行的结果保存给创建的本地变量
                storeLocal(end);

                getStatic(Type.getType("Ljava/lang/System;"), "out",
                        Type.getType("Ljava/io/PrintStream;"));
                // 分配内存
                newInstance(Type.getType("Ljava/lang/StringBuilder;"));
                dup();
                invokeConstructor(Type.getType("Ljava/lang/StringBuilder;"),
                        new Method("<init>", "()V"));
                visitLdcInsn("方法耗时：");
                invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"),
                        new Method("append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));

                // 减法
                loadLocal(end);
                loadLocal(start);
                math(SUB, Type.LONG_TYPE);

                invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"), new Method("append", "(J)Ljava/lang/StringBuilder;"));
                invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"), new Method("toString", "()Ljava/lang/String;"));
                invokeVirtual(Type.getType("Ljava/io/PrintStream;"), new Method("println", "(Ljava/lang/String;)V"));
            }
        }
    }
}
