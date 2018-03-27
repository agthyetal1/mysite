package com.oil.entity;


import org.apache.xpath.operations.String;

import javax.persistence.*;
import java.util.Date;

@Table(name = "wx_banner")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "select seq_wx_banner.nextval from dual")
    private Long id;
    private String usePlace;
    private String photoImg;
    private Integer sort;
    private Integer forbidStatus;
    private String remark;
    private String actionType;
    private String actionParam;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsePlace() {
        return usePlace;
    }

    public void setUsePlace(String usePlace) {
        this.usePlace = usePlace;
    }

    public String getPhotoImg() {
        return photoImg;
    }

    public void setPhotoImg(String photoImg) {
        this.photoImg = photoImg;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getForbidStatus() {
        return forbidStatus;
    }

    public void setForbidStatus(Integer forbidStatus) {
        this.forbidStatus = forbidStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionParam() {
        return actionParam;
    }

    public void setActionParam(String actionParam) {
        this.actionParam = actionParam;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
