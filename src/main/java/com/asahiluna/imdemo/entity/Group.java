package com.asahiluna.imdemo.entity;

import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;

import java.util.HashMap;
import java.util.Map;

public class Group {

    public Group() {
    }

    private String groupName;
    private String groupType;
    private String groupDescription;

    /**
     * 返回 tableStore 需要的的主键的格式
     * @return pk
     */
    public Map<String, PrimaryKeyValue> toPKs() {
        Map<String, PrimaryKeyValue> pk = new HashMap<>();
        pk.put("groupName", PrimaryKeyValue.fromString(this.groupName));
        return pk;
    }

    /**
     * 返回 tableStore 的属性列和值的格式
     *
     * @return 属性列
     */
    public Map<String, ColumnValue> toColumns() {
        Map<String, ColumnValue> columnValueMap = new HashMap<>();
        columnValueMap.put("groupType", ColumnValue.fromString(this.groupType));
        columnValueMap.put("groupDescription", ColumnValue.fromString(this.groupDescription));
        return columnValueMap;
    }

    private Group(Builder builder) {
        this.groupName = builder.groupName;
        this.groupType = builder.groupType;
        this.groupDescription = builder.groupDescription;
    }

    public static Builder newGroup() {
        return new Builder();
    }


    public static final class Builder {
        private String groupName;
        private String groupType;
        private String groupDescription;

        private Builder() {
        }

        public Group build() {
            return new Group(this);
        }

        public Builder groupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public Builder groupType(String groupType) {
            this.groupType = groupType;
            return this;
        }

        public Builder groupDescription(String groupDescription) {
            this.groupDescription = groupDescription;
            return this;
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }
}
