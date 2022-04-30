package com.numble.shortForm.upload;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile multipartFile, String dirName) throws IOException {

        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_CONVERT_ERROR));
        return upload(uploadFile,dirName);
    }

    public String upload(File uploadFile, String filePath) {
        String fileName = filePath+"/"+ UUID.randomUUID()+uploadFile.getName();
        String uploadImageUrl  = pusS3(uploadFile,fileName);

        removeNewFile(uploadFile);
        return uploadImageUrl;
    }



    private String pusS3(File uploadFile, String fileName) {
        // withCannedAcl 권한을 부여하는것. public으로 지정
        amazonS3Client.putObject(new PutObjectRequest(bucket,fileName,uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        log.info("pustS3 메서드 fileName {}",fileName );
        return amazonS3Client.getUrl(bucket,fileName).toString();
    }


    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("targetFile delete success ");
            return;
        }
        log.info("file delete fail");
    }


    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir")+"/",file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            log.info("createNewfile success");
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
}
