package com.star.aries.auth.dal.repository;

import com.google.common.collect.Maps;
import com.star.aries.auth.dal.entity.RoleUrl;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class RoleUrlRepository extends AbstractJdbcDaoSupport<RoleUrl> {

    public int insert(List<RoleUrl> roleUrls){
        String sql = "insert into ma_auth_role_url_mapping (role_id,url_id) values (?,?)";
        int res=executeBatch(sql,new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return roleUrls.size();
            }
            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                ps.setInt(1, roleUrls.get(i).getRoleId());
                ps.setInt(2, roleUrls.get(i).getUrlId());
            }
        });
        return res;
    }

    public List<RoleUrl> selectList(String roleId){
        StringBuffer sql = new StringBuffer("select url_id" +
                " form ma_auth_role_url_mapping" +
                " where role_id=:roleId");
        Map<String,Object> params = Maps.newHashMapWithExpectedSize(1);
        params.put("roleId", roleId);
        return super.queryForListWithMapParams(sql.toString(),params);
    }

    public int deleteByRoleId(Integer roleId){
        return super.delete("delete from ma_auth_role_url_mapping where role_id=?", roleId);
    }
}
