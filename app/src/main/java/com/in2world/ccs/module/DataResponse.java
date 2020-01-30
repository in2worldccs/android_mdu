package com.in2world.ccs.module;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.in2world.ccs.helper.ValidationHelper;

import java.util.ArrayList;
import java.util.List;

public class DataResponse {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("users")
    @Expose
    private List<User> userList;
    @SerializedName("group")
    @Expose
    private Group group;
    @SerializedName("groups")
    @Expose
    private List<Group> groupList;
    @SerializedName("groups_user")
    @Expose
    private List<Group> groupUserList;

    public User getUser() {
        return ValidationHelper.validObject(user) ? user : new User();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<User> getUserList() {
        return ValidationHelper.validList(userList) ? userList : new ArrayList<User>();
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public Group getGroup() {
        return ValidationHelper.validObject(group) ? group : new Group();
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Group> getGroupList() {
        return ValidationHelper.validList(groupList) ? groupList : new ArrayList<Group>();
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public List<Group> getGroupUserList() {
        return ValidationHelper.validList(groupUserList) ? groupUserList : new ArrayList<Group>();
    }

    public void setGroupUserList(List<Group> groupUserList) {
        this.groupUserList = groupUserList;
    }
}
