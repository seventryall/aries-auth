package com.star.aries.auth.server.endpoint;

import com.star.aries.auth.common.pojo.MsgResult;
import com.star.aries.auth.common.pojo.PageResult;
import com.star.aries.auth.common.util.MsgResultUtil;
import com.star.aries.auth.dal.entity.SysUser;
import com.star.aries.auth.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class UserEndpoint {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user/current/info", method = RequestMethod.GET)
    public MsgResult<SysUser> queryCurUser(Principal user) {
        SysUser sysUser = userService.queryCurUser(user);
        return MsgResultUtil.buildSuccess(sysUser);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public MsgResult<SysUser> queryUser(@PathVariable("id") Integer id) {
        SysUser sysUser = userService.queryUser(id);
        return MsgResultUtil.buildSuccess(sysUser);
    }


    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public MsgResult<PageResult<List<SysUser>>> queryUserList(String searchText,
                                                              @RequestParam(required = true, defaultValue = "1") int pageNo,
                                                              @RequestParam(required = true, defaultValue = "20") int pageSize) {
        PageResult<List<SysUser>> res = userService.queryByPage(searchText, pageNo, pageSize);
        return MsgResultUtil.buildSuccess(res);
    }


    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public MsgResult<SysUser> createUser(Principal user, @RequestBody SysUser createUser) {
        SysUser curUser = userService.queryCurUser(user);
        userService.createUser(curUser, createUser);
        return MsgResultUtil.buildSuccess(createUser);
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public MsgResult<SysUser> modifyUser(Principal user, @RequestBody SysUser modifyUser) {
        SysUser curUser = userService.queryCurUser(user);
        userService.modifyUser(curUser, modifyUser);
        return MsgResultUtil.buildSuccess(modifyUser);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public MsgResult<Integer> deleteUser(@PathVariable("id") Integer id) {
        Integer res = userService.deleteUser(id);
        return MsgResultUtil.buildSuccess(res);
    }

}
