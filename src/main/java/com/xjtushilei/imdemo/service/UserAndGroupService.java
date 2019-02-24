package com.xjtushilei.imdemo.service;

import com.alibaba.fastjson.JSON;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKeySchema;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.xjtushilei.imdemo.entity.Group;
import com.xjtushilei.imdemo.entity.User;
import com.xjtushilei.imdemo.repository.PkV;
import com.xjtushilei.imdemo.repository.TableStoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserAndGroupService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    TableStoreRepository tableStoreRepository;

    @Value("${im.userTable}")
    private String userTable;

    @Value("${im.groupTable}")
    private String groupTable;

    @Value("${im.groupUserTable}")
    private String groupUserTable;


    public void createTable() {
        logger.info("Begin create some table for user and group");
        List<String> existTables = tableStoreRepository.ListTable();
        logger.debug("existTables: " + existTables.toString());
        if (!existTables.contains(userTable)) {
            tableStoreRepository.createTable(userTable, Collections.singletonList(new PrimaryKeySchema("userName", PrimaryKeyType.STRING)));
        }
        if (!existTables.contains(groupTable)) {
            tableStoreRepository.createTable(groupTable, Collections.singletonList(new PrimaryKeySchema("groupName", PrimaryKeyType.STRING)));
        }
        if (!existTables.contains(groupUserTable)) {
            tableStoreRepository.createTable(groupUserTable, Arrays.asList(new PrimaryKeySchema("groupName", PrimaryKeyType.STRING),
                    new PrimaryKeySchema("userName", PrimaryKeyType.STRING)));
        }
        logger.info("End create some table for user and group");
    }


    public void addUser(User user) {
        tableStoreRepository.putRow(userTable, user.toPKs(), user.toColumns());
    }

    public void addGroup(Group group) {
        tableStoreRepository.putRow(groupTable, group.toPKs(), group.toColumns());
    }

    public void userJoinGroup(String userName, String groupName) {
        Map<String, PrimaryKeyValue> pk = new HashMap<>();
        pk.put("groupName", PrimaryKeyValue.fromString(groupName));
        pk.put("userName", PrimaryKeyValue.fromString(userName));

        Map<String, ColumnValue> columnValueMap = new HashMap<>();
        columnValueMap.put("joinTime", ColumnValue.fromLong(Timestamp.valueOf(LocalDateTime.now()).getTime()));
        tableStoreRepository.putRow(groupUserTable, pk, columnValueMap);
    }

    public void userLeaveGroup(String userName, String groupName) {
        Map<String, PrimaryKeyValue> pk = new HashMap<>();
        pk.put("groupName", PrimaryKeyValue.fromString(groupName));
        pk.put("userName", PrimaryKeyValue.fromString(userName));
        tableStoreRepository.deleteRow(groupUserTable, pk);
    }

    public List<String> listGroupMembers(String groupName) {
        List<String> memberNames = new ArrayList<>();
        tableStoreRepository
                .getRange(
                        groupUserTable,
                        Arrays.asList(
                                PkV.newPkV().pk("groupName").primaryKeyValue(PrimaryKeyValue.fromString(groupName)).build(),
                                PkV.newPkV().pk("userName").primaryKeyValue(PrimaryKeyValue.INF_MIN).build()
                        ),
                        Arrays.asList(
                                PkV.newPkV().pk("groupName").primaryKeyValue(PrimaryKeyValue.fromString(groupName)).build(),
                                PkV.newPkV().pk("userName").primaryKeyValue(PrimaryKeyValue.INF_MAX).build()
                        ))
                .forEach(row -> memberNames.add(row.getPrimaryKey().getPrimaryKeyColumnsMap().get("userName").getValue().asString()));
        return memberNames;
    }

    public boolean existUser(String userName) {
        Map<String, PrimaryKeyValue> pk = new HashMap<>();
        pk.put("userName", PrimaryKeyValue.fromString(userName));
        return tableStoreRepository.getRow(userTable, pk).getRow()!=null;

    }


    /**
     * 删除用户和群组相关的所有表
     */
    public List<String> deleteAllofUserAndGroup() {
        List<String> result = new ArrayList<>();
        result.add(JSON.toJSONString(tableStoreRepository.deleteTable(userTable)));
        result.add(JSON.toJSONString(tableStoreRepository.deleteTable(groupTable)));
        result.add(JSON.toJSONString(tableStoreRepository.deleteTable(groupUserTable)));

        return result;
    }

}
