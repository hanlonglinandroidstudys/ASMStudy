package com.dgplugin.activity_record;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM7;


/**
 * author: DragonForest
 * time: 2019/12/27
 */
public class ActivityClassVisitor extends ClassVisitor {
    public ActivityClassVisitor(ClassVisitor classVisitor) {
        super(ASM7, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        ActivityMethodVisitor activityMethodVisitor = new ActivityMethodVisitor(api, methodVisitor, access, name, descriptor);
        return activityMethodVisitor;
    }
}
