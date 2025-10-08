package com.bookhup.controller;

import com.bookhup.model.Group;
import com.bookhup.model.User;
import com.bookhup.service.GroupService;
import com.bookhup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")

public class GroupController {
    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Group> createGroup(@RequestBody Group group, @RequestBody User user) {
        Group newGroup = groupService.createGroup(group,user);
        return new ResponseEntity<>(newGroup, HttpStatus.CREATED);
    }

    @PostMapping("/{groupId}/addMember/{userId}")
    public ResponseEntity<String> addMemberToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        Group group = groupService.getGroupById(groupId);
        User member = userService.getUserById(userId);

        if (group != null && member != null) {
            groupService.addMemberToGroup(group, member);
            return new ResponseEntity<>("Member added to group", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Group or member not found", HttpStatus.NOT_FOUND);
        }
    }

}
