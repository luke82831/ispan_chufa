package com.ispan.chufa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.domain.PostBean;

@Repository
public interface PostRepository extends JpaRepository<PostBean, Long>, PostDao {
    // 根據 placeBean 刪除所有資料
	void deleteByPlace(PlaceBean place);  // 修改方法名稱為 deleteByPlace

	
	@Transactional
    @Modifying
    @Query("DELETE FROM PostBean p WHERE p.forwardedFrom = :post")
    void deleteAllByForwardedFrom(@Param("post") PostBean post);

	
	
	 public long countByForwardedFrom(PostBean postlist);
}
