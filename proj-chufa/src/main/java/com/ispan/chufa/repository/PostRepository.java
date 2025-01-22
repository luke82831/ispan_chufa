package com.ispan.chufa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.domain.PostBean;

@Repository
public interface PostRepository extends JpaRepository<PostBean, Long>, PostDao {
    // 根據 placeBean 刪除所有資料
	void deleteByPlace(PlaceBean place);  // 修改方法名稱為 deleteByPlace
}
