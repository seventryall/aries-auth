package com.star.aries.auth.dal.entity;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Authority {

    private String permission;  //AuthServerAdmin
    private String permissionDesc;
    private String resource;  //权限所属资源
    private String statement;   //{"statement":[{"Action":"dev:read","Effect":"Allow","Resource":"*"}]}，这里可能不需要resource
    private Integer authorizationLevel;

    private Date gmtCreate;

    private Date gmtModify;

    private String permissionName;

    private Integer companyType;

    private List<Url> urlList;

    public Authority(String permission, String resource){
        this.permission = permission;
        this.resource = resource;
    }

    public String toJsonString(){
        return JSON.toJSONString(this);
    }

    public static Authority toObject(String json){
        if(StringUtils.isNotBlank(json) && json.startsWith("{") && json.endsWith("}")){
            return JSON.parseObject(json, Authority.class);
        }
        return null;
    }
}
