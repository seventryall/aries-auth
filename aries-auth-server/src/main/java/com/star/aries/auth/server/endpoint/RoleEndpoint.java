package com.star.aries.auth.server.endpoint;

import com.star.aries.auth.common.pojo.MsgResult;
import com.star.aries.auth.common.pojo.PageResult;
import com.star.aries.auth.common.util.MsgResultUtil;
import com.star.aries.auth.dal.entity.Role;
import com.star.aries.auth.dal.entity.SysUser;
import com.star.aries.auth.server.service.RoleService;
import com.star.aries.auth.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class RoleEndpoint {
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/role/{id}", method = RequestMethod.GET)
    public MsgResult<Role> queryRole(@PathVariable("id") Integer id) {
        Role role = roleService.queryById(id);
        return MsgResultUtil.buildSuccess(role);
    }


    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public MsgResult<PageResult<List<Role>>> queryRoleList(String searchText,
                                                           @RequestParam(required = true, defaultValue = "1") int pageNo,
                                                           @RequestParam(required = true, defaultValue = "20") int pageSize) {
        PageResult<List<Role>> res = roleService.queryByPage(searchText, pageNo, pageSize);
        return MsgResultUtil.buildSuccess(res);
    }


    @RequestMapping(value = "/role", method = RequestMethod.POST)
    public MsgResult<Role> createRole(Principal user, @RequestBody Role role) {
        SysUser userInfo = userService.queryCurUser(user);
        roleService.createRole(userInfo, role);
        return MsgResultUtil.buildSuccess(role);
    }

    @RequestMapping(value = "/role", method = RequestMethod.PUT)
    public MsgResult<Role> modifyRole(Principal user, @RequestBody Role role) {
        SysUser userInfo = userService.queryCurUser(user);
        roleService.modifyRole(userInfo, role);
        return MsgResultUtil.buildSuccess(role);
    }


    @RequestMapping(value = "/role/{id}", method = RequestMethod.DELETE)
    public MsgResult<Integer> deleteRole(@PathVariable("id") Integer id) {
        Integer res = roleService.deleteRole(id);
        return MsgResultUtil.buildSuccess(res);
    }

//    @RequestMapping(value = "/urls", method = RequestMethod.POST)
//    public MsgResult<List<UrlNode>> queryUrls(Integer roleId) {
//        List<UrlNode> res = roleService.queryUrls(roleId);
//        return MsgResultUtil.buildSuccess(res);
//    }

}
