package com.numble.shortForm.comment.entity;

import com.numble.shortForm.time.BaseTime;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.video.entity.VideoLike;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@DynamicUpdate
public class Comment extends BaseTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_id")
    private Long  id;

    private String title;

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


    @OneToMany(mappedBy ="comment",cascade = CascadeType.REMOVE)
    private List<CommentLike> commentLikes = new ArrayList<>() ;

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

    public void updateComment(String title, String context) {
        if(!title.isEmpty())
            this.title =title;

        if(!context.isEmpty())
            this.context =context;

    }
}
