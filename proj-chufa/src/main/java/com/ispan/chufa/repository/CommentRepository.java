package com.ispan.chufa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.chufa.domain.CommentBean;

public interface CommentRepository extends JpaRepository<CommentBean, Integer> {

}
