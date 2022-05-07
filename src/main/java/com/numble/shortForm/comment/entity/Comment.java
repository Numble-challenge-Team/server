package com.numble.shortForm.comment.entity;

import com.numble.shortForm.time.BaseTime;
import com.numble.shortForm.user.entity.Users;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Comment extends BaseTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_id")
    private Long  id;

    @Setter
    private String title;

    @Setter
    private String context;

    @Column(name = "video_id")
    private Long videoId;

    @Column(name = "comment_seq")
    private Long commentSeq;

    @ColumnDefault("false")
    private boolean isBlock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    @Override
    public LocalDateTime getCreated_at() {
        return super.getCreated_at();
    }

    @Override
    public LocalDateTime getUpdated_at() {
        return super.getUpdated_at();
    }

    @Builder
    public Comment(Long id, String title, String context, Long videoId, Long commentSeq, boolean isBlock, Users users) {
        this.id = id;
        this.title = title;
        this.context = context;
        this.videoId = videoId;
        this.commentSeq = commentSeq;
        this.isBlock = isBlock;
        this.users = users;
    }
}
