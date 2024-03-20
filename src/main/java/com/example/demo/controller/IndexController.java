package com.example.demo.controller;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.example.demo.dto.FotoInfo;
import com.example.demo.util.MetadataUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class IndexController {

    @PostMapping("/getInfo")
    public FotoInfo uploadImage(@RequestParam("file") MultipartFile file) throws IOException, ImageProcessingException {
        return MetadataUtil.getFotoInfo(file);
    }
}
