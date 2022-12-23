package com.example.converter.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author : xuansy
 * @version : 1.0
 * @date : 2021/5/21 22:45
 * @project_name: ffmpeg-demo
 * @package_name : com.example.ffmpeg.demo.controller
 * @name: TestController
 * @email: 1292798418@qq.com
 * @description :
 */
@RestController
public class TestController {

    /**
     * 目录路径,这个路径需要包含test.info文件，test.key文件和test.mp4文件
     */
    private static final String PATH = "C:\\Users\\79301\\Desktop\\";

    @PostMapping("uploadToM3u8")
    public void uploadToM3u8() throws Exception {
        FileInputStream inputStream = new FileInputStream(PATH + "test.mp4");
        String m3u8Url = "C:\\Users\\79301\\Desktop\\upload\\test.m3u8";
        String infoUrl = "C:\\Users\\79301\\Desktop\\test.info";
//        FFmpegProcessor.convertMediaToM3u8ByHttp(inputStream, m3u8Url, infoUrl);
        FFmpegProcessor.doConversions(new File("C:\\Users\\79301\\Desktop\\test.mp4"));
    }

    @PostMapping("upload/{fileName}")
    public void upload(HttpServletRequest request, @PathVariable("fileName") String fileName) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        FileWriter writer = new FileWriter(PATH + fileName);
        writer.writeFromStream(inputStream);
        IoUtil.close(inputStream);
    }

    /**
     * 预览加密文件
     */
    @PostMapping("preview/{fileName}")
    public void preview(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
        FileReader fileReader = new FileReader(PATH + fileName);
        fileReader.writeToStream(response.getOutputStream());
    }

    /**
     * 预览加密文件
     */
    @GetMapping("download/{fileName}")
    public void download(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
        FileReader fileReader = new FileReader(PATH + fileName);
        fileReader.writeToStream(response.getOutputStream());
    }

}

