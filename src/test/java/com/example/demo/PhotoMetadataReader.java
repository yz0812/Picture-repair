package com.example.demo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

public class PhotoMetadataReader {
    public static void main(String[] args) {


        String path = "E:\\照片备份 - 副本 (3)";

        File directory = new File(path);

        File[] files = directory.listFiles();

        Arrays.asList(files).parallelStream().forEach(file -> {
            Date captureDate = getCaptureDate(file);
            if (captureDate != null) {
                System.out.println("拍摄时间（ExifIFD0）: " + DateUtil.formatDateTime(captureDate));
                // 设置修改时间
                // file.setLastModified(captureDate.getTime());
                // 修改文件名称
                String originalFileName = file.getName();
                String fileExtension = getFileExtension(originalFileName);
                String newFileName;
                if (isVideoFile(fileExtension)) {
                    newFileName = generateVideoFileName();
                } else {
                    newFileName = generateImageFileName();
                }
                String newFilePath = file.getParent() + File.separator + newFileName + "." + fileExtension;
                File newFile = new File(newFilePath);

                try {
                    File file1 = FileUtil.rename(file, newFileName, true, false);

                    if (file1 != null) {
                        System.out.println("文件已重命名为：" + file1.getName());
                    } else {
                        System.out.println("无法重命名文件。");
                    }
                }catch (Exception e){
                    System.out.println("文件已存在。");
                }


                // 设置创建时间
                FileTime creationTime = FileTime.fromMillis(captureDate.getTime());
                try {
                    Files.setLastModifiedTime(Paths.get(newFile.getPath()), creationTime);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("没有拍摄时间:" + file.getName());
            }
        });


    }

    private static String generateImageFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmssSSS");
        String timestamp = dateFormat.format(new Date());
        return "IMG_" + timestamp;
    }

    private static String generateVideoFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmssSSS");
        String timestamp = dateFormat.format(new Date());
        return "MP4_" + timestamp;
    }

    private static boolean isVideoFile(String fileExtension) {
        return fileExtension.equalsIgnoreCase("mp4") || fileExtension.equalsIgnoreCase("avi");
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    public static Date getCaptureDate(File imageFile) {

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);

            ExifSubIFDDirectory exifDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifDirectory != null) {
                Date date = exifDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                if (date != null) {
                    return date;
                }
            }
            ExifIFD0Directory ifd0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (ifd0Directory != null && ifd0Directory.containsTag(ExifIFD0Directory.TAG_DATETIME)) {
                Date dateTime = ifd0Directory.getDate(ExifIFD0Directory.TAG_DATETIME, TimeZone.getDefault());
                if (dateTime != null) {
                    return dateTime;
                }
            }
            // 获取其他时间相关的标签的值
            BasicFileAttributes fileAttributes = Files.readAttributes(imageFile.toPath(), BasicFileAttributes.class);

            return new Date(fileAttributes.lastModifiedTime().toMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


//    private static void extracted(File photoFile) {
//
//        try {
//            Metadata metadata = ImageMetadataReader.readMetadata(photoFile);
//
//            ExifIFD0Directory ifd0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
//            if (ifd0Directory != null && ifd0Directory.containsTag(ExifIFD0Directory.TAG_DATETIME)) {
//                Date dateTime = ifd0Directory.getDate(ExifIFD0Directory.TAG_DATETIME, TimeZone.getDefault());
//                if (dateTime!=null){
//                    System.out.println("拍摄时间（ExifIFD0）: " + DateUtil.formatDateTime(dateTime));
//                    // 设置修改时间
//                    photoFile.setLastModified(dateTime.getTime());
//
//                    // 设置创建时间
//                    FileTime creationTime = FileTime.fromMillis(dateTime.getTime());
//                    Files.setLastModifiedTime(Paths.get(photoFile.getPath()), creationTime);
//                }
//
//            }
//
//            ExifSubIFDDirectory subIfdDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
//            if (subIfdDirectory != null && subIfdDirectory.containsTag(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)) {
//                Date dateTimeOriginal = subIfdDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, TimeZone.getDefault());
//                System.out.println("拍摄时间（ExifSubIFD）: " +  DateUtil.formatDateTime(dateTimeOriginal));
//                if (dateTimeOriginal!=null){
//
//
//                // 设置修改时间
//                photoFile.setLastModified(dateTimeOriginal.getTime());
//
//                // 设置创建时间
//                FileTime creationTime = FileTime.fromMillis(dateTimeOriginal.getTime());
//                Files.setLastModifiedTime(Paths.get(photoFile.getPath()), creationTime);   }
//            }
//        } catch (ImageProcessingException | IOException e) {
//            e.printStackTrace();
//        }
//    }
}