package com.example.demo.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.png.PngDirectory;
import com.example.demo.dto.FotoInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Slf4j
public class MetadataUtil {
    public static FotoInfo getFotoInfo(MultipartFile file) throws IOException, ImageProcessingException {
        Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());


        File tempFile = File.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(tempFile);


        FotoInfo info = new FotoInfo();
        info.setExit(getExit(metadata));
        info.setGps(getGps(metadata));
        info.setFileInfo(getFileInfo(metadata, tempFile));
        return info;
    }

    public static List<FotoInfo.FotoTagInfo> getExit(Metadata metadata) {
        List<FotoInfo.FotoTagInfo> fotoTagInfoList = new ArrayList<>();
        try {
            ResourceBundle bundleCn = ResourceBundle.getBundle("messages", Locale.CHINA);
            ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.US);
            // 获取拍摄时间
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (directory != null) {
                // 遍历ExifIFD0Directory中的所有标签
                for (Tag tag : directory.getTags()) {
                    String key = String.format("exif_%d", tag.getTagType());
                    if (bundle.containsKey(key)) {
                        String translatedTagNameCn = getString(bundleCn, key);
                        String translatedTagName = getString(bundle, key);
                        fotoTagInfoList.add(new FotoInfo.FotoTagInfo(tag.getTagType(), tag.getDescription(), translatedTagNameCn, translatedTagName));
                    } else {
                        // 属于没有定义的额外信息

                        fotoTagInfoList.add(new FotoInfo.FotoTagInfo(tag.getTagType(), tag.getDescription(), "额外信息", tag.getTagName()));
                    }
                }
            }


        } catch (IOException e) {
            log.error("获取图片额外信息异常", e);
        }
        return fotoTagInfoList;
    }

    public static FotoInfo.FileInfo getFileInfo(Metadata metadata, File file) {
        FotoInfo.FileInfo fileInfo = new FotoInfo.FileInfo();
        try {
            // 获取文件大小
            long fileSize = file.length();
            fileInfo.setFileSize(fileSize);

            // 获取文件名称
            String fileName = file.getName();
            fileInfo.setFileName(fileName);

            // 获取分辨率
            PngDirectory pngDirectory = metadata.getFirstDirectoryOfType(PngDirectory.class);
            if (pngDirectory != null) {
                int width = pngDirectory.getInt(PngDirectory.TAG_IMAGE_WIDTH);
                int height = pngDirectory.getInt(PngDirectory.TAG_IMAGE_HEIGHT);
                fileInfo.setWidth(width);
                fileInfo.setHeight(height);
            } else {

                BufferedImage image = ImageIO.read(file);

                int width = image.getWidth();
                int height = image.getHeight();
                fileInfo.setWidth(width);
                fileInfo.setHeight(height);

            }


            BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

            FileTime creationTime = attributes.creationTime();
            FileTime modifiedTime = attributes.lastModifiedTime();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String creationTimeString = dateFormat.format(creationTime.toMillis());
            String modifiedTimeString = dateFormat.format(modifiedTime.toMillis());
            fileInfo.setCreationTime(creationTimeString);
            fileInfo.setModifiedTime(modifiedTimeString);
        } catch (Exception e) {
            log.error("获取文件信息异常", e);
        }
        return fileInfo;
    }

    public static FotoInfo.Gps getGps(Metadata metadata) {
        GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
        // 获取经纬度信息
        if (gpsDirectory != null) {
            Double latitude = gpsDirectory.getGeoLocation().getLatitude();
            Double longitude = gpsDirectory.getGeoLocation().getLongitude();
            return new FotoInfo.Gps(latitude, longitude);
        }
        return null;
    }

    private static String getString(ResourceBundle bundle, String key) throws IOException {
        String value = bundle.getString(key);
        return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
