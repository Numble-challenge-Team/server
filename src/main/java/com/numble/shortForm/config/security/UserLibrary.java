package com.numble.shortForm.config.security;

import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserLibrary {

    private final AuthenticationFacade authenticationFacade;
    private final UsersRepository usersRepository;


    public Long retrieveUserId() {
        String userEmail = authenticationFacade.getAuthentication().getName();
        if(userEmail.equals("anonymousUser")){
            return 0L;
        }
        Users users = usersRepository.findByEmail(userEmail).orElseThrow(()->{
            throw new CustomException(ErrorCode.NOT_FOUND_USER,"유저가 조회되지 않습니다.");
        });
        return users.getId();
    }
}
