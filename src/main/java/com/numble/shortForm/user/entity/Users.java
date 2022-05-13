package com.numble.shortForm.user.entity;

import com.numble.shortForm.comment.entity.Comment;
import com.numble.shortForm.comment.entity.CommentLike;
import com.numble.shortForm.report.entity.Report;
import com.numble.shortForm.time.BaseTime;
import com.numble.shortForm.user.dto.request.UpdateUserRequestDto;
import com.numble.shortForm.user.dto.request.UserRequestDto;
import com.numble.shortForm.video.entity.Video;
import com.numble.shortForm.video.entity.VideoLike;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@DynamicUpdate
public class Users extends BaseTime implements UserDetails {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name ="users_id")
    private Long id;

    private String email;

    private String password;

    private String nickname;

    @Embedded
    private ProfileImg profileImg;

    @OneToMany(mappedBy = "users",cascade = CascadeType.REMOVE)
    private List<Video> videos = new ArrayList<>();

    @OneToMany(mappedBy = "users",cascade = CascadeType.REMOVE)
    private List<VideoLike> videoLikes  = new ArrayList<>();

    @OneToMany(mappedBy = "users",cascade = CascadeType.REMOVE)
    private List<CommentLike> commentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "users",cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "users",cascade = CascadeType.REMOVE)
    private List<Report> reports = new ArrayList<>();


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(joinColumns = @JoinColumn(name = "USERS_ID"))
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    private List<String> roles = new ArrayList<>();

    @Builder
    public Users(String email, String password, String nickname, List<String> roles,ProfileImg profileImg) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.roles = roles;
        this.profileImg =profileImg;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public void updateProfile(String nickname, ProfileImg profileImg) {

        if (nickname != null)
            this.nickname =nickname;
        if (profileImg.getName() != null && profileImg.getUrl() != null)
            this.profileImg =profileImg;

    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
