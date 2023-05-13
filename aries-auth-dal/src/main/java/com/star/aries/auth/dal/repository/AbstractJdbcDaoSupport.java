package com.star.aries.auth.dal.repository;

import com.star.aries.auth.common.pojo.PageResult;
import com.star.aries.auth.common.pojo.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractJdbcDaoSupport<T> {

    private Class<T> entityClass;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public AbstractJdbcDaoSupport() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = (Class) params[0];
    }

    /**
     * 带参数的sql查询
     *
     * @param sql    eg: select * from t_sys_user where f_phone = ? or f_account = ? or f_email = ?
     * @param params eg : 13311111111, benny, 13311111111@163.com
     * @return
     */
    protected T queryForObject(String sql, Object... params) {
        List<T> r = jdbcTemplate.query(sql, params, new BeanPropertyRowMapper(entityClass));
        if (r != null && r.size() > 0) {
            return r.get(0);
        }
        return null;
    }

    protected Object queryForObject(String sql, Map<String, Object> params, Class clazz) {
        return namedParameterJdbcTemplate.queryForObject(sql,params,clazz);
    }

    /**
     * sql查询
     *
     * @param sql
     * @return
     */
    protected List<T> queryForList(String sql) {
        return jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(entityClass));
    }

    /**
     * @param sql
     * @param params
     * @return
     */
    protected List<T> queryForList(String sql, Object... params) {
        if (params != null && params.length == 1 && params[0].getClass() == ArrayList.class && ((ArrayList) params[0]).size() == 0) {
            params = null;
        }
        if (params == null || params.length == 0) {
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper(entityClass));
        }
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper(entityClass));
    }

    protected List<String> queryForStringList(String sql, Object... params) {
        List<String> r = jdbcTemplate.queryForList(sql, params, String.class);
        return r;
    }

    protected int count(String sql, Map<String, Object> params) {
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    /**
     * @param sql
     * @param params
     */
    protected int insert(String sql, Object... params) {
        return jdbcTemplate.update(sql, params);
    }

    protected int delete(String sql, Object... params) {
        return jdbcTemplate.update(sql, params);
    }

    protected int update(String sql, Object... params) {
        return jdbcTemplate.update(sql, params);
    }

    protected int executeBatch(String sql, BatchPreparedStatementSetter batchPreparedStatementSetter) {
        return jdbcTemplate.batchUpdate(sql, batchPreparedStatementSetter).length;
    }

    protected List<T> queryForListWithMapParams(String sql, Map<String, Object> params) {
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper(entityClass));
    }

    protected List<String> queryStringForListWithMapParams(String sql, Map<String, Object> params) {
        return namedParameterJdbcTemplate.queryForList(sql, params, String.class);
    }

    protected T queryObjectWithMapParams(String sql, Map<String, Object> params) {
        return (T) namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper(entityClass));
    }

    protected PageResult<List<T>> queryForListWithMapParamsByPage(String sql, Integer pageNo, Integer pageSize, Map<String, Object> params) {
        PageResult pageData;
        if (pageNo != null && pageNo > 0 && pageSize != null && pageSize > 0) {
            Pagination pagination = new Pagination(sql, pageNo, pageSize);
            int totalCount = count(pagination.getCountSQL(), params);
            List<T> data = queryForListWithMapParams(pagination.getPageSQL(), params);
            pageData = PageResult.builder().data(data).pageNo(pageNo).totalCount(totalCount).build();
            return pageData;
        }
        List<T> data = queryForListWithMapParams(sql, params);
        pageData = PageResult.builder().data(data).pageNo(0).totalCount(data.size()).build();
        return pageData;
    }

    protected Integer insertAndReturnId(PreparedStatementCreator preparedStatementCreator){
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(preparedStatementCreator,holder);
        return holder.getKey().intValue();
    }
}
