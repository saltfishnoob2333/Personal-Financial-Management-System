package com.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Bill {
    private Integer id;
    private Integer userId;
    private Integer categoryId;
    private String categoryName; // 用于显示，不存数据库
    private BigDecimal amount;
    private String type; // INCOME 或 EXPENSE
    private String remark;
    private Date billDate;
    private Date createTime;
    private Date updateTime;

    public Bill() {}

    public Bill(Integer userId, Integer categoryId, BigDecimal amount,
                String type, String remark, Date billDate) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.type = type;
        this.remark = remark;
        this.billDate = billDate;
    }

    // Getter和Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", remark='" + remark + '\'' +
                ", billDate=" + billDate +
                '}';
    }
}