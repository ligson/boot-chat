package com.yonyougov.bootchat.base.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;


@Repository
public interface UserDao  extends JpaRepository<User, String>, QuerydslPredicateExecutor<User> {

}
