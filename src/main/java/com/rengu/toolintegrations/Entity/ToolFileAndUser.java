package com.rengu.toolintegrations.Entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * @ClassName ToolFileAndUser
 * @Description TODO 用户下载工具记录表
 * @Author yyc
 * @Date 2020/8/26 16:31
 * @Version 1.0
 */
@Entity
@Data
public class ToolFileAndUser implements Serializable {
    @Id
    private String id = UUID.randomUUID().toString();
    private String toolId;
    private String userId;
}
