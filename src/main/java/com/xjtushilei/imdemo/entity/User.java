package com.xjtushilei.imdemo.entity;

import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userName;
    private int age;
    private String gender;
    private String sign;

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

    public User() {
    }

    private User(Builder builder) {
        this.userName = builder.userName;
        this.age = builder.age;
        this.gender = builder.gender;
        this.sign = builder.sign;
    }

    public static Builder newUser() {
        return new Builder();
    }

    /**
     * 返回 tableStore 需要的的主键的格式
     * @return pk
     */
    public Map<String, PrimaryKeyValue> toPKs() {
        Map<String, PrimaryKeyValue> pk = new HashMap<>();
        pk.put("userName", PrimaryKeyValue.fromString(this.userName));
        return pk;
    }

    /**
     * 返回 tableStore 的属性列和值的格式
     * @return 属性列
     */
    public Map<String, ColumnValue> toColumns() {
        Map<String, ColumnValue> columnValueMap = new HashMap<>();
        columnValueMap.put("age", ColumnValue.fromLong(this.age));
        columnValueMap.put("sign", ColumnValue.fromString(this.sign));
        columnValueMap.put("gender", ColumnValue.fromString(this.gender));
        return columnValueMap;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public static final class Builder {
        private String userName;
        private int age;
        private String gender;
        private String sign;

        private Builder() {
        }

        public User build() {
            return new User(this);
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder sign(String sign) {
            this.sign = sign;
            return this;
        }
    }
}
