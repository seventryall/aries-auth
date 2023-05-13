package com.star.aries.auth.server.service;

import com.star.aries.auth.common.enums.StatusType;
import com.star.aries.auth.common.exception.CommonRuntimeException;
import com.star.aries.auth.common.pojo.PageResult;
import com.star.aries.auth.dal.entity.Role;
import com.star.aries.auth.dal.entity.SysUser;
import com.star.aries.auth.dal.repository.RoleRepository;
import com.star.aries.auth.dal.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    private final String HASH_USER = "platform-user";

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    public SysUser queryCurUser(Principal user) {
        if (user == null) {
            throw new CommonRuntimeException(StatusType.NO_LOGIN, "请先登录");
        }
        if (redisTemplate.opsForHash().hasKey(HASH_USER, user.getName())) {
            SysUser sysUserInfo = (SysUser) this.redisTemplate.opsForHash().get(HASH_USER, user.getName());
            return sysUserInfo;
        }
        SysUser sysUserInfo = userRepository.findByPhoneOrEmailOrAccountWithoutPwd(user.getName());
        if (sysUserInfo != null) {
            redisTemplate.opsForHash().put(HASH_USER, user.getName(), sysUserInfo);
        }
        return sysUserInfo;
    }

    public SysUser loadUserByUsername(String username) {
        SysUser user = userRepository.findByPhoneOrEmailOrAccount(username);
        return user;
    }

    public SysUser queryUser(Integer userId){
        Assert.notNull(userId,"用户id不能为空！");
        return userRepository.selectByUserId(userId);
    }

    public PageResult<List<SysUser>> queryByPage(String searchText, Integer pageNo, Integer pageSize){
        return userRepository.selectByPage(searchText,pageNo,pageSize);
    }

    public SysUser createUser(SysUser curUser,SysUser createUser) {
        Assert.notNull(curUser,"请先登录！");
        Assert.notNull(createUser,"参数不能为空！");
        if(StringUtils.isBlank(createUser.getPassword())){
            createUser.setPassword("123456");
        }
        String cryptPwd = new BCryptPasswordEncoder().encode(createUser.getPassword());
        createUser.setPassword(cryptPwd);
        createUser.setCreator(curUser.getId());
        createUser.setCreateTime(new Date());
        userRepository.insert(createUser);
        return createUser;
    }

    public SysUser modifyUser(SysUser curUser,SysUser modifyUser){
        Assert.notNull(curUser,"请先登录！");
        Assert.notNull(modifyUser,"参数不能为空！");
        modifyUser.setModifier(curUser.getId());
        modifyUser.setModifyTime(new Date());
        userRepository.update(modifyUser);
        return modifyUser;
    }

    public int modifyPassword(String username, String password) {
        String cryptPwd = new BCryptPasswordEncoder().encode(password);
        return userRepository.updatePassword(username, cryptPwd);
    }

    public int deleteUser(Integer userId){
        Assert.notNull(userId,"参数不能为空！");
        return userRepository.delete(userId);
    }

    public void logout(String username) {
        if (StringUtils.isNotBlank(username)) {
            if (redisTemplate.opsForHash().hasKey(HASH_USER, username)) {
                redisTemplate.opsForHash().delete(HASH_USER, username);
            }
        }
    }

    public List<Role> queryRolesByUserId(Integer userId){
        List<Role> list=roleRepository.selectRolesByUserId(userId);
        return list;
    }


}
