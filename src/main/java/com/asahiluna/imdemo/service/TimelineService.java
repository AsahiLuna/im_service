package com.asahiluna.imdemo.service;

import com.alibaba.fastjson.JSON;
import com.alicloud.openservices.tablestore.timeline.ScanParameter;
import com.alicloud.openservices.tablestore.timeline.ScanParameterBuilder;
import com.alicloud.openservices.tablestore.timeline.Timeline;
import com.alicloud.openservices.tablestore.timeline.TimelineEntry;
import com.alicloud.openservices.tablestore.timeline.message.IMessage;
import com.alicloud.openservices.tablestore.timeline.message.StringMessage;
import com.alicloud.openservices.tablestore.timeline.store.DistributeTimelineConfig;
import com.alicloud.openservices.tablestore.timeline.store.DistributeTimelineStore;
import com.asahiluna.imdemo.repository.TableStoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class TimelineService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserAndGroupService userAndGroupService;

    @Autowired
    TableStoreRepository tableStoreRepository;

    @Value("${aliyun.tablestore.endpoint}")
    private String endpoint;
    @Value("${aliyun.tablestore.AccessKeyID}")
    private String accessId;
    @Value("${aliyun.tablestore.AccessKeySecret}")
    private String accessKey;
    @Value("${aliyun.tablestore.instanceName}")
    private String instanceName;
    @Value("${im.syncTable}")
    private String syncTable;
    @Value("${im.storeTable}")
    private String storeTable;
    //timeline 存储库
    private DistributeTimelineStore store;
    //timeline 同步库
    private DistributeTimelineStore sync;

    /**
     * 删除timeline相关的所有表
     */
    public List<String> deleteAllofTimeline() {
        List<String> result = new ArrayList<>();
        result.add(JSON.toJSONString(tableStoreRepository.deleteTable(syncTable)));
        result.add(JSON.toJSONString(tableStoreRepository.deleteTable(storeTable)));
        return result;
    }


    /**
     * 读取最新的同步消息
     */
    public List<StringMessage> getSyncMessage(String user, long lastSequenceID) {
        List<StringMessage> messages = new ArrayList<>();
        logger.debug("Begin Get sync message from IM");
        Timeline userSync = new Timeline(user, sync);
        ScanParameter scanParameter = ScanParameterBuilder
                .scanBackward()
                .from(Long.MAX_VALUE)
                .to(lastSequenceID)
                .maxCount(100)
                .build();
        Iterator<TimelineEntry> iterator = userSync.scan(scanParameter);
        while (iterator.hasNext()) {
            TimelineEntry entry = iterator.next();
            try {
                String content = new String(entry.getMessage().serialize(), "UTF-8");

                StringMessage message = (StringMessage) entry.getMessage();
                logger.debug(message.toString());
                messages.add(message);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        logger.debug("End sync message from IM");
        return messages;
    }

    /**
     * 发送群组消息
     */
    public void sendGroupMessage(String groupName, IMessage message) {
        List<String> groupMembers = userAndGroupService.listGroupMembers(groupName);
        logger.debug("Begin send Message to " + groupMembers.size() + " members");
        Timeline sender = new Timeline(groupName, store);
        sender.store(message);

        for (String user : groupMembers) {
            Timeline receiver = new Timeline(user, sync);
            receiver.store(message);
        }
        logger.debug("End send Message to " + groupMembers.size() + " members");
    }

    /**
     * 发送个人消息
     */
    public void sendPersonalMessage(String userNameFrom, String userNameTo, IMessage message) {
        Timeline sender = new Timeline(userNameFrom, store);
        sender.store(message);

        Timeline receiver = new Timeline(userNameTo, sync);
        receiver.store(message);
        logger.debug("【" + userNameFrom + "】send Message to 【" + userNameTo + "】");
    }


    @PostConstruct
    private void init() {
        logger.info("Start create IM");
        DistributeTimelineConfig storeConfig = new DistributeTimelineConfig(
                endpoint,
                accessId,
                accessKey,
                instanceName,
                storeTable);
        storeConfig.setTtl(-1); // 永久
        storeConfig.setLimit(100);

        DistributeTimelineConfig syncConfig = new DistributeTimelineConfig(
                endpoint,
                accessId,
                accessKey,
                instanceName,
                syncTable);
        syncConfig.setTtl(30 * 24 * 3600); // 保存30天消息，30天用户还没收消息，就自动消失了。假如用户是高级会员，该值设置为-1，即可永久保存（tablestore的存储费用很低的）
        syncConfig.setLimit(100);


        store = new DistributeTimelineStore(storeConfig);
        sync = new DistributeTimelineStore(syncConfig);

        //如果存在则不创建
        if (!store.exist()) {
            logger.info("create store table");
            store.create();
        }

        if (!sync.exist()) {
            logger.info("create sync table");
            sync.create();
        }

        logger.info("End create IM");
    }

}
