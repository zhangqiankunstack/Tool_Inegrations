package com.rengu.toolintegrations.Entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * Author: Zhangqiankun
 * Date: 2020/8/27 14:47
 */
@Data
@Entity
public class DesignEntity implements Serializable {
    @Id
    private String id = UUID.randomUUID().toString();

}
