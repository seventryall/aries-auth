package com.star.aries.auth.server.endpoint;


import com.star.aries.auth.common.pojo.MsgResult;
import com.star.aries.auth.common.util.MsgResultUtil;
import com.star.aries.auth.dal.entity.Menu;
import com.star.aries.auth.dal.entity.SysUser;
import com.star.aries.auth.server.service.MenuService;
import com.star.aries.auth.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class MenuEndpoint {
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserService userService;

    /**
     * 创建菜单
     *
     * @param user
     * @param menu
     * @return
     */
    @RequestMapping(
            value = {"/menu"},
            method = {RequestMethod.POST}
    )
    public MsgResult<Menu> createMenu(Principal user, @RequestBody Menu menu) {
        SysUser userInfo = userService.queryCurUser(user);
       menuService.createMenu(userInfo, menu);
        return MsgResultUtil.buildSuccess(menu);
    }

    /**
     * 修改菜单
     *
     * @param user
     * @param menu
     * @return
     */
    @RequestMapping(
            value = {"/menu"},
            method = {RequestMethod.PUT}
    )
    public MsgResult<Menu> modifyMenu(Principal user,  @RequestBody Menu menu) {
        SysUser userInfo = userService.queryCurUser(user);
        menuService.modifyMenu(userInfo, menu);
        return MsgResultUtil.buildSuccess(menu);
    }

    /**
     * 删除菜单
     *
     * @param id
     * @return
     */
    @RequestMapping(
            value = {"/menu/{id}"},
            method = {RequestMethod.DELETE}
    )
    public MsgResult<Integer> deleteMenu(@PathVariable("id") Integer id) {
        Integer res = menuService.deleteMenu(id);
        return MsgResultUtil.buildSuccess(res);
    }


    /**
     * 菜单列表（树形结构）
     *
     * @param searchText
     * @return
     */
    @RequestMapping(value = "/menus", method = RequestMethod.GET)
    public MsgResult<List<Menu>> queryMenuList(String searchText) {
        List<Menu> list = menuService.queryMenuList(searchText);
        return MsgResultUtil.buildSuccess(list);
    }

    /**
     * 获取登录用户有权限的菜单和功能集合（树形结构）
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/authlist", method = RequestMethod.POST)
    public MsgResult<List<Menu>> queryMenuAuthList(Principal user) {
        SysUser userInfo = userService.queryCurUser(user);
        List<Menu> list = menuService.queryMenuAuthList(userInfo);
        return MsgResultUtil.buildSuccess(list);
    }

    /**
     * 菜单详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/menu/{id}", method = RequestMethod.GET)
    public MsgResult<Menu> queryMenu(@PathVariable("id") Integer id) {
        Menu menu = menuService.queryMenu(id);
        return MsgResultUtil.buildSuccess(menu);
    }
}
