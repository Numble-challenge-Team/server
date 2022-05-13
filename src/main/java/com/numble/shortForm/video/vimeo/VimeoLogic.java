package com.numble.shortForm.video.vimeo;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
@Slf4j
public class VimeoLogic {

    @Value("${vimeo.token}")
    private String vimeoToken;

    public String uploadNormalVideo(MultipartFile video) throws IOException {

        Vimeo vimeo = new Vimeo(vimeoToken);

        //비디오 파일 저장
        File convertFile = new File(System.getProperty("user.dir") + "/" + video.getOriginalFilename());
        if (convertFile.createNewFile()) {
            log.info("createNewfile success");
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(video.getBytes());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        String videoEndPoint=null;
        String url = null;

        try {
            videoEndPoint = vimeo.addVideo(convertFile, false);

        } catch (JSONException | VimeoException | InterruptedException e) {
            log.error("json error : {}",e);
        }
        finally {
            convertFile.delete();
        }
        return videoEndPoint;
    }

    public VimeoResponse deleteVimeo(String videoEndPoint) throws IOException {
        Vimeo vimeo = new Vimeo(vimeoToken);

        return vimeo.removeVideo(videoEndPoint);
    }
}
