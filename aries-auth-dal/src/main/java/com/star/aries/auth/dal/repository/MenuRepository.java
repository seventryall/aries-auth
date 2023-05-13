package com.star.aries.auth.dal.repository;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.star.aries.auth.dal.entity.Menu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MenuRepository extends AbstractJdbcDaoSupport<Menu> {

    public int insert(Menu menu) {
        String sql = "insert into ma_auth_menu(pid,code,name,uri,icon,status," +
                "type,sort,description,merchant_id,,client_id,creator)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int menuId = insertAndReturnId(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setInt(1, menu.getPid());
            ps.setString(2, menu.getCode());
            ps.setString(3, menu.getName());
            ps.setString(4, menu.getUri());
            ps.setString(5, menu.getIcon());
            ps.setInt(6, menu.getStatus());
            ps.setInt(7, menu.getType());
            ps.setInt(8, menu.getSort());
            ps.setString(9, menu.getDescription());
            ps.setInt(10, menu.getMerchantId());
            ps.setInt(11, menu.getClientId());
            ps.setInt(12, menu.getCreator());
            return ps;
        });
        return menuId;
    }

    public int update(Menu menu) {
        List<Object> paramList = Lists.newArrayList();
        StringBuffer stringBuffer = new StringBuffer("update ma_auth_menu set modifier=? ");
        paramList.add(menu.getModifier());

        append(stringBuffer, paramList, "code", menu.getCode());
        append(stringBuffer, paramList, "name", menu.getName());
        append(stringBuffer, paramList, "uri", menu.getUri());
        append(stringBuffer, paramList, "icon", menu.getIcon());
        append(stringBuffer, paramList, "status", menu.getStatus());
        append(stringBuffer, paramList, "type", menu.getType());
        append(stringBuffer, paramList, "sort", menu.getSort());
        append(stringBuffer, paramList, "description", menu.getDescription());

        stringBuffer.append(" where id=?");
        paramList.add(menu.getId());
        return super.update(stringBuffer.toString(), paramList.toArray());
    }

    private void append(StringBuffer stringBuffer, List paramList, String fieldName, Object val) {
        if (val != null && StringUtils.isNotBlank(val.toString())) {
            stringBuffer.append(String.format(",%s=?", fieldName));
            paramList.add(val);
        }
    }

    public int selectCount(Integer menuId) {
        Map<String, Object> param = new HashMap<>();
        param.put("pid", menuId);
        return super.count("select count(*)  from ma_auth_menu where f_pid=:pid", param);
    }

    public List<Menu> selectList(String searchText) {
        StringBuffer sql = new StringBuffer("select " +
                " id,pid,name,code,uri,icon,status,type,description" +
                " from ma_auth_menu" +
                " where (is_deleted is null or is_deleted=0) and status=1");
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(1);
        if (StringUtils.isNotBlank(searchText)) {
            sql.append(" and name like :searchText");
            params.put("searchText", "%" + searchText + "%");
        }
        sql.append(" order by sort");
        return super.queryForListWithMapParams(sql.toString(), params);
    }

    public List<Menu> selectAuthListByUserId(Integer userId) {
        StringBuffer sql = new StringBuffer("select " +
                " m.id,pid,name,code,uri,icon,type" +
                " from ma_auth_menu m join ma_auth_role_menu rm" +
                " on m.id=rm.menu_id" +
                " join ma_auth_role_user ru" +
                " on ru.role_id=rm.role_id" +
                " where (is_deleted is null or is_deleted=0) and status=1 and ru.user_id=:userId" +
                " order by sort");
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(1);
        params.put("userId", userId);
        return super.queryForListWithMapParams(sql.toString(), params);
    }

    public Menu selectMenu(Integer menuId) {
        StringBuffer sql = new StringBuffer("select " +
                " id,pid,name,code,uri,icon,status,type,description,create_time,update_time" +
                " from ma_auth_menu" +
                " where (is_deleted is null or is_deleted=0) and status=1 and id=:menuId");
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(1);
        params.put("menuId", menuId);
        return super.queryObjectWithMapParams(sql.toString(), params);
    }


    public int delete(Integer menuId) {
        return super.delete("update ma_auth_menu set is_deleted=1 where id=?", menuId);
    }


}
