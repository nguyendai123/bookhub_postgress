package com.thanhson.bookhup.service;

import com.thanhson.bookhup.model.Group;
import com.thanhson.bookhup.model.User;
import com.thanhson.bookhup.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    public Group createGroup(Group group, User createdByUser) {
        group.setCreateDate(LocalDateTime.now());
        group.setMemberCount(1);
        group.setCreatedUser(createdByUser);
        return groupRepository.save(group);
    }

    public void addMemberToGroup(Group group, User users) {
        group.getUsers().add(users);
        groupRepository.save(group);
    }

    public Group getGroupById(Long groupId) {
        return groupRepository.getById(groupId);
    }
}
