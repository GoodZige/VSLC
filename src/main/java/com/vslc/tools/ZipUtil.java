package com.vslc.tools;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static org.springframework.util.FileCopyUtils.BUFFER_SIZE;

public class ZipUtil {

    /**
     * 压缩成ZIP 压缩单个文件或文件夹
     * @param srcPath 压缩源文件路径
     * @param zipPath 压缩目标文件路径
     * @param keepDirStructure  是否保留原来的目录结构,true:保留目录结构;
     * false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String srcPath, String zipPath, boolean keepDirStructure) {
        long start = System.currentTimeMillis();
        System.out.println("正在压缩：" + srcPath);
        try {
            FileOutputStream out = new FileOutputStream(new File(zipPath));
            ZipOutputStream zos = new ZipOutputStream(out);
            File sourceFile = new File(srcPath);
            compress(sourceFile, zos, sourceFile.getName(), keepDirStructure);
            zos.close();
            out.close();
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
        } catch (IOException e) {
            throw new RuntimeException("zip error from ZipUtil", e);
        }
    }

    /**
     * 压缩成ZIP 压缩多个文件或文件夹
     * @param srcFiles 需要压缩的文件列表
     * @param zipPath  输出文件路径
     * @throws IOException 压缩失败会抛出运行时异常
     */
    public static void toZip(List<File> srcFiles, String zipPath, boolean keepDirStructure) {
        long start = System.currentTimeMillis();
        try {
            FileOutputStream out = new FileOutputStream(new File(zipPath));
            ZipOutputStream zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                System.out.println("正在压缩：" + srcFile.getAbsolutePath());
                compress(srcFile, zos, srcFile.getName(), keepDirStructure);
            }
            zos.close();
            out.close();
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtil", e);
        }
    }

    /**
     * 递归压缩
     * @param srcFile 源文件
     * @param zos zip输出流
     * @param name 压缩后的名称
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     * false:所有文件跑到压缩包根目录下(不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws IOException
     */
    public static void compress(File srcFile, ZipOutputStream zos, String name,
                                boolean keepDirStructure) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        if(srcFile.isFile()) {
            //向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            //copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(srcFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            in.close();
        } else {
            String dirName = srcFile.getName();
            if (!dirName.equals("NotDcm")) {
                File[] listFiles = srcFile.listFiles();
                if(listFiles == null || listFiles.length == 0) {
                    //需要保留原来的文件结构时,需要对空文件夹进行处理
                    if(keepDirStructure){
                        //空文件夹的处理
                        zos.putNextEntry(new ZipEntry(name + "/"));
                        //没有文件，不需要文件的copy
                        zos.closeEntry();
                    }

                } else {
                    for (File file : listFiles) {
                        //判断是否需要保留原来的文件结构
                        if (keepDirStructure) {
                            //注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                            //不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                            compress(file, zos, name + "/" + file.getName(), keepDirStructure);
                        } else {
                            compress(file, zos, file.getName(), keepDirStructure);
                        }
                    }
                }
            }
        }
    }

    public static void unZip(String srcZip, String desDir) throws IOException {
        long start = System.currentTimeMillis();
        File zipFile = new File(srcZip);
        ZipFile zip = new ZipFile(zipFile,Charset.forName("GBK"));
        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (desDir + zipEntryName).replaceAll("\\*", "/");
            //判断路径是否存在,不存在则创建文件路径
            System.out.println(outPath);
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
            FileOutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
        long end = System.currentTimeMillis();
        System.out.println("解压完成，耗时：" + (end - start) + " ms");
    }
}
