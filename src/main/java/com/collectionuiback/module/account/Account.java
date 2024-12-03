package com.collectionuiback.module.account;

import com.collectionuiback.module.account.convert.PasswordConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Convert(converter = PasswordConverter.class)
    private String password;

    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountRole role;

    @Builder
    public Account(String email, String name, String password, String picture, AccountRole role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.picture = picture;
        this.role = role;
    }

    public Account updateProfile(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }

    public String getRoleName() {
        return role.name();
    }
}
