package com.yonyougov.bootchat.minio.file;


import com.yonyougov.bootchat.fw.base.domain.CqtIdGenerator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "t_file_msg")
public class FileMsg {

        @Id
        //@TableGenerator(name = "global_id_gen", allocationSize = 1)
        //@GeneratedValue(strategy = GenerationType.TABLE, generator = "global_id_gen")
        @GeneratedValue(generator = "cqtIdGenerator")
        @GenericGenerator(name = "cqtIdGenerator", type = CqtIdGenerator.class)
        @Comment("主键")
        private String id;

        @Size(max = 200)
        @Column(name = "file_name", length = 200)
        private String fileName;

        @Size(max = 100)
        @Column(name = "upload_id", length = 100)
        private String uploadId;

        @Column(name = "chunks")
        private Integer chunks;

        @Size(max = 50)
        @Column(name = "config_code", length = 50)
        private String configCode;

        @Column(name = "is_upload_complete")
        private Integer isUploadComplete;

        @Size(max = 2000)
        @Column(name = "local_directory", length = 2000)
        private String localDirectory;

        @Column(name = "chunk")
        private Integer chunk;

        @Size(max = 200)
        @Column(name = "type", length = 200)
        private String type;

        @Column(name = "file_size")
        private Integer fileSize;

        @Size(max = 1000)
        @Column(name = "remark", length = 1000)
        private String remark;

        @Size(max = 100)
        @Column(name = "hash_code", length = 100)
        private String hashCode;

        @Size(max = 20)
        @Column(name = "file_type", length = 20)
        private String fileType;

        @Size(max = 50)
        @Column(name = "file_id", length = 50)
        private String fileId;

        @NotNull
        @ColumnDefault("CURRENT_TIMESTAMP")
        @Column(name = "pubts", nullable = false)
        private Instant pubts;

        @ColumnDefault("0")
        @Column(name = "dr")
        private Short dr;

        @Size(max = 36)
        @Column(name = "ytenant_id", length = 36)
        private String ytenantId;

        @Size(max = 100)
        @Column(name = "business_id", length = 100)
        private String businessId;

        @Column(name = "modify_time")
        private Instant modifyTime;

        @Size(max = 64)
        @Column(name = "creator", length = 64)
        private String creator;

        @Size(max = 64)
        @Column(name = "modifier", length = 64)
        private String modifier;

        @Column(name = "create_time")
        private Instant createTime;

}