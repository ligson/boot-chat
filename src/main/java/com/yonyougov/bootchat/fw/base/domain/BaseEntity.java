package com.yonyougov.bootchat.fw.base.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
@Setter
public class BaseEntity {
    @Id
    //@TableGenerator(name = "global_id_gen", allocationSize = 1)
    //@GeneratedValue(strategy = GenerationType.TABLE, generator = "global_id_gen")
    @GeneratedValue(generator = "cqtIdGenerator")
    @GenericGenerator(name = "cqtIdGenerator", type = CqtIdGenerator.class)
    @Comment("主键")
    private String id;

    //"创建人"
    @CreatedBy
    @Column(name = "create_by")
    @Comment("创建人")
    private String createBy;

    //"创建时间"
    @CreatedDate
    @Column(name = "create_time")
    @Comment("创建时间")
    private Date createTime;

    //"修改人"
    @LastModifiedBy
    @Column(name = "last_modified_by")
    @Comment("修改人")
    private String lastModifiedBy;

    //"更新时间"
    @LastModifiedDate
    @Column(name = "update_time")
    @Comment("更新时间")
    private Date updateTime;
}
