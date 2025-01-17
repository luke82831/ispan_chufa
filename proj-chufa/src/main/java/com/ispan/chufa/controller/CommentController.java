package com.ispan.chufa.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.json.JSONException;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.domain.CommentBean;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.PostBean;
import com.ispan.chufa.dto.CommentDTO;
import com.ispan.chufa.dto.CommentResponse;
import com.ispan.chufa.repository.MemberRepository;
import com.ispan.chufa.repository.PostRepository;
import com.ispan.chufa.service.CommentService;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    // 將Bean映射到DTO用的
    private final ModelMapper modelMapper = new ModelMapper();

    // 新增留言
    // 測試 http://localhost:8080/comment/create
    // 測試 RequestBody =>
    // {"postId":"1","userId":"1","content":"留言內容","parentId":""}
    @PostMapping("/create")
    public CommentResponse create(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        CommentResponse response = new CommentResponse();
        CommentBean commentBean = new CommentBean();
        PostBean postBean = new PostBean();
        MemberBean memberBean = new MemberBean();

        Long postId;
        Long userId;
        String content;
        Long parentId;
        CommentBean parentBean = null;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("postId")) {
                try {
                    postId = requestJson.getLong("postId");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("postId請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入postId");
                return response;
            }

            if (!requestJson.isNull("userId")) {
                try {
                    userId = requestJson.getLong("userId");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("userId請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入userId");
                return response;
            }

            if (!requestJson.isNull("content")) {
                content = requestJson.getString("content");
                if (content.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入content");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入content");
                return response;
            }

            if (!requestJson.isNull("parentId")
                    && requestJson.getString("parentId").length() != 0) {
                try {
                    parentId = requestJson.getLong("parentId");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("parentId請輸入整數");
                    return response;
                }
                parentBean = commentService.findById(parentId);
            } else {
                parentId = null;
            }
        }

        // 設定CommentBean
        {
            // setPostId
            try {
                postBean = postRepository.findById(postId).get();
                commentBean.setPostBean(postBean);
            } catch (NoSuchElementException e) {
                response.setSuccesss(false);
                response.setMessage("沒有這則貼文");
                return response;
            }
            // setUserId
            try {
                memberBean = memberRepository.findById(userId).get();
                commentBean.setMemberBean(memberBean);
            } catch (NoSuchElementException e) {
                response.setSuccesss(false);
                response.setMessage("沒有這個使用者");
                return response;
            }
            // setContent
            commentBean.setContent(content);
            // setPostId
            // 是否有這筆上層留言
            if (parentBean != null) {
                // 要驗證上層留言和這留言是否在同一個貼文中
                if (parentBean.getPostBean().getPostid() == postId) {
                    commentBean.setParentId(parentId);
                } else {
                    response.setSuccesss(false);
                    response.setMessage("上層留言不在這篇文章");
                    return response;
                }
            }
        }

        // 存入資料庫
        CommentBean bean = commentService.createComment(commentBean);

        // 設定response
        {
            // 將Bean映射到DTO用的
            CommentDTO dto = modelMapper.map(bean, CommentDTO.class);
            response.setSuccesss(true);
            response.setMessage("創建成功");
            response.setBean(dto);
        }

        return response;
    }

    // 刪除留言
    // 測試 http://localhost:8080/comment/delete
    // 測試 RequestBody => {"commentId":"1"}
    @DeleteMapping("/delete")
    public CommentResponse delete(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        CommentResponse response = new CommentResponse();

        Long commentId;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("commentId")) {
                try {
                    commentId = requestJson.getLong("commentId");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("commentId請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("request請輸入commentId");
                return response;
            }
        }

        // 刪除資料
        {
            if (commentService.deleteComment(commentId)) {
                response.setSuccesss(true);
                response.setMessage("已刪除這筆留言");
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆留言");
            }
        }

        return response;
    }

    // 更新留言
    // 測試 http://localhost:8080/comment/update
    // 測試 RequestBody => {"commentId":"1","content":"留言內容"}
    @PutMapping("/update")
    public CommentResponse update(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        CommentResponse response = new CommentResponse();

        Long commentId;
        String content;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("commentId")) {
                try {
                    commentId = requestJson.getLong("commentId");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("commentId請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入commentId");
                return response;
            }

            if (!requestJson.isNull("content")) {
                content = requestJson.getString("content");
                if (content.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入content");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入content");
                return response;
            }
        }

        // 更新留言
        {
            CommentBean bean = commentService.updateComment(commentId, content);
            if (bean != null) {
                // 將Bean映射到DTO用的
                CommentDTO dto = modelMapper.map(bean, CommentDTO.class);
                response.setSuccesss(true);
                response.setMessage("更新成功");
                response.setBean(dto);
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆ID");
            }
        }
        return response;
    }

    // 更新留言狀態
    // 測試 http://localhost:8080/comment/updateState
    // 測試 RequestBody => {"commentId":"1","commentState":"私人"}
    @PutMapping("/updateState")
    public CommentResponse updateState(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        CommentResponse response = new CommentResponse();

        Long commentId;
        String commentState;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("commentId")) {
                try {
                    commentId = requestJson.getLong("commentId");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("commentId請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入commentId");
                return response;
            }
            if (!requestJson.isNull("commentState")) {
                commentState = requestJson.getString("commentState");
                if (commentState.length() == 0) {
                    response.setSuccesss(false);
                    response.setMessage("請輸入commentState");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入commentState");
                return response;
            }
        }

        // 更新留言狀態
        {
            CommentBean commentBean = commentService.updateCommentState(commentId, commentState);
            if (commentBean != null) {
                // 將Bean映射到DTO用的
                CommentDTO dto = modelMapper.map(commentBean, CommentDTO.class);
                response.setSuccesss(true);
                response.setMessage("留言狀態更新成功");
                response.setBean(dto);
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆留言");
            }
        }

        return response;
    }

    // 用commentId查詢留言
    // 測試 http://localhost:8080/comment/findById
    // 測試 RequestBody => {"commentId":"1"}
    @GetMapping("/findById")
    public CommentResponse findById(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        CommentResponse response = new CommentResponse();

        Long commentId;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("commentId")) {
                try {
                    commentId = requestJson.getLong("commentId");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("commentId請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入commentId");
                return response;
            }
        }

        // 用commentId查詢留言
        {
            CommentBean commentBean = commentService.findById(commentId);
            if (commentBean != null) {
                // 將Bean映射到DTO用的
                CommentDTO dto = modelMapper.map(commentBean, CommentDTO.class);
                response.setSuccesss(true);
                response.setMessage("查尋成功");
                response.setBean(dto);
            } else {
                response.setSuccesss(false);
                response.setMessage("查不到這筆留言");
            }
        }

        return response;
    }

    // 用userid查詢根留言
    // 測試 http://localhost:8080/comment/findByUserid
    // 測試 RequestBody => {"userId":"1"}
    @GetMapping("/findByUserid")
    public CommentResponse findByUserid(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        CommentResponse response = new CommentResponse();

        Long userId;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("userId")) {
                try {
                    userId = requestJson.getLong("userId");
                    if (!memberRepository.existsById(userId)) {
                        response.setSuccesss(false);
                        response.setMessage("沒有這個成員");
                        return response;
                    }
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("userId請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入userId");
                return response;
            }
        }

        // 用userid查詢根留言
        {
            // 用userid查詢所有留言
            List<CommentBean> beans = commentService.findByUserid(userId);
            // 刪除有上層留言的資料
            for (int i = 0; i < beans.size(); i++) {
                if (beans.get(i).getParentId() != null) {
                    beans.remove(i);
                } else {
                    // 將Bean映射到DTO用的
                    CommentDTO dto = modelMapper.map(beans.get(i), CommentDTO.class);
                    response.getList().add(dto);
                }
            }

            if (beans.size() != 0) {
                response.setSuccesss(true);
                response.setMessage("查到資料");
            } else {
                response.setSuccesss(false);
                response.setMessage("沒查到資料");
            }

        }

        return response;
    }

    // 用parentId查詢留言
    // 測試 http://localhost:8080/comment/findByParentId
    // 測試 RequestBody => {"parentId":"1"}
    @GetMapping("/findByParentId")
    public CommentResponse findByParentId(@RequestBody String json) {
        JSONObject requestJson = new JSONObject(json);
        CommentResponse response = new CommentResponse();

        Long parentId;
        // 驗證request資料(防呆)
        {
            if (!requestJson.isNull("parentId")) {
                try {
                    parentId = requestJson.getLong("parentId");
                } catch (JSONException e) {
                    response.setSuccesss(false);
                    response.setMessage("parentId請輸入整數");
                    return response;
                }
            } else {
                response.setSuccesss(false);
                response.setMessage("請輸入parentId");
                return response;
            }
        }

        // 用parentId查詢留言
        {
            List<CommentBean> beans = commentService.findByParentId(parentId);
            if (beans.size() != 0) {
                for (int i = 0; i < beans.size(); i++) {
                    // 將Bean映射到DTO用的
                    CommentDTO dto = modelMapper.map(beans.get(i), CommentDTO.class);
                    response.getList().add(dto);
                }
                response.setSuccesss(true);
                response.setMessage("查到資料");
            } else {
                response.setSuccesss(false);
                response.setMessage("沒查到資料");
            }
        }

        return response;
    }

}
