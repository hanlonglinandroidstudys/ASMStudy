package com.dgplugin.asmtest;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM7;

/**
 * author: DragonForest
 * time: 2019/12/26
 */
public class MyMethodVisitor extends MethodVisitor {
    public MyMethodVisitor(MethodVisitor methodVisitor) {
        super(ASM7, methodVisitor);
    }

}
