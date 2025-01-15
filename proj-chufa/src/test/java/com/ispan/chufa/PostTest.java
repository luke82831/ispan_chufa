package com.ispan.chufa;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.Post;
import com.ispan.chufa.domain.TagsBean;
import com.ispan.chufa.repository.MemberRepository;
import com.ispan.chufa.repository.PostRepository;
import com.ispan.chufa.repository.TagsRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
public class PostTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagsRepository tagsRepository;
    
    @Autowired 
    private MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testInsertPostWithTags() {
        // 建立標籤
        TagsBean tag1 = new TagsBean();
        tag1.setTagName("travel");
        tag1.setTagState(1);
        tag1.setTagCreatedAt(LocalDateTime.now());

        TagsBean tag2 = new TagsBean();
        tag2.setTagName("hiphop");
        tag2.setTagState(1);
        tag2.setTagCreatedAt(LocalDateTime.now());

        tagsRepository.save(tag1);
        tagsRepository.save(tag2);
        
        // 建立 Member
        MemberBean member = new MemberBean();
        member.setRole(MemberBean.Role.USER);
        member.setUsername("hey");
        member.setPassword("password3".getBytes(StandardCharsets.UTF_8));
        member.setPhoneNumber("09989012223324");
        member.setEmail("gey@example.com");
        member.setName("hey");
        member.setGender("Male");
        member.setNickname("hey");

        // 儲存 Member，確保它已經持久化
        MemberBean savedMember = memberRepository.save(member);

        // 建立 Post
        Post post = new Post();
        post.setPostTitle("what what what");
        post.setPostContent("This is a detailed article about Spring Boot.");
        post.setPostLink("https://example.com/spring-boot");
        post.setPostStatus("Published");
        post.setPostTime(LocalDateTime.now());
        post.setTags("Java");

        // 關聯已保存的 Member
        post.setMember(savedMember);

        // 關聯標籤
        Set<TagsBean> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);
        post.setTag(tags);

        // 儲存 Post
        Post savedPost = postRepository.save(post);

        // 驗證插入結果
        assertNotNull(savedPost.getPostid(), "Post ID should not be null");
        assertNotNull(savedPost.getMember(), "Member should not be null");
        assertNotNull(savedPost.getTag(), "Tags should not be null");
        assertNotNull(savedPost.getPostTitle(), "Post Title should not be null");
    }
}
