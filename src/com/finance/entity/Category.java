package com.finance.entity;

import java.util.Date;

public class Category {
    private Integer id;
    private String name;
    private String type; // INCOME 或 EXPENSE
    private Integer userId;
    private Date createTime;

    public Category() {}

    public Category(String name, String type, Integer userId) {
        this.name = name;
        this.type = type;
        this.userId = userId;
    }

    // Getter和Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}