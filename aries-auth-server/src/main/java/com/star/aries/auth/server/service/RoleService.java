package com.star.aries.auth.server.service;


import com.google.common.collect.Lists;
import com.star.aries.auth.common.pojo.PageResult;
import com.star.aries.auth.dal.entity.*;
import com.star.aries.auth.dal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService extends BaseService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleUserRepository roleUserRepository;
    @Autowired
    private RoleUrlRepository roleUrlRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UrlRepository urlRepository;

    public Role createRole(SysUser user, Role role) {
        Assert.notNull(user, "请先登录！");
        Assert.notNull(role, "参数不能为空！");
        checkParam(role, true);
        setDefaultValue(user, role, true);
        //url空，只新增角色
        if (CollectionUtils.isEmpty(role.getUrls())) {
            roleRepository.insert(role);
        } else {
            //同时新增角色和角色Url关联表
            transactionExecute(() -> {
                int roleId = roleRepository.insert(role);
                role.setId(roleId);
                List<RoleUrl> roleUrls =
                        role.getUrls().stream().map(a -> new RoleUrl(roleId, a.getId())).collect(Collectors.toList());
                roleUrlRepository.insert(roleUrls);
                return role;
            }, String.format("创建角色【%s】失败", role.getName()));
        }
        return role;
    }

    public Role modifyRole(SysUser user, Role role) {
        Assert.notNull(user, "请先登录！");
        Assert.notNull(role, "参数不能为空！");
        checkParam(role, false);
        setDefaultValue(user, role, false);
        transactionExecute(() -> {
            int res = roleRepository.update(role);
            //修改角色url
            roleUrlRepository.deleteByRoleId(role.getId());
            if (!CollectionUtils.isEmpty(role.getUrls())) {
                List<RoleUrl> roleUrls =
                        role.getUrls().stream().map(a -> new RoleUrl(role.getId(),
                                a.getId())).collect(Collectors.toList());
                roleUrlRepository.insert(roleUrls);
            }
            //修改角色用户
            roleUserRepository.deleteByRoleId(role.getId());
            if (!CollectionUtils.isEmpty(role.getUsers())) {
                List<RoleUser> roleUsers =
                        role.getUsers().stream().map(a -> new RoleUser(role.getId(),
                                a.getId())).collect(Collectors.toList());
                roleUserRepository.insert(roleUsers);
            }
            return res;
        }, String.format("修改角色【%s】失败", role.getName()));
        return role;
    }

    private void checkParam(Role role, boolean insert) {
        Assert.notNull(role, "参数有误!");
        if (!insert) {
            Assert.notNull(role.getId(), "角色id不能为空！");
        }
        Assert.hasText(role.getCode(), "角色名称不能为空！");
        Assert.hasText(role.getName(), "角色名称不能为空！");
    }

    private void setDefaultValue(SysUser user, Role role, boolean insert) {
        if (insert) {
            role.setCreator(user.getId());
            role.setCreateTime(new Date());
        } else {
            role.setModifier(user.getId());
            role.setModifyTime(new Date());
        }
    }

    public Integer deleteRole(Integer roleId) {
        Assert.notNull(roleId, "参数不能为空!");
        return transactionExecute(() -> {
            //删除角色，角色策略，角色用户
            int res = roleRepository.delete(roleId);
            roleUrlRepository.deleteByRoleId(roleId);
            roleUserRepository.deleteByRoleId(roleId);
            return res;
        }, "删除角色失败");
    }

    public Role queryById(Integer roleId) {
        Assert.notNull(roleId, "参数不能为空!");
        Role role = roleRepository.selectByRoleId(roleId);
        if (role == null) {
            return null;
        }
        List<Url> urls = urlRepository.selectUrlsByRoleId(role.getId());
        role.setUrls(urls);
        List<SysUser> users = userRepository.selectUsersByRoleId(role.getId());
        role.setUsers(users);
        return role;
    }

    public PageResult<List<Role>> queryByPage(String searchText, int pageNo, int pageSize) {
        return roleRepository.selectRoleListByPage(searchText, pageNo, pageSize);
    }

    public List<UrlNode> queryUrls(Integer roleId) {
        Assert.notNull(roleId, "角色不能为空");
        List<Url> urls = urlRepository.selectUrlsByRoleId(roleId);
        //url去重
        List<Url> uniqueUrls = urls.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Url::getUrl))), ArrayList::new)
        );
        //应用去重
        List<String> resourceList =
                uniqueUrls.stream().map(Url::getClientName).distinct().collect(Collectors.toList());
        List resList = Lists.newArrayList();
        resourceList.forEach(r -> {
            UrlNode resourceNode = UrlNode.builder().name(r).build();
            //模块去重
            List<String> moduleList = uniqueUrls.stream().filter(u -> u.getClientName().equals(r))
                    .map(Url::getModuleName).distinct().collect(Collectors.toList());
            List moduleNodes = Lists.newArrayList();
            moduleList.forEach(m -> {
                UrlNode moduleNode = UrlNode.builder().name(m).build();
                List<UrlNode> urlNodes =
                        uniqueUrls.stream().filter(u -> u.getModuleName().equals(m) && u.getClientName().equals(r))
                                .map(u -> UrlNode.builder().name(u.getName()).build()).collect(Collectors.toList());
                moduleNode.setSubNodes(urlNodes);
                moduleNodes.add(moduleNode);
            });
            resourceNode.setSubNodes(moduleNodes);
            resList.add(resourceNode);
        });
        return resList;
    }

    public List<Role> queryRolesByUrl(String url){
        return roleRepository.selectRolesByUrl(url);
    }

}
