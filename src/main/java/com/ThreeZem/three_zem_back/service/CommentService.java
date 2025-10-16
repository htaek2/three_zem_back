package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.constant.ResponseMessage;
import com.ThreeZem.three_zem_back.data.dto.CommentDto;
import com.ThreeZem.three_zem_back.data.entity.Alert;
import com.ThreeZem.three_zem_back.data.entity.Comment;
import com.ThreeZem.three_zem_back.data.entity.Member;
import com.ThreeZem.three_zem_back.repository.AlertRepository;
import com.ThreeZem.three_zem_back.repository.CommentRepository;
import com.ThreeZem.three_zem_back.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AlertRepository alertRepository;
    private final MemberRepository memberRepository;

    public ResponseEntity<String> saveComment(CommentDto comment, Long memberId) {
        try {
            Alert alert = alertRepository.findById(comment.getRelatedAlertId()).orElse(null);
            Member member = memberRepository.findById(memberId).orElse(null);

            commentRepository.save(comment.toEntity(alert, member));

            return ResponseEntity.status(HttpStatus.OK).body(ResponseMessage.SUCCESS);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMessage.SERVER_ERROR);
        }
    }

    public ResponseEntity<List<CommentDto>> getAllComments() {
        try {
            List<Comment> comments = commentRepository.findAll();
            List<CommentDto> dtos = comments.stream().map(entity -> new CommentDto(
                    entity.getId(),
                    entity.getCreatedTime(),
                    entity.getRelatedAlert().getId(),
                    entity.getWritedMember().getId(),
                    entity.getContent()))
                    .toList();

            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<String> updateComment(Long id, CommentDto commentDto) {
        try {
            Alert alert = alertRepository.findById(commentDto.getRelatedAlertId()).orElse(null);
            Member member = memberRepository.findById(commentDto.getId()).orElse(null);

            Comment entity = commentDto.toEntity(alert, member);
            commentRepository.save(entity);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseMessage.SUCCESS);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<String> deleteComment(Long id) {
        try {
            if (commentRepository.existsById(id)) {
                commentRepository.deleteById(id);
                return ResponseEntity.status(HttpStatus.OK).body(ResponseMessage.SUCCESS);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMessage.CLIENT_ERROR);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
