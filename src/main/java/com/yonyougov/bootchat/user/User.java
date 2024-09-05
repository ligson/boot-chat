package com.yonyougov.bootchat.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yonyougov.bootchat.fw.base.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "t_user")
@Comment("用户表")
public class User extends BaseEntity {
    @JsonIgnore
    @Comment("密码")
    @Column
    private String password;
    private String name;
    private String code;
    private String email;
}
