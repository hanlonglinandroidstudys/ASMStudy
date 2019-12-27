package com.dgplugin.asmtest;

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
        System.out.println("ClassAdapterVisitor#visitMethod()-->name：" + name + ",signature：" + signature);
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodAdapterVisitor(api, methodVisitor, access, name, descriptor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        System.out.println("ClassAdapterVisitor#visit()-->name：" + name + ",signature：" + signature);
    }
}
