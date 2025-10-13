package com.ThreeZem.three_zem_back.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "related_alert_id")
    private Alert relatedAlert;

    @ManyToOne
    @JoinColumn(name = "writed_member_id")
    private Member writedMemberId;

    private String content;

    private LocalDateTime createdTime;

}
