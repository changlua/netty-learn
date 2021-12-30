package com.changlu.No1Nio基础组件入门学习.PathsAndFiles;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName FilesTest
 * @Author ChangLu
 * @Date 2021/12/17 20:41
 * @Description TODO
 */
public class FilesTest {

    public static void main(String[] args) throws IOException {
        //01、存在、创建单级多级目录测试
//        test01();
        //02、拷贝测试
//        testCopy(false);  //若是已存在就会报已存在异常
//        testCopy(true);  //强制覆盖，无报错
        //03、删除目录
//        testDelDir();
        //04、删除文件
//        testDelFile();
        //单独功能案例点
        //1、遍历目录文件
//        testViewAllFiles();
        //2、统计jdk8目录下jar包的个数
//        testJarFileCount();//705
        //3、删除多级目录
//        testFileMultDirs();
        //4、拷贝文件夹到指定目录
        testCopyMultDir();
    }

    /**
     * 单独功能点4、拷贝文件夹到指定目录
     */
    private static void testCopyMultDir() throws IOException {
        String COPY_ORIGIN = "C:\\Users\\93997\\Desktop\\工作室网站";
        //复制到的文件目录为：指定路径\\工作室网站1
        String COPY_TARGET = "C:\\Users\\93997\\Desktop\\新建文件夹\\工作室网站1";
        //递归返回所有的文件Path集合stream流
        Files.walk(Paths.get(COPY_ORIGIN)).forEach(path->{
            final String copyFileName = path.toFile().getAbsolutePath().replace(COPY_ORIGIN, COPY_TARGET);
            System.out.println(copyFileName);
            //若是文件先创建文件
            try {
                if (Files.isDirectory(path)){
                    Files.createDirectory(Paths.get(copyFileName));
                } else {
                    Files.copy(path, Paths.get(copyFileName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 单独功能点3：删除多级目录（不走回收站，谨慎删除）
     */
    private static void testFileMultDirs() throws IOException {
        final Path path = Paths.get("C:\\Users\\93997\\Desktop\\新建文件夹\\工作室网站");
        //注意点：删除多级目录时，首先需要先将某个文件夹中的文件内容删除，之后才可以删除文件目录
        Files.walkFileTree(path,new SimpleFileVisitor<Path>() {

            //1、先删除文件
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            //2、后删除目录（后置文件访问器）
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    /**
     * 单独功能点2：统计JDK8目录中jar包的个数
     */
    private static void testJarFileCount() throws IOException {
        final Path path = Paths.get("C:\\Program Files\\Java\\jdk1.8.0_201");
        AtomicInteger jarCount = new AtomicInteger();
        //递归遍历文件夹
        Files.walkFileTree(path,new SimpleFileVisitor<Path>() {
            //访问当前文件
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                //注意：file.getFileName()返回值是Path
                if (file.toFile().getName().endsWith(".jar")){
                    System.out.println(file.getFileName());
                    jarCount.getAndIncrement();
                }
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("jar包数量：" + jarCount);
    }


    /**
     * 单独功能点1：遍历目录文件，打印出文件夹数量、文件数。
     *      说明：这里将root文件也算入文件夹的数量中，所以内部文件夹数量应该-1
     */
    private static void testViewAllFiles() throws IOException {
        final Path path = Paths.get("C:\\Program Files\\Java\\jdk1.8.0_201");
        //由于匿名实现类中重写方法不能直接对局部变量进行改变值操作所以使用AtomicInteger
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        //递归遍历文件夹
        Files.walkFileTree(path,new SimpleFileVisitor<Path>() {
            //进入目录前
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("进入文件目录=》" + dir);
                dirCount.getAndIncrement();
                return super.preVisitDirectory(dir, attrs);
            }

            //访问当前文件
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("           =》" + file);
                fileCount.getAndIncrement();
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("文件夹数量：" + dirCount);
        System.out.println("文件数量：" + fileCount);
    }


    /**
     * 04、测试删除文件or目录（目录为空）
     */
    private static void testDelFile() throws IOException {
        Files.delete(Paths.get("C:\\Users\\93997\\Desktop\\新建文件夹\\test1\\test2"));
    }


    /**
     * 03、测试删除目录or目录（目录为空）
     * @throws IOException 若是目录内有文件会抛出异常
     */
    private static void testDelDir() throws IOException {
        Files.delete(Paths.get("C:\\Users\\93997\\Desktop\\新建文件夹\\test1"));
    }


    /**
     * 02、测试拷贝功能
     * @param replace false:不覆盖，若是已存在会报异常；true：直接覆盖复制
     * @throws IOException
     */
    private static void testCopy(boolean replace) throws IOException {
        if (replace) {
            //额外添加覆盖参数
            Files.copy(
                    Paths.get("C:\\Users\\93997\\Desktop\\nettyDemo\\data.txt"),
                    Paths.get("C:\\Users\\93997\\Desktop\\新建文件夹\\data.txt"),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }else {
            Files.copy(
                    Paths.get("C:\\Users\\93997\\Desktop\\nettyDemo\\data.txt"),
                    Paths.get("C:\\Users\\93997\\Desktop\\新建文件夹\\data.txt")
            );
        }

    }

    /**
     * 01、基本功能测试：文件是否存在、
     */
    private static void test01() throws IOException {
        //测试是否存在
        System.out.println(Files.exists(Paths.get("d:\\1.txt")));
        //测试创建目录
        Files.createDirectory(Paths.get("C:\\Users\\93997\\Desktop\\新建文件夹\\test"));
        //创建多级目录
        Files.createDirectories(Paths.get("C:\\Users\\93997\\Desktop\\新建文件夹\\test1\\test2"));
    }


}