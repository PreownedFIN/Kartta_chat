package com.sqllite.sakari.sqlitetest;

import java.util.List;

/**
 * Created by Sakari on 09.01.2016.
 */
public class Group {
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupPassWord() {
        return groupPassWord;
    }

    public void setGroupPassWord(String groupPassWord) {
        this.groupPassWord = groupPassWord;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    String groupName;
    String groupPassWord;
    List<User> users;
}
