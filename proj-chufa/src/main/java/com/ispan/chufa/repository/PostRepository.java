package com.ispan.chufa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.chufa.domain.PostBean;

public interface PostRepository extends JpaRepository<PostBean, Long> {

}
