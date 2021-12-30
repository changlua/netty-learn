package com.changlu.No1Nio基础组件入门学习.PathsAndFiles;


import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @ClassName PathTest
 * @Author ChangLu
 * @Date 2021/12/17 20:30
 * @Description Path类使用
 */
public class PathsTest {
    public static void main(String[] args) {
//        test01();
        test02();
    }

    //调用normalize()可对路径进行转义：..表示上级
    public static void test02(){
        Path path = Paths.get("d:\\data\\projects\\a\\..\\b");
        System.out.println(path);
        System.out.println(path.normalize());
    }

    //基本的Path.get()格式
    public static void test01(){
        // 相对路径 使用 user.dir 环境变量来定位 1.txt
        Path path1 = Paths.get("1.txt");
        System.out.println(path1);//1.txt

        // 绝对路径 代表了  d:\1.txt
        Path path2 = Paths.get("E:\\资源\\电影资源\\1.txt");
        System.out.println(path2);//E:\资源\电影资源\1.txt

        // 绝对路径 同样代表了  d:\1.txt
        Path path3 = Paths.get("d:/1.txt");
        System.out.println(path3);

        // 代表了  d:\data\projects
        Path path4 = Paths.get("d:\\data", "changlu.txt");
        System.out.println(path4);
    }

}