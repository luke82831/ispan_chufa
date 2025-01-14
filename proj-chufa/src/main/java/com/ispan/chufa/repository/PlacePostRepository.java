package com.ispan.chufa.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.chufa.domain.PlacePostBean;

public interface PlacePostRepository extends JpaRepository<PlacePostBean, Long>{
//	 @Query("SELECT p.price FROM PlacePostBean pp JOIN pp.place p JOIN pp.post post WHERE post.postTitle = :postTitle")
//	    List<String> findPriceByPostTitle(@Param("postTitle") String postTitle);
}
