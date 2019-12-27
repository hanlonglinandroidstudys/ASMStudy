package com.dgplugin;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionsSchema;

/**
 * author: DragonForest
 * time: 2019/12/24
 */
public class AsmPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        System.out.println("===================");
        System.out.println("I am com.dgplugin.AsmPlugin");
        System.out.println("===================");

        // 注册transform
        registerTransform(project);
    }

    private void registerTransform(Project project) {
        AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);
        appExtension.registerTransform(new AsmTransform(project));
        appExtension.registerTransform(new ActivityTransform(project));
    }
}
