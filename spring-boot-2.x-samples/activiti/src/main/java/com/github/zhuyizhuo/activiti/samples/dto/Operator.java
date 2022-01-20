package com.github.zhuyizhuo.activiti.samples.dto;

import java.io.Serializable;

public class Operator implements Serializable {

    private static final long serialVersionUID = -8481895465223141247L;
    private String deptId;
    private String positionId;
    private String staffId;

    public Operator(String deptId, String positionId, String staffId) {
        this.deptId = deptId;
        this.positionId = positionId;
        this.staffId = staffId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}
