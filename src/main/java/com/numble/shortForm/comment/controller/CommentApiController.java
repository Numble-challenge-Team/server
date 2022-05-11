package com.numble.shortForm.comment.controller;

import com.numble.shortForm.comment.dto.response.CommentNumberResponse;
import com.numble.shortForm.comment.dto.response.CommentResponse;
import com.numble.shortForm.comment.dto.response.IsCommentLike;
import com.numble.shortForm.comment.entity.Comment;
import com.numble.shortForm.comment.repository.CommentRepository;
import com.numble.shortForm.comment.service.CommentService;
import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.exception.ErrorResponse;
import com.numble.shortForm.response.Response;
import com.numble.shortForm.security.AuthenticationFacade;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@Api(tags = "댓글작성 api")
@RestController
@RequestMapping("/api/v1/comments/")
@RequiredArgsConstructor
public class CommentApiController {

    private final AuthenticationFacade authenticationFacade;
    private final CommentService commentService;
    private final UsersRepository usersRepository;
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
    @PostMapping("/auth/comment")
    public ResponseEntity<?> createComment(@RequestBody Comment comment){

        String userEmail = authenticationFacade.getAuthentication().getName();

        Users user = usersRepository.findByEmail(userEmail).orElseThrow(()->
                new CustomException(ErrorCode.NOT_FOUND_USER, "해당유저에 해당하는 토큰을 찾을 수 없습니."));
        commentService.createComment(comment, user.getId());


        return ResponseEntity.ok().body("ok");
    }
    @ApiOperation(value = "video에 해당되는 댓글 불러오기", notes="<big>해당 비디오에 댓글이 있을 경우 List형태로 반환</big>")
    @ApiImplicitParam(name = "videoId", value = "해당비디오의 id값")
    @ApiResponses(
            {@ApiResponse(code = 200, message = "ok", response = CommentNumberResponse.class),
             @ApiResponse(code = 500, message = "서버 에러", response = ErrorResponse.class)
            }
    )
    @GetMapping("/{videoId}")
    public List<CommentNumberResponse> commentResponseList(@PathVariable("videoId") Long videoId){
        List<CommentNumberResponse> commentList = commentService.testComment(videoId);


        return commentList;
    }

    @ApiOperation(value = "comment에 해당되는 댓글 불러오기", notes = "<big>해당 댓글에 대댓글이 있을 경우 List형태로 반환</big>")
    @ApiImplicitParam(name = "commentSeq", value = "해당 댓글의 id값")
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok", response = CommentResponse.class),
            @ApiResponse(code = 500, message = "서버 에러", response = ErrorResponse.class)
    })
    @GetMapping("/recomment/{commentSeq}")
    public List<CommentResponse> recommentList(@PathVariable("commentSeq")long commentSeq){
        List<CommentResponse> recommentlist = commentService.reComment(commentSeq);

        return recommentlist;
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
    @PatchMapping("/auth/comment")
    public ResponseEntity<?> updateComment(@RequestBody Comment comment){
        String userEmail = authenticationFacade.getAuthentication().getName();

        Users user = usersRepository.findByEmail(userEmail).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_USER, "해당유저를 찾을 수가 없습니다."));

        boolean updateCheck = commentService.updateComment(comment, user.getId());

        if(!updateCheck) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "댓글 작성자가 아닙니다.");
        }
        return ResponseEntity.ok().body("ok");
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
    @DeleteMapping("/auth/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId")Long commentId){
        String userEmail = authenticationFacade.getAuthentication().getName();

        Users user = usersRepository.findByEmail(userEmail).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_USER,"해당유저를 찾을 수가 없습니다"));

        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new CustomException(ErrorCode.BAD_REQUEST_PARAM,"잘못된 요청입니다."));

        if(user.getId() != comment.getUsers().getId()){
            throw new CustomException(ErrorCode.ACCESS_DENIED,"댓글 작성자가 아닙니다.");
        }

        commentService.deleteComment(commentId, user.getId());

        return ResponseEntity.ok().body("ok");
    }
    @ApiOperation(value = "comment 좋아요", notes = "<big>comment 좋아요 누를 때 accessToken 필요")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bearer token", value = "accessToken 값"),
            @ApiImplicitParam(name = "commentId", value = "댓글의 id값")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 403, message = "권한이 없음", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "유저 NOT FOUND", response = ErrorResponse.class)
    })
    @PostMapping("/auth/like/{commentId}")
    public ResponseEntity<?> requestCommentLike(@PathVariable("commentId")Long commentId){
        //System.out.println("commentId = " + commentId);
        String userEmail = authenticationFacade.getAuthentication().getName();


        boolean LikeCheck = commentService.requestLikeComeent(userEmail, commentId);


        return ResponseEntity.ok().body(new IsCommentLike(LikeCheck));
    }

    /*@GetMapping("test/{videoId}")
    public List<commentNumberResponse> testingApi(@PathVariable("videoId") Long videoId){
        List<commentNumberResponse> list = commentService.testComment(videoId);

        return list;
    }*/
}
