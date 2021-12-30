package com.changlu.No1Nio基础组件入门学习.FileChannel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @ClassName TestTransferTo
 * @Author ChangLu
 * @Date 2021/12/17 19:26
 * @Description 测试FileChannel的transferTo方法：底层为零拷贝
 */
public class TestTransferTo {
    public static void main(String[] args) {
        String FILE_FROM = "C:\\Users\\93997\\Desktop\\新建文件夹\\这个杀手不太冷1994.mp4";
        String FILE_TO = "C:\\Users\\93997\\Desktop\\新建文件夹\\新建文件夹\\这个杀手不太冷1994.mp4";
        final long begin = System.currentTimeMillis();
        System.out.println("复制中...");
        //传输<=2G
//        transferTo_2G(FILE_FROM, FILE_TO);
        //>2G
        transferTo_t2G(FILE_FROM, FILE_TO);
        System.out.println("耗时：" + (System.currentTimeMillis() - begin) / 1000 + "s");
    }

    /**
     * 传输超过<=2G容量大小，默认transferTo传输一次上限为2G容量
     */
    public static void transferTo_2G(String FILE_FROM, String FILE_TO){
        try (
            FileChannel from = new FileInputStream(FILE_FROM).getChannel();
            FileChannel to = new FileOutputStream(FILE_TO).getChannel();
        ) {
            final long size = from.size();
            //注意：传输默认上限最大为2G
            from.transferTo(0, size, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 传输超过2G容量大小
     */
    public static void transferTo_t2G(String FILE_FROM, String FILE_TO){
        try (
                FileChannel from = new FileInputStream(FILE_FROM).getChannel();
                FileChannel to = new FileOutputStream(FILE_TO).getChannel();
        ) {
            //通过传输容量与原始容量比对来解决一次传输>2G文件的情况
            long leftSize = from.size();
            while (true) {
                leftSize -= from.transferTo(from.size() - leftSize, leftSize, to);
                System.out.println("已拷贝：" + (from.size() - leftSize)/1024.0/1024.0 + "MB，剩余：" + leftSize/1024.0/1024.0 + "MB");
                if (leftSize < 2 * 1024 * 1024){
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}