package com.star.aries.auth.dal.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class SysClient {

    private Integer id;

    private String clientId;

    private String clientName;

    private String clientSecret;

    private String clientDesc;

    private Integer clientType;

    private String companyId;

    private String companyName;

    private Integer resourceStatus;

    private String scope;

    private String authorizedGrantTypes;

    private String autoApprove;

    private Integer accessTokenValidity;

    private Integer refreshTokenValidity;

    private String additionalInformation;

    private String webServerRedirectUri;

    private Date createTime;

    private Date modifyTime;

    private String resources;//所拥有的资源

    private List<Role> roles;
    //private List<Authority> authorities;//策略：可配权限的最小单位
}
