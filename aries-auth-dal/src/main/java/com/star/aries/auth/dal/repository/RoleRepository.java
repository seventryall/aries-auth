package com.star.aries.auth.dal.repository;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.star.aries.auth.common.exception.DuplicateUniqueException;
import com.star.aries.auth.common.pojo.PageResult;
import com.star.aries.auth.dal.entity.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;


@Repository
public class RoleRepository extends AbstractJdbcDaoSupport<Role> {

    public Role selectByRoleId(Integer roleId) {
        Map<String, Object> params = ImmutableMap.of("roleId", roleId);
        StringBuffer sql = new StringBuffer("select" +
                " id,code,name,description" +
                " from ma_auth_role" +
                " where (is_deleted=0 or is_deleted is null) and id=:roleId");
        return super.queryObjectWithMapParams(sql.toString(), params);
    }

    /**
     * @param roleIds
     * @return
     */
    public List<Role> selectByRoleIds(List<Integer> roleIds) {
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(5);
        StringBuffer sql = new StringBuffer("select" +
                " id,name,code,description" +
                " from ma_auth_role" +
                " where (is_deleted=0 or is_deleted is null) and id in (:roleIds)");
        params.put("roleIds", roleIds);
        List<Role> res = super.queryForListWithMapParams(sql.toString(), params);
        return res;
    }


    public Integer insert(Role role) {
        try {
            String sql = "insert into ma_auth_role(code,name,description,creator)values(?,?,?,?)";
            int id = insertAndReturnId(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, role.getCode());
                ps.setString(2, role.getName());
                ps.setString(3, role.getDescription());
                ps.setInt(4, role.getCreator());
                return ps;
            });
            return id;
        } catch (DuplicateKeyException e) {
            throw new DuplicateUniqueException(JSON.toJSONString(role), String.format("ROLE:[%s]已经存在!",
                    role.getName()));
        }
    }

    public int update(Role role) {
        List<Object> paramList = Lists.newArrayList();
        StringBuffer stringBuffer = new StringBuffer("update ma_auth_role set modify_time=CURRENT_TIMESTAMP()" +
                ",modifier=?");
        paramList.add(role.getModifier());

        append(stringBuffer, paramList, "code", role.getCode());
        append(stringBuffer, paramList, "name", role.getName());
        append(stringBuffer, paramList, "description", role.getDescription());

        stringBuffer.append(" where id=?");
        paramList.add(role.getId());
        return super.update(stringBuffer.toString(), paramList.toArray());
    }

    private void append(StringBuffer stringBuffer, List paramList, String fieldName, Object val) {
        if (val != null && StringUtils.isNotBlank(val.toString())) {
            stringBuffer.append(String.format(",%s=?", fieldName));
            paramList.add(val);
        }
    }

    public int delete(Integer roleId) {
        return super.delete("update ma_auth_role set is_deleted=1 where id=?", new Object[]{roleId});
    }

    public List<Role> selectRolesByUserId(Integer userId) {
        StringBuffer sql = new StringBuffer("select " +
                " r.id,r.code,r.name,r.description" +
                " from ma_auth_role r, ma_auth_role_user_mapping ru" +
                " where r.id = ru.role_id" +
                " and ru.user_id = ?");
        return super.queryForList(sql.toString(), userId);
    }

    public List<Role> selectRolesByUrl(String url){
        StringBuffer sql = new StringBuffer("select " +
                " r.id,role_code,role_name,role_desc,client_id" +
                " from ma_auth_role r join ma_auth_role_url_mapping ru" +
                " on r.id=ru.role_id"+
                " join ma_auth_url u"+
                " on u.id=ru.url_id"+
                " where u.url=?");
        return super.queryForList(sql.toString(), url);
    }

    public PageResult<List<Role>> selectRoleListByPage(String searchText, Integer pageNo, Integer pageSize) {
        StringBuffer sql = new StringBuffer("select" +
                " id,code,name,description" +
                " from ma_auth_role" +
                " where (is_deleted=0 or is_deleted is null)");
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(searchText)) {
            sql.append(" and (code like :searchText or name like :searchText)");
            params.put("searchText", "%" + searchText + "%");
        }
        return super.queryForListWithMapParamsByPage(sql.toString(), pageNo, pageSize, params);
    }


}
