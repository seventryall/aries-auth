package com.star.aries.auth.dal.repository;

import com.google.common.collect.Maps;
import com.star.aries.auth.dal.entity.RoleMenu;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class RoleMenuRepository extends AbstractJdbcDaoSupport<RoleMenu> {

    public int insert(List<RoleMenu> roleMenus){
        String sql = "insert into ma_auth_role_menu_mapping (role_id,menu_id) values (?,?)";
        int res=executeBatch(sql,new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return roleMenus.size();
            }
            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                ps.setInt(1, roleMenus.get(i).getRoleId());
                ps.setInt(2, roleMenus.get(i).getMenuId());
            }
        });
        return res;
    }

    public List<RoleMenu> selectList(String roleId){
        StringBuffer sql = new StringBuffer("select menu_id" +
                " form ma_auth_role_menu_mapping" +
                " where role_id=:roleId");
        Map<String,Object> params = Maps.newHashMapWithExpectedSize(1);
        params.put("roleId", roleId);
        return super.queryForListWithMapParams(sql.toString(),params);
    }

    public int deleteByRoleId(Integer roleId){
        return super.delete("delete from ma_auth_role_menu_mapping where role_id=?", roleId);
    }
}
