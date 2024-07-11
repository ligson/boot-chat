package com.yonyougov.bootchat.base.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

//JpaRepository<User, String>, QuerydslPredicateExecutor<User>
@NoRepositoryBean
public interface CrudDao<E extends BaseEntity, PK extends Serializable> extends JpaRepository<E, PK>, QuerydslPredicateExecutor<E> {
}
