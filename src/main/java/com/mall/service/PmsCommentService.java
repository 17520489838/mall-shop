package com.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.dto.CommentDTO;
import com.mall.entity.PmsComment;

/**
 * 评价服务接口
 */
public interface PmsCommentService {

    void addComment(Long userId, CommentDTO dto);

    Page<PmsComment> listProductComments(Long productId, Integer pageNum, Integer pageSize);

    Page<PmsComment> adminListComments(Integer pageNum, Integer pageSize, Integer status);
}
