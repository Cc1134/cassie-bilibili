package com.cassie.bilibili.domain;

import java.util.Date;

public class User {

    //User里面字段的命名和navicat里t_user里的命名完全一致
    //好处：在用mybatis框架的时候可以给我们一个直接的映射，
    // 也就是说数据库的字段查询出来以后不需要再额外的写一个对应关系来映射到user这个实体类里面
    private Long id;

    private String phone;

    private String email;

    private String password;

    private String salt;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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
}
