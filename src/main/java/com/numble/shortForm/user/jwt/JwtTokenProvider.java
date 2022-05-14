package com.numble.shortForm.user.jwt;

import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.user.dto.request.UserRequestDto;
import com.numble.shortForm.user.dto.response.UserResponseDto;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY ="auth";
    private static final String BEARER_TYPE ="Bearer";
//    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 10 *1000L;

    private static final Long ACCESS_TOKEN_EXPIRE_TIME =  60 * 60 *1000L; //30 Minutes
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 *1000L; //7 days

    private final Key key;

    private UsersRepository usersRepository;


    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,UsersRepository usersRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.usersRepository = usersRepository;

    }

    // 유저정보기반 accessToken , reFreshToken 생성
    public UserResponseDto.TokenInfo generateToken(Authentication authentication) {
        //권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        log.info("authentication 권한 {}",authorities);

        Long now = (new Date()).getTime();

        //Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY,authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();

        //RefreshToken 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();

        Users users = usersRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->new CustomException(ErrorCode.NOT_FOUND_USER, "Error"));

        return UserResponseDto.TokenInfo.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .nickname(users.getNickname())
                .userId(users.getId())
                .profileImg(users.getProfileImg())
                .build();
    }



    // Jwt token 복호화하여 토근에 들어있는 정보를 꺼냄
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);


        //문제 발생 추후에 수정
        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new CustomException(ErrorCode.NONE_AUTHENTICATION_TOKEN,"권한 정보가 없는 토큰입니다.");
        }
        // claims 에서 권한 정보 가져오기

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // entity의 유저가 아닌 userdetails
        UserDetails principal = new User(claims.getSubject(),"",authorities);
        return new UsernamePasswordAuthenticationToken(principal,"",authorities);
    }

    private Claims parseClaims(String accessToken) {

        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            log.info("만료 체크");
            return e.getClaims();
//            throw new CustomException(ErrorCode.EXPIRE_TOKEN);
        }
    }


    public Long getExpiration(String accessToken) {
        //accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();
        Long now = new Date().getTime();

        return (expiration.getTime() - now);
    }

    public boolean validationToken(String token) throws ExpiredJwtException{
        log.info("access-token : {}" ,token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token {}", e);
        }
        catch (UnsupportedJwtException e) {
            log.info("UnSupproted JWT Token {}", e);

        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty {}",e);

        }
        return false;
    }

    public boolean validationTokenIn(String token) throws ExpiredJwtException{
        log.info("access-token : {}" ,token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token {}", e);
            throw new CustomException(ErrorCode.ACCESS_DENIED);

        }catch (ExpiredJwtException e){
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        catch (UnsupportedJwtException e) {
            log.info("UnSupproted JWT Token {}", e);
            throw new CustomException(ErrorCode.ACCESS_DENIED);

        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty {}",e);

        }
        return false;
    }

    public boolean revalidationToken(String token, HttpServletRequest request) {

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token {}", e);
        }catch (ExpiredJwtException e){
            request.setAttribute("exception",ErrorCode.EXPIRED_TOKEN.getDetail());
        }
        catch (UnsupportedJwtException e) {
            log.info("UnSupproted JWT Token {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty {}",e);
        }
        log.info("해당하지 않음");
        return false;
    }
    public boolean refreshValidation(String token) {

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token {}", e);
        }catch (ExpiredJwtException e){
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRE);
        }
        catch (UnsupportedJwtException e) {
            log.info("UnSupproted JWT Token {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty {}",e);
        }
        log.info("해당하지 않음");
        return false;
    }
}
