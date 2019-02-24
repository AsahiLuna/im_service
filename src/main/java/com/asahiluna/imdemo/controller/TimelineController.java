package com.asahiluna.imdemo.controller;


import com.alicloud.openservices.tablestore.timeline.message.IMessage;
import com.alicloud.openservices.tablestore.timeline.message.StringMessage;
import com.asahiluna.imdemo.entity.Ok;
import com.asahiluna.imdemo.service.TimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/Timeline")
public class TimelineController {

    @Autowired
    TimelineService timelineService;

    @GetMapping("/getSyncMessage")
    public List<StringMessage> getSyncMessage(String user, @RequestParam(defaultValue = "0") long lastSequenceID) {
        return timelineService.getSyncMessage(user, lastSequenceID);
    }

    @PostMapping("/sendMessage")
    public Ok sendMessage(String from, String to, String message, String type) {

        IMessage iMessage = new StringMessage(message);
        iMessage.addAttribute("from", from);
        iMessage.addAttribute("to", to);
        iMessage.addAttribute("messageType", type);
        iMessage.addAttribute("time", LocalDateTime.now().toString());
        if (type.trim().equals("group")) {
            timelineService.sendGroupMessage(to, iMessage);
        } else {
            timelineService.sendPersonalMessage(from, to, iMessage);
        }
        return Ok.newOk();
    }
}
