package com.ispan.chufa.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mycoupon")
public class MyCouponBean {

	@Id
    @Column(name = "mycoupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myCouponId;

    @ManyToOne
    @JsonIgnoreProperties("mycoupon")
    @JoinColumn(name = "fk_couponid", referencedColumnName = "couponId", nullable = true)
    private CouponBean couponBean; // FK_優惠券ID

    @ManyToOne
    @JsonIgnoreProperties("mycoupon")
    @JoinColumn(name = "fk_userid", referencedColumnName = "userid", nullable = false)
    private MemberBean memberBean; // FK_使用者ID

    @Column(name = "gettime")
    private LocalDateTime getTime; // 紀錄領取時間

	public Long getMyCouponId() {
		return myCouponId;
	}

	public void setMyCouponId(Long myCouponId) {
		this.myCouponId = myCouponId;
	}

	public CouponBean getCouponBean() {
		return couponBean;
	}

	public void setCouponBean(CouponBean couponBean) {
		this.couponBean = couponBean;
	}

	public MemberBean getMemberBean() {
		return memberBean;
	}

	public void setMemberBean(MemberBean memberBean) {
		this.memberBean = memberBean;
	}

	
//	// 在插入資料之前自動設定領取時間為當前時間
//    @PrePersist
//    public void prePersist() {
//        this.getTime = LocalDateTime.now(); // 設定為當前的系統時間
//    }

    public LocalDateTime getGetTime() {
    	return getTime;
    }

    public void setGetTime(LocalDateTime getTime) {
		this.getTime = getTime;
	}

	@Override
	public String toString() {
		return "MyCoupon [myCouponId=" + myCouponId + ", couponBean=" + couponBean + ", memberBean=" + memberBean
				+ ", getTime=" + getTime + "]";
	}

}
