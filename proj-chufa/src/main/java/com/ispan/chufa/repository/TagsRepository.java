package com.ispan.chufa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.chufa.domain.TagsBean;

public interface TagsRepository extends JpaRepository<TagsBean, Long> {

}
