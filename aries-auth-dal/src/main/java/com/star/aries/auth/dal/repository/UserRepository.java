package com.star.aries.auth.dal.repository;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.star.aries.auth.common.enums.StatusType;
import com.star.aries.auth.common.exception.CommonRuntimeException;
import com.star.aries.auth.common.exception.DuplicateUniqueException;
import com.star.aries.auth.common.pojo.PageResult;
import com.star.aries.auth.common.util.IdWorker;
import com.star.aries.auth.dal.entity.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@Repository
public class UserRepository extends AbstractJdbcDaoSupport<SysUser> {

    @Autowired
    private IdWorker idWorker;

    public SysUser findByPhoneOrEmailOrAccountWithoutPwd(String username) {
        SysUser user = findByPhoneOrEmailOrAccount(username);
        user.setPassword(null);
        user.setSalt(null);
        return user;
    }

    public SysUser findByPhoneOrEmailOrAccount(String username) {
        return super.queryForObject("select "+
                " id,name,phone,email,password,account,salt" +
                " from ma_user_info" +
                " where status=1 and (is_deleted=0 or is_deleted is null) and phone = ? or account = ? or email = ?", username, username, username);
    }

    public SysUser selectByUserId(Integer userId) {
        return super.queryForObject("select" +
                " id,name,phone,email,password,account,salt" +
                " from ma_user_info" +
                " where status=1 and (is_deleted=0 or is_deleted is null) and id = ?", userId);
    }

    public int insert(SysUser user) {
        Assert.notNull(user, "sysUser is not null");
        Assert.notNull(user.getPassword(), "密码不能为空!");
        //user.setUserId(String.valueOf(idWorker.nextId()));
        user.setStatus(1); //1是启用 0是停用
        try {
            return super.insert("insert into ma_user_info (account, phone, email, name, " +
                            "password, salt, type, merchant_id, status,creator) values(?,?,?,?,?,?,?,?,?,?)",
                    new Object[]{user.getAccount(), user.getPhone(), user.getEmail(), user.getName(),
                            user.getPassword(), user.getSalt(), user.getType(), user.getMerchantId(), user.getStatus(),user.getCreator()});
        } catch (DuplicateKeyException ex) {
            throw new DuplicateUniqueException(JSON.toJSONString(user), String.format("SysUser:账号[%s]或手机[%s]或邮箱[%s]已经存在!",
                    user.getAccount(), user.getPhone(), user.getEmail()));
        } catch (Exception e) {
            throw new CommonRuntimeException(StatusType.DB_ERROR, "新增用户失败");
        }
    }

    public int updatePassword(String username, String password) {
        Assert.notNull(password, "参数不能为空!");
        SysUser sysUser = findByPhoneOrEmailOrAccount(username);
        try {
            return super.update("update ma_user_info set password = ? where id = ?", password, sysUser.getId());
        } catch (Exception e) {
            throw new CommonRuntimeException(StatusType.DB_ERROR, "更新密码失败");
        }
    }

    public List<SysUser> selectUsersByRoleId(Integer roleId) {
        Assert.notNull(roleId, "参数不能为空!");
        StringBuffer sql = new StringBuffer("select"+
                " u.id,name,phone,email,password,account,salt" +
                " from ma_user_info u, ma_auth_role_user ru" +
                " where u.status=1 and (u.is_deleted=0 or u.is_deleted is null) and u.id = ru.user_id and ru.role_id = :roleId");
        Map<String, Object> params = Maps.newHashMap();
        params.put("roleId", roleId);
        return super.queryForListWithMapParams(sql.toString(), params);
    }

     public PageResult<List<SysUser>> selectByPage(String searchText, Integer pageNo, Integer pageSize) {
        StringBuffer sql = new StringBuffer("select"+
                " id,name,phone,email,password,account,salt,type,status,create_time,modify_time" +
                " from ma_user_info" +
                " where status=1 and (is_deleted=0 or is_deleted is null)"
        );
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(searchText)) {
            sql.append(" and (name = :searchText or account = :searchText or email = :searchText or phone = :searchText)");
            params.put("searchText", searchText);
        }
        return super.queryForListWithMapParamsByPage(sql.toString(), pageNo, pageSize, params);
    }

    public Integer update(SysUser user) {
        Assert.notNull(user, "参数不能为空!");
        List<Object> paramList = Lists.newArrayList();
        StringBuffer stringBuffer = new StringBuffer("update ma_user_info set modify_time=CURRENT_TIMESTAMP()" +
                ",modifier=?");
        paramList.add(user.getModifier());

        append(stringBuffer, paramList, "name", user.getName());
        append(stringBuffer, paramList, "phone", user.getPhone());
        append(stringBuffer, paramList, "email", user.getEmail());

        stringBuffer.append(" where id=?");
        paramList.add(user.getId());
        return super.update(stringBuffer.toString(), paramList.toArray());
    }

    private void append(StringBuffer stringBuffer, List paramList, String fieldName, Object val) {
        if (val != null && StringUtils.isNotBlank(val.toString())) {
            stringBuffer.append(String.format(",%s=?", fieldName));
            paramList.add(val);
        }
    }

    public Integer delete(Integer userId) {
        Assert.notNull(userId, "参数不能为空!");
        return super.delete("update ma_user_info set is_deleted=1 where id = ?", userId);
    }

}
