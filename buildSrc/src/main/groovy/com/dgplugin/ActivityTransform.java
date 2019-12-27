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
import com.dgplugin.activity_record.HookActivityUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * author: DragonForest
 * time: 2019/12/24
 */
public class ActivityTransform extends Transform {
    Project project;

    public ActivityTransform(Project project) {
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
                transformJar(jarInput.getFile(), dstFile);
                System.out.println("transform---jar目录:--->>:" + jarPath);
            }
        }
    }

    @Override
    public String getName() {
        return ActivityTransform.class.getSimpleName();
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

    // 普通类不做处理
    private void transformDir(File inputDir, File dstDir) {
        try {
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
                    FileUtils.copyFile(file, dstFile);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 转化jar
     * 步骤：
     *      1.获取jar中class集合
     *      2.遍历class,找到FragmentActivity.class, 进行插桩，拿到插桩后的class输入流写入临时jar，对于其他class,直接拷贝到临时jar
     *      3.将临时jar拷贝到目的目录
     *      4.删除临时jar
     * @param inputJarFile
     * @param dstFile
     */
    private void transformJar(File inputJarFile, File dstFile) {
        try {
//            FileUtils.copyFile(inputJarFile,dstFile);
            JarFile jarFile = new JarFile(inputJarFile);
            File tempJarFile = new File(inputJarFile.getParent() + File.separator + "classes_tmp.jar");
            if(tempJarFile.exists()){
                FileUtils.forceDelete(tempJarFile);
            }
            JarOutputStream jarOutputStream=new JarOutputStream(new FileOutputStream(tempJarFile));
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            // 遍历jar包中的.class文件
            while(jarEntries.hasMoreElements()){
                JarEntry jarEntry = jarEntries.nextElement();
                String jarEntryName = jarEntry.getName();
                System.out.println("ActivityTransform --- jarEntry-->"+jarEntryName);
                ZipEntry zipEntry=new ZipEntry(jarEntryName);
                InputStream jarEntryInputStream = jarFile.getInputStream(jarEntry);
                if(jarEntryName.equals("android/support/v4/app/FragmentActivity.class")
                ||jarEntryName.equals("androidx/fragment/app/FragmentActivity.class")){
                    // 进行插桩
                    // 修改原有class 重新写入
                    System.out.println("find--->FragmentActivity, modifying...");
                    jarOutputStream.putNextEntry(zipEntry);
                    jarOutputStream.write(HookActivityUtil.getActivityByte(jarEntryInputStream));
                }else{
                    // 不进行操作 原封写入
                    jarOutputStream.putNextEntry(zipEntry);
                    jarOutputStream.write(IOUtils.toByteArray(jarEntryInputStream));
                }
                jarOutputStream.closeEntry();
            }
            jarOutputStream.close();
            jarFile.close();
            FileUtils.copyFile(tempJarFile,dstFile);
            FileUtils.forceDelete(tempJarFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
