package com.ispan.chufa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.chufa.domain.MemberBean;

public interface MemberRepository extends JpaRepository<MemberBean, Long> {
    // 根據 email 查詢 MemberBean
    Optional<MemberBean> findByEmail(String email);

    // 檢查帳號是否已存在
    boolean existsByUsername(String username);

    // 檢查電子郵件是否已存在
    boolean existsByEmail(String email);
}
