package com.star.aries.auth.dal.repository;


import com.google.common.collect.Maps;
import com.star.aries.auth.dal.entity.MenuAction;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class MenuActionRepository extends AbstractJdbcDaoSupport<MenuAction> {

    public int insert(List<MenuAction> menuActions) {
        String sql = "insert into ma_auth_menu_action(menu_id,code,name,description,value" +
                "client_id,creator)" +
                " values(?,?,?,?,?,?,?)";
        int res = executeBatch(sql, new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return menuActions.size();
            }

            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                ps.setInt(1, menuActions.get(i).getMenuId());
                ps.setString(2, menuActions.get(i).getCode());
                ps.setString(3, menuActions.get(i).getName());
                ps.setString(4, menuActions.get(i).getDescription());
                ps.setInt(5, menuActions.get(i).getValue());
                ps.setInt(6, menuActions.get(i).getClientId());
                ps.setInt(7, menuActions.get(i).getCreator());
            }
        });
        return res;
    }

    public int deleteByMenuId(Integer menuId) {
        return super.delete("delete from ma_auth_menu_action where menu_id=?", menuId);
    }

    public List<MenuAction> selectList(Integer menuId) {
        StringBuffer sql = new StringBuffer("select " +
                " id,menu_id,name,code,value,description" +
                " from ma_auth_menu_action ma" +
                " where menu_id=:menuId");
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(1);
        params.put("menuId", menuId);
        return super.queryForListWithMapParams(sql.toString(), params);
    }

    public List<MenuAction> selectAuthListByUserId(Integer userId) {
        StringBuffer sql = new StringBuffer("select " +
                " f_action_id AS action_id," +
                " f_menu_id as menu_id," +
                " f_name as name," +
                " f_code as code" +
                " from ma_auth_menu_action ma join ma_auth_role_menu rm " +
                " on ma.menu_id=rm.menu_id" +
                " join ma_auth_role_user ru" +
                " on ru.role_id=rm.role_id" +
                " where (rm.actionValues&ma.value)>0 and ru.user_id=:userId");
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(1);
        params.put("userId", userId);
        return super.queryForListWithMapParams(sql.toString(), params);
    }
}
