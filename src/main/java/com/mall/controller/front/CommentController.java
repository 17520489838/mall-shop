package com.mall.controller.front;

import com.mall.common.result.Result;
import com.mall.common.utils.CurrentUserUtils;
import com.mall.dto.CommentDTO;
import com.mall.service.PmsCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/comments")
@Tag(name = "评价接口")
public class CommentController {

    private final PmsCommentService commentService;

    public CommentController(PmsCommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @Operation(summary = "添加评价")
    public Result<String> addComment(@Valid @RequestBody CommentDTO dto) {
        commentService.addComment(CurrentUserUtils.getUserId(), dto);
        return Result.success("评价成功");
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "商品评价列表")
    public Result<?> listProductComments(@PathVariable Long productId,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(commentService.listProductComments(productId, pageNum, pageSize));
    }
}
