package com.star.aries.auth.dal.repository;

import com.google.common.collect.ImmutableMap;
import com.star.aries.auth.dal.entity.ClientResource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ClientResourceRepository extends AbstractJdbcDaoSupport<ClientResource> {

    public List<String> selectResourcesByClientId(String clientId) {
        Assert.hasText(clientId, "参数clientId不能为空");

        String sql = "select c.client_code from ma_auth_client c,ma_auth_client_resource cr" +
                " where c.id=cr.resource_id and cr.client_id = :clientId";

        return super.queryStringForListWithMapParams(sql, ImmutableMap.of("clientId", clientId));
    }

    public int insert(List<ClientResource> clientResources){
        String sql = "insert into ma_auth_client_resource(client_id,resource_id)"+
                " values(?,?)";
        int res=executeBatch(sql,new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return clientResources.size();
            }
            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                ps.setInt(1, clientResources.get(i).getClientId());
                ps.setInt(2, clientResources.get(i).getResourceId());
            }
        });
        return res;
    }

    public int deleteByClientId(Integer clientId){
        return super.delete("delete from ma_auth_client_resource where client_id=?", clientId);
    }

}
