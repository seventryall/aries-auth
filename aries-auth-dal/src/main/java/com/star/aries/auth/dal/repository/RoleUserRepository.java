package com.star.aries.auth.dal.repository;

import com.star.aries.auth.dal.entity.RoleUser;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RoleUserRepository extends AbstractJdbcDaoSupport<RoleUser>{

    /**
     * 删除用户下所有角色
     * @param userId
     * @return
     */
    public Integer deleteByUserId(Integer userId){
        return super.delete("delete from ma_auth_role_user_mapping where f_user_id = ?", userId);
    }

    /**
     * 删除该role的所有关联
     * @param roleId
     * @return
     */
    public Integer deleteByRoleId(Integer roleId){
        return super.delete("delete from ma_auth_role_user_mapping where role_id = ?", roleId);
    }


    public int insert(List<RoleUser> roleUsers){
        String sql = "insert into ma_auth_role_user_mapping (role_id,user_id) values (?,?)";
        int res=executeBatch(sql,new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return roleUsers.size();
            }
            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                ps.setInt(1, roleUsers.get(i).getRoleId());
                ps.setInt(2, roleUsers.get(i).getUserId());
            }
        });
        return res;
    }
}
