package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class FotoInfo {
    private List<FotoTagInfo> exit;

    private Gps gps;

    private FileInfo fileInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Gps {
        private Double latitude;
        private Double longitude;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FotoTagInfo {
        private Integer tagType;
        private String desc;
        private String tagName;
        private String tagNameCn;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileInfo {
        private long fileSize;
        private String fileName;
        private int width;
        private int height;

        private String creationTime;
        private String modifiedTime;
    }
}
