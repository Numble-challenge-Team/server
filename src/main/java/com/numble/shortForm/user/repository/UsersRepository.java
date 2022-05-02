package com.numble.shortForm.user.repository;

import com.numble.shortForm.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users,Long>,UserCustomRepository{


    Optional<Users> findByEmail(String email);


    Optional<Users> findByNickname(String nickname);

    Integer deleteByEmail(String userEmail);
}
