package com.star.aries.auth.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 接口树形节点，默认三层结构：应用 模块 接口
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlNode {
    private String name;
    private String url;
    private List<UrlNode> subNodes;
}
