package com.padyun.scripttools.biz.ui.data;

import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.padyun.scripttools.R;

/**
 * Created by daiepngfei on 2020-06-05
 */
public class MdFavoredUserTask implements IBaseRecyclerModel, Comparable<MdFavoredUserTask> {

    private String id;
    private String task_name;
    private String activate_version; // 当前线上激活版本
    private int status; // 审核状态 0 审核中， 1 通过，2 拒绝
    private int is_share; // 0 share
    private int is_creator; // 0 no 1 yes
    private int task_status; // 0 未添加   1 正在运行  2 待运行


    public boolean hasCensoredVersion() {
        return activate_version != null && !activate_version.isEmpty();
    }

    public boolean isCreator() {
        return is_creator == 1;
    }

    public boolean isTaskRunning(){
        return task_status == 1;
    }

    public boolean isTaskAdded() {
        return task_status != 0;
    }

    public boolean canShared() {
        return is_share == 0;
    }

    public boolean isWaiting(){
        return task_status == 2;
    }

    public boolean isIdle() {
        return task_status == 0;
    }

    public boolean isUncensored() {
        return status == 0;
    }

    public boolean isCensored(){
        return status == 1;
    }

    public boolean isRejected() {
        return status == 2;
    }

    @Override
    public int getTypeItemLayoutId() {
        return R.layout.item_custom_script_item;
    }

    public String getId() {
        return id;
    }


    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getActivate_version() {
        return activate_version;
    }

    public void setActivate_version(String activate_version) {
        this.activate_version = activate_version;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIs_share() {
        return is_share;
    }

    public void setIs_share(int is_share) {
        this.is_share = is_share;
    }

    public int getIs_creator() {
        return is_creator;
    }

    public void setIs_creator(int is_creator) {
        this.is_creator = is_creator;
    }

    public int getTask_status() {
        return task_status;
    }

    public void setTask_status(int task_status) {
        this.task_status = task_status;
    }

    @Override
    public int compareTo(MdFavoredUserTask o) {
        if(o == null){
            return -1;
        }

        if(o.task_status == task_status){
            return 0;
        }

        if(task_status == 1){
            return -1;
        }

        if(task_status == 0){
            return 1;
        }

        if(task_status == 2){
            return o.task_status == 1 ? 1 : -1;
        }

        return 0;

    }
}
