package com.example.demo.domain.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="user")
public class UserCredentials {

    @Id
    @OneToOne
    @JoinColumn(name = "user_t_id")
    public User user;

    @Column(nullable = false)
    public String hashed_password;
}
