package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.ResultCode;
import com.mall.dao.OmsOrderDao;
import com.mall.dao.OmsOrderItemDao;
import com.mall.dao.PmsCommentDao;
import com.mall.dao.UmsUserDao;
import com.mall.dto.CommentDTO;
import com.mall.entity.OmsOrder;
import com.mall.entity.PmsComment;
import com.mall.entity.UmsUser;
import com.mall.service.PmsCommentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PmsCommentServiceImpl implements PmsCommentService {

    private final PmsCommentDao commentDao;
    private final OmsOrderDao orderDao;
    private final OmsOrderItemDao orderItemDao;
    private final UmsUserDao userDao;

    public PmsCommentServiceImpl(PmsCommentDao commentDao, OmsOrderDao orderDao,
                                  OmsOrderItemDao orderItemDao, UmsUserDao userDao) {
        this.commentDao = commentDao;
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.userDao = userDao;
    }

    @Override
    public void addComment(Long userId, CommentDTO dto) {
        // 验证订单
        OmsOrder order = orderDao.selectById(dto.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        // 检查是否已评价
        Long count = commentDao.selectCount(
                new LambdaQueryWrapper<PmsComment>()
                        .eq(PmsComment::getUserId, userId)
                        .eq(PmsComment::getOrderId, dto.getOrderId())
                        .eq(PmsComment::getProductId, dto.getProductId()));
        if (count > 0) {
            throw new BusinessException(ResultCode.COMMENT_ALREADY_EXIST);
        }

        PmsComment comment = new PmsComment();
        comment.setProductId(dto.getProductId());
        comment.setUserId(userId);
        comment.setOrderId(dto.getOrderId());
        comment.setContent(dto.getContent());
        comment.setRating(dto.getRating());
        comment.setPics(dto.getPics());
        comment.setIsAnonymous(dto.getIsAnonymous() != null ? dto.getIsAnonymous() : 0);
        comment.setStatus(1);
        commentDao.insert(comment);
    }

    @Override
    public Page<PmsComment> listProductComments(Long productId, Integer pageNum, Integer pageSize) {
        Page<PmsComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PmsComment::getProductId, productId)
                .eq(PmsComment::getStatus, 1)
                .orderByDesc(PmsComment::getCreatedAt);
        Page<PmsComment> result = commentDao.selectPage(page, wrapper);

        // 填充用户信息
        result.getRecords().forEach(c -> {
            if (c.getIsAnonymous() == 0) {
                UmsUser user = userDao.selectById(c.getUserId());
                if (user != null) {
                    c.setNickname(user.getNickname());
                    c.setAvatar(user.getAvatar());
                }
            } else {
                c.setNickname("匿名用户");
            }
        });

        return result;
    }

    @Override
    public Page<PmsComment> adminListComments(Integer pageNum, Integer pageSize, Integer status) {
        Page<PmsComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsComment> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(PmsComment::getStatus, status);
        }
        wrapper.orderByDesc(PmsComment::getCreatedAt);
        return commentDao.selectPage(page, wrapper);
    }
}
