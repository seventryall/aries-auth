package com.star.aries.auth.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuAction {
    private Integer id;
    private Integer menuId;
    private String code;
    private String name;
    private Integer value;
    private String description;
    private Integer clientId;
    private Integer creator;
    private Integer modifier;
    private Date gmtCreate;
    private Date gmtModify;
    private String urls;
}
