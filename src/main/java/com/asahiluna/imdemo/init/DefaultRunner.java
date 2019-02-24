package com.asahiluna.imdemo.init;

import com.asahiluna.imdemo.service.TimelineService;
import com.asahiluna.imdemo.service.UserAndGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultRunner implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TimelineService timelineService;
    @Autowired
    UserAndGroupService userAndGroupService;
    @Override
    public void run(String...args) throws Exception {
        logger.info("Start init");

        userAndGroupService.createTable();

    }


}