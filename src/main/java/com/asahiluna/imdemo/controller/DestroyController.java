package com.asahiluna.imdemo.controller;

import com.asahiluna.imdemo.service.TimelineService;
import com.asahiluna.imdemo.service.UserAndGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/Destroy")
public class DestroyController {

    @Autowired
    TimelineService timelineService;
    @Autowired
    UserAndGroupService userAndGroupService;

    @DeleteMapping("/deleteAllofTimeline")
    public List<String> deleteAllofTimeline() {
        return timelineService.deleteAllofTimeline();
    }

    @DeleteMapping("/deleteAllofUserAndGroup")
    public List<String> deleteAllofUserAndGroup() {
        return userAndGroupService.deleteAllofUserAndGroup();
    }
}
