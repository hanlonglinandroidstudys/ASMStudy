package com.dgplugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * author: DragonForest
 * time: 2019/12/24
 */
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
