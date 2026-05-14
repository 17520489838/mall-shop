package com.mall.common.utils;

import com.aliyun.oss.OSS;
import com.mall.common.config.OssConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class OssService {

    private final OSS ossClient;
    private final OssConfig ossConfig;

    public OssService(OSS ossClient, OssConfig ossConfig) {
        this.ossClient = ossClient;
        this.ossConfig = ossConfig;
    }

    /**
     * 上传文件到阿里云OSS
     * @param file 上传的文件
     * @return 文件的公开访问URL
     */
    public String upload(MultipartFile file) {
        // 生成存储路径: products/yyyyMMdd/uuid.ext
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = "products/" + dateStr + "/" + UUID.randomUUID().toString().replace("-", "") + ext;

        try (InputStream is = file.getInputStream()) {
            ossClient.putObject(ossConfig.getBucket(), fileName, is);
            // 返回公开访问URL
            String url = ossConfig.getEndpoint().replace("https://", "https://" + ossConfig.getBucket() + ".")
                    + "/" + fileName;
            log.info("OSS上传成功: {}", url);
            return url;
        } catch (IOException e) {
            log.error("OSS上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
}
