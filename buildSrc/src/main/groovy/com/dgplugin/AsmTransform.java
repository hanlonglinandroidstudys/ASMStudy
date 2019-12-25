package com.dgplugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * author: DragonForest
 * time: 2019/12/24
 */
public class AsmTransform extends Transform {
    Project project;

    public AsmTransform(Project project) {
        this.project = project;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        // 消费型输入，可以从中获取jar包和class包的文件夹路径，需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        // 引用型输入，无需输出
        Collection<TransformInput> referencedInputs = transformInvocation.getReferencedInputs();
        // 管理输出路径，如果消费型输入为空，你会发现OutputProvider==null
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        // 当前是否是增量编译
        boolean incremental = transformInvocation.isIncremental();

        /*
            进行读取class和jar, 并做处理
         */
        for (TransformInput input : inputs) {
            // 处理class
            Collection<DirectoryInput> directoryInputs = input.getDirectoryInputs();
            for (DirectoryInput directoryInput : directoryInputs) {
                // 目标file
                File dstFile = outputProvider.getContentLocation(
                        directoryInput.getName(),
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY);
                // 执行转化整个目录
                transformDir(directoryInput.getFile(), dstFile);
                System.out.println("transform---class目录:--->>:" + directoryInput.getFile().getAbsolutePath());
                System.out.println("transform---dst目录:--->>:" + dstFile.getAbsolutePath());
            }
            // 处理jar
            Collection<JarInput> jarInputs = input.getJarInputs();
            for (JarInput jarInput : jarInputs) {
                String jarPath = jarInput.getFile().getAbsolutePath();
                File dstFile = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
                transformJar(jarInput.getFile(),dstFile);
                System.out.println("transform---jar目录:--->>:" + jarPath);
            }
        }
    }

    @Override
    public String getName() {
        return AsmTransform.class.getSimpleName();
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return true;
    }

    private void transformDir(File inputDir, File dstDir) {
        try {
            if (dstDir.exists()) {
                FileUtils.forceDelete(dstDir);
            }
            FileUtils.forceMkdir(dstDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String inputDirPath = inputDir.getAbsolutePath();
        String dstDirPath = dstDir.getAbsolutePath();
        File[] files = inputDir.listFiles();
        for (File file : files) {
            System.out.println("transformDir-->" + file.getAbsolutePath());
            String dstFilePath = file.getAbsolutePath();
            dstFilePath = dstFilePath.replace(inputDirPath, dstDirPath);
            File dstFile = new File(dstFilePath);
            if (file.isDirectory()) {
                System.out.println("isDirectory-->" + file.getAbsolutePath());
                // 递归
                transformDir(file, dstFile);
            } else if (file.isFile()) {
                System.out.println("isFile-->" + file.getAbsolutePath());
                // 转化单个class文件
                transformSingleFile(file, dstFile);
            }
        }
    }

    /**
     * 转化jar
     * 对jar暂不做处理，所以直接拷贝
     * @param inputJarFile
     * @param dstFile
     */
    private void transformJar(File inputJarFile, File dstFile) {
        try {
            FileUtils.copyFile(inputJarFile,dstFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 转化class文件
     * 注意：
     *      这里只对InjectTest.class进行插桩，但是对于其他class要原封不动的拷贝过去，不然结果中就会缺少class
     * @param inputFile
     * @param dstFile
     */
    private void transformSingleFile(File inputFile, File dstFile) {
        System.out.println("transformSingleFile-->" + inputFile.getAbsolutePath());
        if (!inputFile.getAbsolutePath().contains("InjectTest")) {
            try {
                FileUtils.copyFile(inputFile,dstFile,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        AsmUtil.inject(inputFile, dstFile);
    }
}
