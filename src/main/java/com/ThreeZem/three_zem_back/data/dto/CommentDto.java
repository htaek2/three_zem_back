package com.ThreeZem.three_zem_back.data.dto;

import com.ThreeZem.three_zem_back.data.entity.Alert;
import com.ThreeZem.three_zem_back.data.entity.Comment;
import com.ThreeZem.three_zem_back.data.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private LocalDateTime createdTime;
    private Long relatedAlertId;
    private Long writedMemberId;
    private String content;

    public Comment toEntity(Alert alert, Member member) {
        return new Comment(id, alert, member, content, createdTime);
    }
}
