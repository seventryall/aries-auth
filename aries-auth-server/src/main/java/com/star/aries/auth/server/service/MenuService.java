package com.star.aries.auth.server.service;

import com.google.common.collect.Lists;
import com.star.aries.auth.common.enums.StatusType;
import com.star.aries.auth.common.exception.CommonRuntimeException;
import com.star.aries.auth.dal.entity.Menu;
import com.star.aries.auth.dal.entity.MenuAction;
import com.star.aries.auth.dal.entity.SysUser;
import com.star.aries.auth.dal.repository.ClientRepository;
import com.star.aries.auth.dal.repository.MenuActionRepository;
import com.star.aries.auth.dal.repository.MenuRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService extends BaseService {
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private MenuActionRepository menuActionRepository;
    @Autowired
    private ClientRepository clientRepository;


    public Menu createMenu(SysUser userInfo, Menu menu) {
        Assert.notNull(userInfo,"请先登录！");
        Assert.notNull(menu, "参数不能为空!");
        checkParam(menu, true);
        setDefaultValue(menu, userInfo, true);
        if (CollectionUtils.isEmpty(menu.getMenuActions())) {
            int menuId = menuRepository.insert(menu);
            menu.setId(menuId);
            return menu;
        } else {
            transactionExecute(() -> {
                int menuId = menuRepository.insert(menu);
                menu.setId(menuId);
                menu.getMenuActions().forEach(a -> {
                    a.setMenuId(menuId);
                });
                menuActionRepository.insert(menu.getMenuActions());
                return menuId;
            }, String.format("创建菜单【%s】失败", menu.getName()));
            return menu;
        }
    }

    public Menu modifyMenu(SysUser userInfo, Menu menu) {
        Assert.notNull(userInfo,"请先登录！");
        Assert.notNull(menu, "参数不能为空!");
        checkParam(menu, false);
        setDefaultValue(menu, userInfo, false);
        transactionExecute(() -> {
            int res = menuRepository.update(menu);
            menuActionRepository.deleteByMenuId(menu.getId());
            menuActionRepository.insert(menu.getMenuActions());
            return res;
        }, String.format("修改菜单【%s】失败", menu.getName()));
        return menu;
    }

    private void setDefaultValue(Menu menu, SysUser userInfo, boolean insert) {
        if (menu.getSort() == null) {
            menu.setSort(-1);
        }
        if (insert) {
            if (menu.getPid() == null) {
                menu.setPid(-1);
            }
            menu.setCreator(userInfo.getId());
            menu.setCreateTime(new Date());
        } else {
            menu.setModifier(userInfo.getId());
            menu.setModifyTime(new Date());
        }
        if (!CollectionUtils.isEmpty(menu.getMenuActions())) {
            menu.getMenuActions().forEach(a -> {
                a.setClientId(menu.getClientId());
                if (!insert) {
                    a.setMenuId(menu.getId());
                }
                a.setCreator(userInfo.getId());
                a.setGmtCreate(new Date());
                a.setModifier(userInfo.getId());
                a.setGmtModify(new Date());
            });
        }
    }

    private void checkParam(Menu menu, boolean insert) {
        Assert.notNull(menu, "参数有误!");
        Assert.hasText(menu.getName(), "名称不能为空！");
        Assert.hasText(menu.getCode(), "编码不能为空！");
        Assert.notNull(menu.getClientId(), "应用不能为空！");
        Assert.notNull(menu.getType(), "菜单类型不能为空！");
        if (!insert) {
            Assert.notNull(menu.getId(), "菜单id不能为空！");
        }
        if (!CollectionUtils.isEmpty(menu.getMenuActions())) {
            menu.getMenuActions().forEach(a -> {
                Assert.hasText(a.getName(), "名称不能为空！");
                Assert.hasText(a.getCode(), "编码不能为空！");
                Assert.notNull(a.getValue(), "值不能为空！");
            });
        }
    }

    public Integer deleteMenu(Integer menuId) {
        Assert.notNull(menuId, "参数不能为空!");
        int count = menuRepository.selectCount(menuId);
        if (count > 0) {
            throw new CommonRuntimeException(StatusType.FAIL, "该菜单下还有子菜单，请先删除子菜单！");
        }
        return transactionExecute(() -> {
            int res = menuRepository.delete(menuId);
            menuActionRepository.deleteByMenuId(menuId);
            return res;
        }, "删除菜单失败");
    }

    public List<Menu> queryMenuList(String searchText) {
        List<Menu> allMenus = menuRepository.selectList(null);
        if (StringUtils.isBlank(searchText)) {
            return buildMenuTree(allMenus, null);
        }
        List<Menu> resMenus = allMenus;
        List<Menu> queryMenus = menuRepository.selectList(searchText);
        if (queryMenus.size() != allMenus.size()) {
            List<Menu> extMenus = Lists.newArrayList();
            extMenus.addAll(queryMenus);
            queryMenus.stream().forEach(d -> {
                //获取子菜单
                if (d.getPid() == -1) {
                    querySubMenus(d.getId(), allMenus, extMenus);
                } else {
                    //获取父菜单和子菜单
                    querySubMenus(d.getId(), allMenus, extMenus);
                    queryParentMenus(d.getPid(), allMenus, extMenus);
                }
            });
            resMenus = extMenus;
        }
        List<Menu> list = buildMenuTree(resMenus, null);
        return list;
    }

    private void queryParentMenus(Integer menuId, List<Menu> allMenus, List<Menu> resultMenus) {
        //从结果集合中查找是否存在该菜单
        Menu parentMenu = resultMenus.stream().filter(m -> m.getId().equals(menuId)).findFirst().orElse(null);
        if (parentMenu == null) {
            //结果集合不存在，需要从所有集合中查找该菜单并加入结果集合中
            parentMenu = allMenus.stream().filter(m -> m.getId().equals(menuId)).findFirst().orElse(null);
            resultMenus.add(parentMenu);
            if (parentMenu.getPid() != -1) {
                queryParentMenus(parentMenu.getPid(), allMenus, resultMenus);
            }
        }
    }


    private void querySubMenus(Integer pid, List<Menu> allMenus, List<Menu> resultMenus) {
        List<Menu> subMenus = allMenus.stream().filter(m -> pid.equals(m.getPid()))
                .collect(Collectors.toList());
        //从结果集合中查找是否存在这些子菜单，不存在就加入
        subMenus.forEach(m -> {
            Menu device =
                    resultMenus.stream().filter(m1 -> m1.getId().equals(m.getId())).findFirst().orElse(null);
            if (device == null) {
                resultMenus.add(m);
            }
            querySubMenus(m.getId(), allMenus, resultMenus);
        });
    }


    private List<Menu> buildMenuTree(List<Menu> menuList, Menu parentMenu) {
        List<Menu> subMenus;
        if (parentMenu == null) {
            subMenus = menuList.stream().filter(m -> m.getPid() == -1).collect(Collectors.toList());
        } else {
            subMenus =
                    menuList.stream().filter(m -> parentMenu.getId().equals(m.getPid())).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(subMenus)) {
            return null;
        }
        if (parentMenu != null) {
            parentMenu.setSubMenus(subMenus);
        }
        for (Menu menu : subMenus) {
            buildMenuTree(menuList, menu);
        }
        return subMenus;
    }

    public List<Menu> queryMenuAuthList(SysUser userInfo) {
        Assert.notNull(userInfo,"请先登录！");
        //获取当前登录用户有权限的菜单和功能集合
        List<Menu> menus = menuRepository.selectAuthListByUserId(userInfo.getId());
        List<MenuAction> menuActions = menuActionRepository.selectAuthListByUserId(userInfo.getId());
        menus.forEach(m -> {
            m.setMenuActions(menuActions.stream().filter(ma -> ma.getMenuId().equals(m.getId())).collect(Collectors.toList()));
        });
         List<Menu> res=buildMenuTree(menus,null);
        return res;
    }

    public Menu queryMenu(Integer menuId) {
        Assert.notNull(menuId, "参数不能为空!");
        Menu menu = menuRepository.selectMenu(menuId);
        if (menu == null) {
            return null;
        }
        List<MenuAction> menuActions = menuActionRepository.selectList(menuId);
        menu.setMenuActions(menuActions);
        return menu;
    }



}
