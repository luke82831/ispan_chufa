package com.ispan.chufa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "placepost")
public class PlacePostBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "placepostId")
    private Long placePostId; // 自己的主鍵

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)  // 確保不為 NULL
    private PlaceBean place;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)   // 確保不為 NULL
    private PostBean post;

    // getter 和 setter
	public Long getPlacePostId() {
		return placePostId;
	}

	public void setPlacePostId(Long placePostId) {
		this.placePostId = placePostId;
	}

	public PlaceBean getPlace() {
		return place;
	}

	public void setPlace(PlaceBean place) {
		this.place = place;
	}

	public PostBean getPost() {
		return post;
	}

	public void setPost(PostBean post) {
		this.post = post;
	}
    
}
