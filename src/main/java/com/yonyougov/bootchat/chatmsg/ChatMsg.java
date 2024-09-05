package com.yonyougov.bootchat.chatmsg;

import com.yonyougov.bootchat.fw.base.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.lang.String;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "t_chat_msg"
)
@EqualsAndHashCode(
        callSuper = false
)
public class ChatMsg extends BaseEntity {
    @Column(
            name = "user_id"
    )
    private String userId;

    @Column
    private String role;

    @Column
    private String msg;
    @Column
    private String msgType;
    @Column
    private String uri;
    @Column
    private Integer size;

}
