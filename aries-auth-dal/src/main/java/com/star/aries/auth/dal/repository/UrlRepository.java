package com.star.aries.auth.dal.repository;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.star.aries.auth.common.exception.DuplicateUniqueException;
import com.star.aries.auth.common.pojo.PageResult;
import com.star.aries.auth.dal.entity.Url;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UrlRepository extends AbstractJdbcDaoSupport<Url> {

//    @Autowired
//    private ClientRepository clientRepository;

//    public List<Url> getValidUrlsByClientId(String clientId) {
//        StringBuffer sql = new StringBuffer("SELECT " +
//                " id,url,url_desc,client_id,module_name,create_time,modify_time" +
//                " FROM ma_auth_url" +
//                " WHERE status=1 and client_id = (:clientId)");
//        return super.queryForListWithMapParams(sql.toString(), ImmutableMap.of("clientId", clientId));
//    }

    public int insert(Url url) {
        try {
            String sql = "insert into ma_auth_url (url,name,description,status,module_name,creator) values(?,?,?,?,?,?)";
            int urlId = insertAndReturnId(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, url.getUrl());
                ps.setString(2, url.getName());
                ps.setString(3, url.getDescription());
                ps.setInt(4, url.getStatus());
                ps.setString(5, url.getModuleName());
                ps.setInt(6, url.getCreator());
                return ps;
            });
            return urlId;
        } catch (DuplicateKeyException e) {
            throw new DuplicateUniqueException(JSON.toJSONString(url), String.format("URI:[%s]已经存在!", url.getUrl()));
        }
    }

    public int update(Url url) {
        try {
            List<Object> paramList = Lists.newArrayList();
            StringBuffer stringBuffer = new StringBuffer("update ma_auth_url set modify_time=CURRENT_TIMESTAMP()," +
                    "modifier=?");
            paramList.add(url.getModifier());

            append(stringBuffer, paramList, "url", url.getUrl());
            append(stringBuffer, paramList, "name", url.getName());
            append(stringBuffer, paramList, "description", url.getDescription());
            append(stringBuffer, paramList, "status", url.getStatus());
            append(stringBuffer, paramList, "module_name", url.getModuleName());

            stringBuffer.append(" where id=?");
            paramList.add(url.getId());
            int res = super.update(stringBuffer.toString(), paramList.toArray());
            return res;
        } catch (DuplicateKeyException e) {
            throw new DuplicateUniqueException(JSON.toJSONString(url), String.format("URL:[%s]已经存在!", url.getUrl()));
        }
    }

    private void append(StringBuffer stringBuffer, List paramList, String fieldName, Object val) {
        if (val != null && StringUtils.isNotBlank(val.toString())) {
            stringBuffer.append(String.format(",%s=?", fieldName));
            paramList.add(val);
        }
    }

    public Url selectUrlById(Integer id) {
        StringBuffer sql = new StringBuffer("SELECT " +
                " id,url,name,description,module_name,status,create_time,modify_time,creator,modifier" +
                " FROM ma_auth_role" +
                " WHERE (is_deleted=0 or is_deleted is null) and id =?");
        return super.queryForObject(sql.toString(), new Object[]{id});
    }

    public int delete(Integer id) {
        return super.delete("update ma_auth_url set is_deleted=1 where id=?", new Object[]{id});
    }

    public PageResult<List<Url>> selectByPage(String searchText, Integer pageNo, Integer pageSize) {
        Map<String, Object> params = new HashMap<String, Object>();

        StringBuffer sql = new StringBuffer("SELECT " +
                " id,url,name,description,status,module_name,create_time,modify_time" +
                " FROM ma_auth_url " +
                " WHERE (is_deleted=0 or is_deleted is null) ");
        if (StringUtils.isNotBlank(searchText)) {
            sql.append(" and (name like :searchText or url like :searchText or module_name like :searchText)");
            params.put("searchText", "%" + searchText + "%");
        }
        PageResult<List<Url>> urls = super.queryForListWithMapParamsByPage(sql.toString(), pageNo, pageSize, params);
        return urls;
    }

    public List<Url> selectUrlsByRoleId(Integer roleId) {
        StringBuffer sql = new StringBuffer("select" +
                " url,description,name,module_name" +
                " from ma_auth_role_url ru" +
                " join ma_auth_url u au on ru.role_id=u.id" +
                " where ru.role_id=:roleId");
        Map<String, Object> params = ImmutableMap.of("roleId", roleId);
        return super.queryForListWithMapParams(sql.toString(), params);
    }

//    public List<Url> selectByClientId(String clientId) {
//        StringBuffer sql = new StringBuffer("select" +
//                " id,url,url_desc,url_name,module_name" +
//                " from ma_auth_url" +
//                " where status=1");
//        Map<String, Object> params = new HashMap<String, Object>();
//        if (StringUtils.isNotBlank(clientId)) {
//            sql.append(" and client_id = :clientId");
//            params.put("clientId", clientId);
//        }
//        return super.queryForListWithMapParams(sql.toString(), params);
//    }
}
