package com.ict.protice01.common.util;

import java.io.File;
import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/util")
public class Util {

    public String UploadFile(MultipartFile file){
        try {
            String uploadDir = new File("src/main/resources/uploads").getAbsolutePath();
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            if (!file.isEmpty()) {
                String filename = UUID.randomUUID()+"_"+file.getOriginalFilename();
                file.transferTo(new File(uploadDir,filename));
                return filename;
            }else{
                log.info("파일 처리 문제 생김");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("파일 저장 실패");
        }
    }
}
