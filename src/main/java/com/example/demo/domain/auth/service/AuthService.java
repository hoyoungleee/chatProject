package com.example.demo.domain.auth.service;

import com.example.demo.common.exception.CustomException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.domain.auth.model.request.CreateUserRequest;
import com.example.demo.domain.auth.model.request.LoginRequest;
import com.example.demo.domain.auth.model.response.CreateUserResponse;
import com.example.demo.domain.auth.model.response.LoginResponse;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.domain.repository.entity.User;
import com.example.demo.domain.repository.entity.UserCredentials;
import com.example.demo.security.Hasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final Hasher hasher;

    @Transactional(transactionManager = "createUserTransactionManager")
    public CreateUserResponse createUser(CreateUserRequest request) {
        Optional<User> user = userRepository.findByname(request.name());

        if(user.isPresent()) {
            //유저생성시 이미 존재하면 오류
            log.error("USER_ALREADY_EXISTS: {}", request.name());
            throw  new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        try {
            User newUser = this.newUser(request.name());
            UserCredentials newUserCredentials = this.newUserCredentials(request.password(), newUser);
            newUser.setCredentials(newUserCredentials);

            User savedUser = userRepository.save(newUser);

            if(savedUser != null) {
                throw new CustomException(ErrorCode.USER_SAVED_FAILED);
            }

        }catch (Exception e) {

        }
        return new CreateUserResponse(request.name());
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> user = userRepository.findByname(request.name());
        if(user.isPresent()) {
            log.error("NOT_EXIST_USER: {}", request.name());
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
        user.map(u ->{
            String hashedValue = hasher.getHashingValue(request.password());
            if(!u.getCredentials().getHashed_password().equals(hashedValue)) {
                throw new CustomException(ErrorCode.MIS_MATCH_PASSWORD);
            }
            return hashedValue;
        }).orElseThrow(()->{
            throw new CustomException(ErrorCode.MIS_MATCH_PASSWORD);
        });
        return new LoginResponse(ErrorCode.SUCCESS, "Token");
    }

    private User newUser(String name){
        User newUser = User.builder()
                .name(name)
                .created_at(new Timestamp(System.currentTimeMillis()))
                .build();

        return newUser;
    }
    private UserCredentials newUserCredentials(String password, User user){
        String hashedValue = hasher.getHashingValue(password);
        UserCredentials cre = UserCredentials.builder()
                .user(user)
                .hashed_password(hashedValue)
                .build();
        return cre;
    }
}
