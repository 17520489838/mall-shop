package com.mall.controller.admin;

import com.mall.common.result.Result;
import com.mall.common.utils.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/admin/upload")
@Tag(name = "管理后台文件上传")
public class OssUploadController {

    private final OssService ossService;

    public OssUploadController(OssService ossService) {
        this.ossService = ossService;
    }

    @PostMapping
    @Operation(summary = "上传文件到OSS")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.failed(400, "请选择要上传的文件");
        }
        String url = ossService.upload(file);
        return Result.success("上传成功", url);
    }
}
