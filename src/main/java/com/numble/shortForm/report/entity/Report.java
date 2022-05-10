package com.numble.shortForm.report.entity;

import com.numble.shortForm.time.BaseTime;
import com.numble.shortForm.user.entity.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@ToString
public class Report extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @Column(name = "video_id")
    private Long videoId;

    @Column(name = "comment_id")
    private  Long commentID;

    @Override
    public LocalDateTime getCreated_at() {
        return super.getCreated_at();
    }

    @Builder
    public Report(Long id, Users users, Long videoId, Long commentID) {
        this.id = id;
        this.users = users;
        this.videoId = videoId;
        this.commentID = commentID;
    }
}
