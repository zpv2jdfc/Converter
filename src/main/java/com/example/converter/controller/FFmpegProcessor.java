package com.example.converter.controller;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

/**
 * @author : xuansy
 * @version : 1.0
 * @date : 2021/5/21 22:46
 * @project_name: ffmpeg-demo
 * @package_name : com.example.ffmpeg.demo.processor
 * @name: FFmpegProcessor
 * @email: 1292798418@qq.com
 * @description :
 */
public class FFmpegProcessor {

    /**
     * 这个方法的url地址都必须是一样的类型 同为post
     */
    public static void convertMediaToM3u8ByHttp(InputStream inputStream, String m3u8Url, String infoUrl) throws IOException {

        avutil.av_log_set_level(avutil.AV_LOG_INFO);
        FFmpegLogCallback.set();

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
        grabber.start();

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(m3u8Url, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());

        recorder.setFormat("hls");
        recorder.setOption("hls_time", "5");
        recorder.setOption("hls_list_size", "0");
        recorder.setOption("hls_flags", "delete_segments");
        recorder.setOption("hls_delete_threshold", "1");
        recorder.setOption("hls_segment_type", "mpegts");
        recorder.setOption("hls_segment_filename", "C:\\Users\\79301\\Desktop\\upload\\test-%d.ts");
        recorder.setOption("hls_key_info_file", infoUrl);

        // http属性
        recorder.setOption("method", "POST");

        recorder.setFrameRate(25);
        recorder.setGopSize(2 * 25);
        recorder.setVideoQuality(1.0);
        recorder.setVideoBitrate(10 * 1024);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        recorder.start();

        Frame frame;
        while ((frame = grabber.grabImage()) != null) {
            try {
                recorder.record(frame);
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }
        recorder.setTimestamp(grabber.getTimestamp());
        recorder.close();
        grabber.close();
    }
    public static void doConversions(File videoFile) {

        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFile);

        try {
            String name = videoFile.getName().split("\\.")[0];
            File file = new File(Paths.get(videoFile.getParentFile().getPath(), name).toString());
            file.mkdir();
            frameGrabber.start();
            Frame grab;
            FrameRecorder recorder = new FFmpegFrameRecorder(new File(file.getPath() + "/" + name + ".m3u8"), frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), frameGrabber.getAudioChannels());
            recorder.setAspectRatio((double) 16/9);
            recorder.setFrameRate(frameGrabber.getFrameRate());
            recorder.setGopSize((int)frameGrabber.getFrameRate()*2);
            recorder.setVideoQuality(1);
            recorder.setVideoBitrate(frameGrabber.getVideoBitrate());
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.setOption("vf", "setdar=16:9");
            recorder.setOption("hls_time", "3");
            recorder.setOption("start_number", "0");
            recorder.setOption("hls_list_size", "0");
            recorder.setOption("f", "hls");
            recorder.setOption("level", "3.0");
            recorder.setOption("profile:v", "baseline");
            recorder.setOption("strict", "experimental");
            recorder.setOption("hls_key_info_file", "C:\\Users\\79301\\Desktop\\test.info");
            recorder.start();
            while ((grab = frameGrabber.grab()) != null) {
                recorder.record(grab);
            }
            recorder.stop();
            recorder.release();
            frameGrabber.stop();
        } catch (FrameGrabber.Exception | FrameRecorder.Exception ignored) {
            System.out.println(ignored.getMessage());
        }}
}

