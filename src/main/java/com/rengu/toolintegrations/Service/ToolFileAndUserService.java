package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.ToolFileAndUser;
import com.rengu.toolintegrations.Repository.ToolFileAndUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName ToolFileAndUserService
 * @Description TODO
 * @Author yyc
 * @Date 2020/8/26 19:08
 * @Version 1.0
 */
@Service
public class ToolFileAndUserService {
    private final ToolFileAndUserRepository toolFileAndUserRepository;

    public ToolFileAndUserService(ToolFileAndUserRepository toolFileAndUserRepository) {
        this.toolFileAndUserRepository = toolFileAndUserRepository;
    }

    //用户下载工具，记录一条下载记录
    public List<ToolFileAndUser> saveToolFileAndUser(String[] toolFileIds, String userId) {
        List list = Arrays.asList(toolFileIds);
        List<ToolFileAndUser> list1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (!hasExistByToolFileIdAndUserId(toolFileIds[i], userId)) {
                ToolFileAndUser toolFileAndUser = new ToolFileAndUser();
                toolFileAndUser.setToolFileId(toolFileIds[i]);
                toolFileAndUser.setUserId(userId);
                toolFileAndUserRepository.save(toolFileAndUser);
                list1.add(toolFileAndUser);
            }
        }
        return list1;
    }

    //判断下载的文件和用户是否同时存在
    public boolean hasExistByToolFileIdAndUserId(String toolFileId, String userId) {
        if (StringUtils.isEmpty(toolFileId) || StringUtils.isEmpty(userId)) {
            return false;
        }
        return toolFileAndUserRepository.existsByToolFileIdAndUserId(toolFileId, userId);
    }
}
