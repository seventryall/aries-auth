package com.star.aries.auth.dal.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by ben on 2018/8/9.
 */
@Data
@NoArgsConstructor
public class ClientResource {

    public ClientResource(Integer clientId, Integer resourceId){
        this.clientId = clientId;
        this.resourceId = resourceId;
    }

    private Integer clientId;
    private String clientCode;
    private Integer resourceId;
    private String resourceCode;
    private String resourceName;
}
