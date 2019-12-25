# 字节码插桩学习

## ASM框架的简单实用
* 见app/src/test
* * 完成对指定InjectTest.class 进行插桩

关于Asm框架的使用，详细见 [ASM框架的使用](https://blog.csdn.net/qq_23992393/article/details/103677719)

## ASM结合Transform使用
>> app 待插桩的项目

>> buildSrc
>>> * src/main/groovy插件入口，tranform处理
>>> * src/main/java ASM插桩处理

关于ASM结合Transform的使用，详细见[ASM + Transform 在android中的使用](https://blog.csdn.net/qq_23992393/article/details/103696976)
