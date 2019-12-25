package com.dgplugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识注解
 * 带有次注解的方法执行ASM插桩
 * <p>
 * author: DragonForest
 * time: 2019/12/23
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface ASMTest {
}
