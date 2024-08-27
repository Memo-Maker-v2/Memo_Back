package com.example.memo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "saved_video")
public class SavedVideoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long savedId;

    @ManyToOne
    @JoinColumn(name = "video_id", referencedColumnName = "videoId")
    private VideoEntity video;

    @ManyToOne
    @JoinColumn(name = "member_email", referencedColumnName = "memberEmail")
    private MemberEntity member;

    @Column(name = "saved_date")
    private LocalDate savedDate;
}
