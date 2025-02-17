package com.ispan.chufa.controller;

import java.util.NoSuchElementException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.PostBean;
import com.ispan.chufa.domain.ScheduleBean;
import com.ispan.chufa.domain.TagsBean;
import com.ispan.chufa.dto.JackPostDTO;
import com.ispan.chufa.dto.JackTagsDTO;
import com.ispan.chufa.dto.Response;
import com.ispan.chufa.repository.MemberRepository;
import com.ispan.chufa.repository.PostRepository;
import com.ispan.chufa.service.JackPostService;
import com.ispan.chufa.service.ScheduleService;
import com.ispan.chufa.service.TagsService;

@RestController
@RequestMapping("/post")
public class JackPostController {
    @Autowired
    private JackPostService postService;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagsService tagsService;
    @Autowired
    private ScheduleService scheduleService;
    // 將Bean映射到DTO用的
    private final ModelMapper modelMapper = new ModelMapper();

    // 創建貼文
    // 測試 http://localhost:8080/post/create
    // 測試 RequestBody =>
    // {"postTitle":"標題","postContent":"內容","postLink":"超連結","userid":"1","tripId":"1","tagId":[1,2,3,4,5]}
    @PostMapping("/create")
    public Response create(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        Response response = new Response();
        PostBean bean = new PostBean();
        MemberBean memberBean;

        System.out.println(json);
        String postTitle;
        String postContent;
        String postLink;
        Long userid;
        Long tripId;
        JSONArray tagIds;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("tagId") && requestJson.getJSONArray("tagId").length() != 0) {
                tagIds = requestJson.getJSONArray("tagId");
            } else {
                tagIds = null;
            }
            if (!requestJson.isNull("tripId") && requestJson.getString("tripId").length() != 0) {
                try {
                    tripId = requestJson.getLong("tripId");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("tripId請輸入整數");
                    return response;
                }
            } else {
                tripId = null;
            }

            if (!requestJson.isNull("postTitle")) {
                postTitle = requestJson.getString("postTitle");
                if (postTitle.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入文章標題");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postTitle");
                return response;
            }

            if (!requestJson.isNull("postContent")) {
                postContent = requestJson.getString("postContent");
                if (postContent.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入postContent");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postContent");
                return response;
            }

            if (!requestJson.isNull("postLink")) {
                postLink = requestJson.getString("postLink");
                if (postLink.length() == 0) {
                    postLink = null;
                }
            } else {
                postLink = null;
            }

            if (!requestJson.isNull("userid")) {
                try {
                    userid = requestJson.getLong("userid");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("userid請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入userid");
                return response;
            }
        }

        // 設定PostBean
        {
            // postTitle
            bean.setPostTitle(postTitle);
            // postContent
            bean.setPostContent(postContent);
            // postLink
            if (postLink != null) {
                bean.setPostLink(postLink);
            }
            // memberBean
            try {
                memberBean = memberRepository.findById(userid).get();
            } catch (NoSuchElementException e) {
                response.setSuccesss(false);
                response.setMessage("沒有這個userid");
                return response;
            }
            bean.setMember(memberBean);

            if (tripId != null) {
                try {
                    ScheduleBean scheduleBean = scheduleService.findScheduleById(tripId).get();
                    bean.setScheduleBean(scheduleBean);
                } catch (NoSuchElementException e) {
                    response.setSuccesss(false);
                    response.setMessage("沒有這個行程");
                    return response;
                }
            }

            if (tagIds != null) {
                for (int i = 0; i < tagIds.length(); i++) {
                    TagsBean tagsBean = tagsService.findById(tagIds.getLong(i));
                    bean.getTagsBeans().add(tagsBean);
                    JackTagsDTO dto = modelMapper.map(tagsBean, JackTagsDTO.class);
                }
            }
        }

        // 創建貼文
        try {
            postService.createPost(bean);
        } catch (NoSuchElementException e) {
            response.setSuccesss(false);
            response.setMessage("這個行程有人使用了");
            return response;
        }

        // 設定response
        {
            response.setSuccesss(true);
            response.setMessage("創建貼文成功");
            JackPostDTO dto = modelMapper.map(bean, JackPostDTO.class);
            response.getList().add(dto);
        }

        return response;
    }

    // 刪除貼文
    // 測試 http://localhost:8080/post/delete
    // 測試 RequestBody => {"postid":"1"}
    @PostMapping("/delete")
    public Response delete(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        Response response = new Response();

        Long postid;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("postid")) {
                try {
                    postid = requestJson.getLong("postid");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("postid請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postid");
                return response;
            }
        }

        // 刪除貼文
        {
            if (postService.deletePost(postid)) {
                response.setSuccesss(true);
                response.setMessage("已刪除這筆貼文");
            } else {
                response.setSuccesss(false);
                response.setMessage("刪除失敗");
            }
        }

        return response;
    }

    // 查詢貼文
    // 測試 http://localhost:8080/post/findById/{postid}
    @GetMapping("/findById/{postid}")
    public Response findById(@PathVariable String postid) {
        Response response = new Response();
        PostBean bean;

        Long longPostid;
        // 驗證request資料(防呆)
        System.out.println(postid);
        {
            if (postid != null && postid != "") {
                try {
                    longPostid = Long.parseLong(postid);
                } catch (NumberFormatException e) {
                    response.setSuccesss(false);
                    response.setMessage("postid請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postid");
                return response;
            }
        }

        // 查詢貼文
        bean = postService.findById(longPostid);

        // 設定response
        {
            if (bean != null) {
                // 將Bean映射到DTO用的
                JackPostDTO dto = modelMapper.map(bean, JackPostDTO.class);
                response.setSuccesss(true);
                response.setMessage("查尋成功");
                response.getList().add(dto);
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆貼文");
            }
        }

        return response;
    }

    // 更新貼文
    // 測試 http://localhost:8080/post/update
    // 測試 RequestBody => {"postid":"1","postTitle":"標題","postContent":"內容"}
    @PutMapping("/update")
    public Response update(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        Response response = new Response();
        PostBean bean;

        Long postid;
        String postTitle;
        String postContent;

        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("postid")) {
                try {
                    postid = requestJson.getLong("postid");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("postid請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postid");
                return response;
            }

            if (!requestJson.isNull("postTitle")) {
                postTitle = requestJson.getString("postTitle");
                if (postTitle.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入postTitle");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postTitle");
                return response;
            }

            if (!requestJson.isNull("postContent")) {
                postContent = requestJson.getString("postContent");
                if (postContent.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入postContent");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postContent");
                return response;
            }

        }

        // 更新貼文
        bean = postService.updatePost(postid, postTitle, postContent);
        // 設定response
        {
            if (bean != null) {
                // 將Bean映射到DTO用的
                JackPostDTO dto = modelMapper.map(bean, JackPostDTO.class);
                response.setSuccesss(true);
                response.setMessage("更新成功");
                response.getList().add(dto);
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆ID");
            }
        }

        return response;
    }

    // 更新貼文標籤
    // 測試 http://localhost:8080/post/updateTags
    // 測試 RequestBody => {"postid":"1","tagId":[1,2,3,4,5]}
    @PutMapping("/updateTags")
    public Response updateTags(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        Response response = new Response();
        PostBean bean;

        Long postid;
        JSONArray tagIds;

        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("postid")) {
                try {
                    postid = requestJson.getLong("postid");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("postid請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postid");
                return response;
            }

            if (!requestJson.isNull("tagId") && requestJson.getJSONArray("tagId").length() != 0) {
                tagIds = requestJson.getJSONArray("tagId");
            } else {
                tagIds = null;
            }
        }

        // 搜尋貼文
        bean = postService.findById(postid);
        // 更新貼文標籤
        bean.getTagsBeans().clear();
        if (tagIds != null) {
            for (int i = 0; i < tagIds.length(); i++) {
                TagsBean tagsBean = tagsService.findById(tagIds.getLong(i));
                bean.getTagsBeans().add(tagsBean);
            }
        }

        // 設定response
        {
            postRepository.save(bean);
            if (bean != null) {
                // 將Bean映射到DTO用的
                JackPostDTO dto = modelMapper.map(bean, JackPostDTO.class);
                response.setSuccesss(true);
                response.setMessage("更新成功");
                response.getList().add(dto);
            }
        }

        return response;
    }

}
