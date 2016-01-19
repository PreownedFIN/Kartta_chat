package com.sqllite.sakari.sqlitetest;

import java.util.List;

/**
 * Created by Sakari on 09.01.2016.
 */
public class Group {

    public Group(){}

    public Group(String groupName, String groupPassWord, User creator){
        this.groupName = groupName;
        this.groupPassWord = groupPassWord;
        this.creator = creator;
        this.users.add(creator);
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

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

    public void addUser(User user){
        this.users.add(this.users.size(), user);
    }

    public void removeUser(User user){
        this.users.remove(user);
    }

    int groupId;
    String groupName;
    String groupPassWord;
    User creator;
    List<User> users;
}
