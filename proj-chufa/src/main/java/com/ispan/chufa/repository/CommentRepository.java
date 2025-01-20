package com.ispan.chufa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.CommentBean;

@Repository
public interface CommentRepository extends JpaRepository<CommentBean, Long> {
    List<CommentBean> findByParentId(Long parentId);

    List<CommentBean> findByMemberBeanUserid(Long userId);
}
