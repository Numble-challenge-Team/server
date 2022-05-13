package com.numble.shortForm.comment.controller;

import com.numble.shortForm.comment.dto.request.ChildCommentRequestDto;
import com.numble.shortForm.comment.dto.request.CommentRequestDto;
import com.numble.shortForm.comment.dto.response.OriginalComment;
import com.numble.shortForm.comment.dto.response.CommentResponse;
import com.numble.shortForm.comment.repository.CommentRepository;
import com.numble.shortForm.comment.service.CommentLikeService;
import com.numble.shortForm.comment.service.CommentService;
import com.numble.shortForm.config.security.UserLibrary;
import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.exception.ErrorResponse;
import com.numble.shortForm.response.Response;
import com.numble.shortForm.config.security.AuthenticationFacade;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.video.dto.response.IsLikeResponse;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/comments/")
@RequiredArgsConstructor
public class CommentApiController {

    private final AuthenticationFacade authenticationFacade;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final UsersRepository usersRepository;
    private final UserLibrary userLibrary;
    private final CommentRepository commentRepository;

    @ApiOperation(value = "댓글작성", notes = "<big>댓글 작성에 성공하면, ok 반환</big>")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "comment", value = "댓글을 작성하기 위한 json"),
            @ApiImplicitParam(name = "bearer token", value = "댓글을 작성하기위한 권한요청을위한 access토큰")}
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok", response = Response.class),
            @ApiResponse(code = 404, message = "유저 NOT FOUND 오류", response = ErrorResponse.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "서버 내부 에러", response = ErrorResponse.class)}
    )
    @PostMapping("/create")
    public ResponseEntity<?> createComment(@RequestBody CommentRequestDto commentRequestDto){

        String userEmail = authenticationFacade.getAuthentication().getName();

        commentService.createComment(commentRequestDto,userEmail);


        return ResponseEntity.ok().body("댓글 생성완료");
    }

    @ApiOperation(value = "대댓글작성", notes = "")
    @PostMapping("/createChild")
    public ResponseEntity<?> createComment(@RequestBody ChildCommentRequestDto commentRequestDto){

        String userEmail = authenticationFacade.getAuthentication().getName();

        commentService.createChildComment(commentRequestDto,userEmail);


        return ResponseEntity.ok().body("댓글 생성완료");
    }

    @PostMapping("/like/{commentId}")
    public ResponseEntity<?> requestLike(@PathVariable("commentId")Long commentId) {
        String userEmail = authenticationFacade.getAuthentication().getName();

        boolean bol = commentLikeService.requestLikeComment(userEmail, commentId);


        return ResponseEntity.ok().body(new IsLikeResponse(bol));
    }



    @ApiOperation(value = "video에 해당되는 댓글 불러오기", notes="<big>해당 비디오에 댓글이 있을 경우 List형태로 반환</big>")
    @ApiImplicitParam(name = "videoId", value = "해당비디오의 id값")
    @ApiResponses(
            {@ApiResponse(code = 200, message = "ok", response = OriginalComment.class),
                    @ApiResponse(code = 500, message = "서버 에러", response = ErrorResponse.class)
            }
    )
    @GetMapping("/commentList/{videoId}")
    public List<OriginalComment> CommentResponseList(@PathVariable("videoId") Long videoId){

        Long userId = userLibrary.retrieveUserId();
        return commentService.getCommentList(videoId,userId);

    }

    @ApiOperation(value = "comment에 해당되는 댓글 불러오기", notes = "<big>해당 댓글에 대댓글이 있을 경우 List형태로 반환</big>")
    @ApiImplicitParam(name = "commentSeq", value = "해당 댓글의 id값")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok", response = CommentResponse.class),
            @ApiResponse(code = 500, message = "서버 에러", response = ErrorResponse.class)
    })
    @GetMapping("/getChild/{commentId}")
    public List<OriginalComment> recommentList(@PathVariable("commentId")Long commentId){
        Long userId = userLibrary.retrieveUserId();
        return  commentService.getChildList(commentId,userId);


    }

    @ApiOperation(value = "comment 수정하기", notes = "<big>댓글을 수정할때 accessToken 입력필요</big>")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bearer Token", value = "accessToken값"),
            @ApiImplicitParam(name = "comment", value = "변경할 제목과 내용에 해당하는 json")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 403, message = "권한이 없음", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "유저 NOT FOUND", response = ErrorResponse.class)
    })
    @PatchMapping("/update")
    public ResponseEntity<?> updateComment(@RequestBody ChildCommentRequestDto childCommentRequestDto){
        String userEmail = authenticationFacade.getAuthentication().getName();

        Users user = usersRepository.findByEmail(userEmail).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_USER, "해당유저를 찾을 수가 없습니다."));

        commentService.updateComment(user,childCommentRequestDto);

        return ResponseEntity.ok().body("변경완료");
    }
    @ApiOperation(value = "comment 삭제하기", notes = "<big>댓글을 삭제할때 accessToken 필요")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bearer token", value = "accessToken 값"),
            @ApiImplicitParam(name = "commentId", value = "해당댓글의 id값")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 403, message = "권한이 없음", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "유저 NOT FOUND", response = ErrorResponse.class)
    })
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId")Long commentId){

        Long userId = userLibrary.retrieveUserId();

        boolean b = commentService.deleteComment(commentId,userId);

        Map<String,String> result = new HashMap<>();

        if(b == true)
            result.put("complete","true");
        else{
            result.put("complete","false");
        }

        return ResponseEntity.ok().body(result);
    }


}
