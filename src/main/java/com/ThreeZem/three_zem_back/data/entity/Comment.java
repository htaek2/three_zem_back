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
    @JoinColumn(name = "related_alert_id", nullable = true)
    private Alert relatedAlert;

    @ManyToOne
    @JoinColumn(name = "writed_member_id", nullable = false)
    private Member writedMember;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

}
