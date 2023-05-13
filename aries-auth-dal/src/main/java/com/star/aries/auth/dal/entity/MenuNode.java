package com.star.aries.auth.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 菜单树形节点
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuNode {
    private String clientId;
    private String clientName;
    private List<Menu> menus;
}