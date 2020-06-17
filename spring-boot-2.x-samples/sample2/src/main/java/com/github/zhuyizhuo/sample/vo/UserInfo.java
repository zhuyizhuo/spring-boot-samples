package com.github.zhuyizhuo.sample.vo;

import io.swagger.annotations.ApiModelProperty;

public class UserInfo {
    /** position 为 swagger ui 上展示的顺序，非必须；
     *  数值型字段需指定 example 为数值字符串 ，否则 swagger 会报错，这个是 swagger 的 BUG
     */
    @ApiModelProperty(position = 1, example = "1")
    private Long id;

    @ApiModelProperty(value="姓名", allowEmptyValue = true, position = 2)
    private String realName;

    @ApiModelProperty(value="身份证号", allowEmptyValue = true, position = 3)
    private String certNo;

    @ApiModelProperty(position = 4)
    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", realName='" + realName + '\'' +
                ", certNo='" + certNo + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
