package com.ispan.chufa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.PostBean;

@Repository
public interface PostRepository extends JpaRepository<PostBean, Long> {
}
