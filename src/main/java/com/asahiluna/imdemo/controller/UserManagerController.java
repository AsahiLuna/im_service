package com.asahiluna.imdemo.controller;

import com.asahiluna.imdemo.entity.Group;
import com.asahiluna.imdemo.entity.Ok;
import com.asahiluna.imdemo.entity.User;
import com.asahiluna.imdemo.service.UserAndGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/UserAndGroup")
public class UserManagerController {


    @Autowired
    UserAndGroupService userAndGroupService;


    @GetMapping("/listGroupMembers")
    public List<String> listGroupMembers(String groupName) {
        return userAndGroupService.listGroupMembers(groupName);
    }


    @PostMapping("/addUser")
    public Ok addUser(User user) {
        userAndGroupService.addUser(user);
        return Ok.newOk();
    }

    @PostMapping("/existUser")
    public boolean existUser(String userName) {
        return userAndGroupService.existUser(userName);
    }


    @PostMapping("/addGroup")
    public Ok addGroup(Group group) {
        userAndGroupService.addGroup(group);
        return Ok.newOk();
    }

    @PostMapping("/userJoinGroup")
    public Ok userJoinGroup(String userName, String groupName) {
        userAndGroupService.userJoinGroup(userName, groupName);
        return Ok.newOk();

    }

    @PostMapping("/userLeaveGroup")
    public Ok userLeaveGroup(String userName, String groupName) {
        userAndGroupService.userLeaveGroup(userName, groupName);
        return Ok.newOk();
    }


}
