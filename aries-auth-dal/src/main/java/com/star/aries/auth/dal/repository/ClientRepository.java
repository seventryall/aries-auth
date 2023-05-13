package com.star.aries.auth.dal.repository;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.star.aries.auth.common.exception.DuplicateUniqueException;
import com.star.aries.auth.common.pojo.PageResult;
import com.star.aries.auth.common.util.Md5;
import com.star.aries.auth.common.util.StringRandomUtil;
import com.star.aries.auth.dal.entity.SysClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ClientRepository extends AbstractJdbcDaoSupport<SysClient> {

    public SysClient selectByClientId(String clientId) {
        SysClient sysClient = super.queryForObject("select " +
                " id,client_id,client_secret,client_name,client_desc,scope," +
                " authorized_grant_types,auto_approve,access_token_validity," +
                " refresh_token_validity,additional_information,web_server_redirect_uri," +
                " create_time,modify_time" +
                " from ma_client_info where client_id = ?", clientId);
        //获取所属资源，包含自己
//        Set<String> sources = clientResourceRepository.selectSourcesByClientId(clientId).stream().collect(Collectors
//                .toSet());
//        if(sources != null && sources.size() > 0){
//            sysClient.setResources(String.join(",", sources));
//        }
//        sysClient.setAuthorities(authoritiesRepository.selectGrantedAuthoritiesByClientId(clientId));
        return sysClient;
    }

    public SysClient selectById(Integer id) {
        SysClient sysClient = super.queryForObject("select " +
                " id,client_id,client_name,client_desc,scope," +
                " authorized_grant_types,auto_approve,access_token_validity," +
                " refresh_token_validity,additional_information,web_server_redirect_uri," +
                " create_time,modify_time" +
                " from ma_client_info where id = ?", id);
        return sysClient;
    }

    public Integer insert(SysClient sysClient) {
        try {
            return super.insert("insert into ma_client_info(client_id, client_secret, client_name, " +
                            "client_desc,scope,authorized_grant_types,auto_approve)" +
                            " values(?,?,?,?,?,?,?)",
                    new Object[]{sysClient.getClientId(),
                            Md5.getInstance().md5_32(StringRandomUtil.randomStr(8)),
                            sysClient.getClientName(),
                            sysClient.getClientDesc(),
                            "read,write,trust",
                            sysClient.getAuthorizedGrantTypes(),
                            1
                    });
        } catch (DuplicateKeyException e) {
            throw new DuplicateUniqueException(JSON.toJSONString(sysClient), String.format("应用:[%s,%s]已经存在!",
                    sysClient.getClientId(), sysClient.getClientName()));
        }
    }

    public int update(SysClient sysClient) {
        List<Object> paramList = Lists.newArrayList();
        StringBuffer stringBuffer = new StringBuffer("update ma_client_info set modify_time=CURRENT_TIMESTAMP() ");

        append(stringBuffer, paramList, "client_id", sysClient.getClientId());
        append(stringBuffer, paramList, "client_name", sysClient.getClientName());
        append(stringBuffer, paramList, "client_desc", sysClient.getClientDesc());
        append(stringBuffer, paramList, "resource_status", sysClient.getResourceStatus());

        stringBuffer.append(" where id=?");
        paramList.add(sysClient.getId());
        return super.update(stringBuffer.toString(), paramList.toArray());
    }

    private void append(StringBuffer stringBuffer, List paramList, String fieldName, Object val) {
        if (val != null && StringUtils.isNotBlank(val.toString())) {
            stringBuffer.append(String.format(",%s=?", fieldName));
            paramList.add(val);
        }
    }

    public PageResult<List<SysClient>> selectListByPage(String searchText, Integer pageNo, Integer pageSize){
        StringBuffer sql = new StringBuffer("select" +
                " id,client_id,client_name,client_desc,client_type,scope,resource_status" +
                " from ma_client_info" +
                " where 1=1");
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(searchText)) {
            sql.append(" and (client_id like :searchText or client_name like :searchText)");
            params.put("searchText", "%" + searchText + "%");
        }
        return super.queryForListWithMapParamsByPage(sql.toString(), pageNo, pageSize, params);
    }

    public int delete(Integer id){
        return super.delete("delete from ma_client_info where id=?", id);
    }

    public List<SysClient> selectByIds(List<Integer> ids){
        return super.queryForList("select id, client_name, client_code from ma_client_info where id in (:ids)",ids);
    }
}
