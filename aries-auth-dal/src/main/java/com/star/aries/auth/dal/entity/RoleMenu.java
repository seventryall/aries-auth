package com.star.aries.auth.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleMenu {
    private Integer menuId;
    private Integer roleId;
    private Integer actionValues;
}
