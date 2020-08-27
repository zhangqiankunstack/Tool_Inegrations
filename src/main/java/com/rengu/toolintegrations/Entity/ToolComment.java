package com.rengu.toolintegrations.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * @ClassName ToolComment
 * @Description TODO 工具评论
 * @Author yyc
 * @Date 2020/8/26 15:53
 * @Version 1.0
 */
@Entity
@Data
public class ToolComment implements Serializable {
    @Id
    private String id= UUID.randomUUID().toString();
    private int starGrade;  //评论星级
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "longtext",length = 0)
    private String description;  //评论
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime = new Date();
    private double average;  //平均分
    private String toolFileId;  //下载工具文件id
    @ManyToOne
    private UserEntity userEntity;


}
