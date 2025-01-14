package com.ispan.chufa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.chufa.domain.MemberBean;

public interface MemberRepository extends JpaRepository<MemberBean, Long> {

}
