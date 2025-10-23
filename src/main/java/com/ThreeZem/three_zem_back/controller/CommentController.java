package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.data.common.CustomUser;
import com.ThreeZem.three_zem_back.data.dto.CommentDto;
import com.ThreeZem.three_zem_back.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /// 코멘트 저장
    @PostMapping("/api/comment")
    public ResponseEntity<String> saveComment(@RequestBody CommentDto comment, Authentication auth) {
        return commentService.saveComment(comment, ((CustomUser)auth.getPrincipal()).getUserDbId());
    }

    /// 코멘트 전체 조회
    @GetMapping("/api/comments")
    public ResponseEntity<List<CommentDto>> getAllComments(Authentication auth) {
        return commentService.getAllComments();
    }

    /// 코멘트 수정
    @PatchMapping("/api/comment/{id}")
    public ResponseEntity<String> updateComment(@PathVariable Long id, @RequestBody CommentDto comment, Authentication auth) {
        return commentService.updateComment(id, comment);
    }

    /// 코멘트 삭제
    @DeleteMapping("/api/comment/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id, Authentication auth) {
        return commentService.deleteComment(id);
    }

}