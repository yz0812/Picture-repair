package com.example.demo;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.png.PngDirectory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

public class ImageMetadataReaderExample {

    private static String getString(ResourceBundle bundle, String key) throws IOException {
        String value = bundle.getString(key);
        return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
    // 加载中文资源文件

    public static void main(String[] args) {
        try {
            // 指定要读取的图片文件
            //      File file = new File("C:\\Users\\yuanzhang\\Desktop\\IMG_0399.HEIC");
            File file = new File("C:\\Users\\yuanzhang\\Desktop\\IMG_20240320_01322.jpg");
            // 使用metadata-extractor库读取图片元数据
            Metadata metadata = null;
            metadata = ImageMetadataReader.readMetadata(file);

            ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.CHINA);

            // 获取拍摄时间
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (directory != null) {

                // 遍历ExifIFD0Directory中的所有标签
                Iterator<Tag> tags = directory.getTags().iterator();
                while (tags.hasNext()) {
                    Tag tag = tags.next();
                    if (bundle.containsKey("exif_" + tag.getTagType())) {
                        String translatedTagName = getString(bundle, "exif_" + tag.getTagType());
                        System.out.println(translatedTagName + " : " + tag.getDescription());
                    } else {
                        System.out.println("额外信息:" + tag.getTagType() + ":" + tag.getDescription());
                    }
                }

            }

            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            // 获取经纬度信息
            if (gpsDirectory != null) {
                Double latitude = gpsDirectory.getGeoLocation().getLatitude();
                Double longitude = gpsDirectory.getGeoLocation().getLongitude();
                System.out.println("经度: " + latitude);
                System.out.println("纬度: " + longitude);
            }


            // 获取文件大小
            long fileSize = file.length();
            System.out.println("文件大小: " + fileSize);

            // 获取文件名称
            String fileName = file.getName();
            System.out.println("文件名称: " + fileName);

            // 获取分辨率
            PngDirectory pngDirectory = metadata.getFirstDirectoryOfType(PngDirectory.class);
            if (pngDirectory != null) {
                int width = pngDirectory.getInt(PngDirectory.TAG_IMAGE_WIDTH);
                int height = pngDirectory.getInt(PngDirectory.TAG_IMAGE_HEIGHT);
                System.out.println("分辨率: " + width + " x " + height);
            }else {

                BufferedImage image = ImageIO.read(file);

                int width = image.getWidth();
                int height = image.getHeight();

                System.out.println("Width: " + width);
                System.out.println("Height: " + height);

            }



            BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

            FileTime creationTime = attributes.creationTime();
            FileTime modifiedTime = attributes.lastModifiedTime();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String creationTimeString = dateFormat.format(creationTime.toMillis());
            String modifiedTimeString = dateFormat.format(modifiedTime.toMillis());

            System.out.println("Creation Time: " + creationTimeString);
            System.out.println("Modified Time: " + modifiedTimeString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//
//            // 获取经纬度信息
//            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
//            if (gpsDirectory != null) {
//                Double latitude = gpsDirectory.getGeoLocation().getLatitude();
//                Double longitude = gpsDirectory.getGeoLocation().getLongitude();
//                System.out.println("经度: " + latitude);
//                System.out.println("纬度: " + longitude);
//            }
//
//            // 获取创建日期和修改日期
//            FileSystemDirectory fileSystemDirectory = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
//            if (fileSystemDirectory != null) {
//               // Date createdDate = fileSystemDirectory.getDate(FileSystemDirectory.TAG_FILE_CREATED_DATE);
//                Date modifiedDate = fileSystemDirectory.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);
//              //  System.out.println("创建日期: " + createdDate);
//                System.out.println("修改日期: " + modifiedDate);
//            }
//
//            // 获取拍摄参数
//            QuickTimeDirectory quickTimeDirectory = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);
//            if (quickTimeDirectory != null) {
////                String cameraMake = quickTimeDirectory.getString(QuickTimeDirectory.TAG_MAKE);
////                String cameraModel = quickTimeDirectory.getString(QuickTimeDirectory.TAG_MODEL);
////                System.out.println("相机制造商: " + cameraMake);
////                System.out.println("相机型号: " + cameraModel);
//                System.out.println(quickTimeDirectory);
//            }
//
//            // 获取文件大小
//            long fileSize = file.length();
//            System.out.println("文件大小: " + fileSize);
//
//            // 获取文件名称
//            String fileName = file.getName();
//            System.out.println("文件名称: " + fileName);
//
//            // 获取分辨率
//            PngDirectory pngDirectory = metadata.getFirstDirectoryOfType(PngDirectory.class);
//            if (pngDirectory != null) {
//                int width = pngDirectory.getInt(PngDirectory.TAG_IMAGE_WIDTH);
//                int height = pngDirectory.getInt(PngDirectory.TAG_IMAGE_HEIGHT);
//                System.out.println("分辨率: " + width + " x " + height);
//            }

//        catch (MetadataException e) {
//            throw new RuntimeException(e);
//        }
