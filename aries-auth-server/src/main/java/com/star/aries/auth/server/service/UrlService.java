package com.star.aries.auth.server.service;


import com.star.aries.auth.common.pojo.PageResult;
import com.star.aries.auth.dal.entity.SysUser;
import com.star.aries.auth.dal.entity.Url;
import com.star.aries.auth.dal.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;

    public PageResult<List<Url>> queryByPage(String searchText, Integer pageNo, Integer pageSize) {
        PageResult<List<Url>> res = urlRepository.selectByPage(searchText, pageNo, pageSize);
        return res;
    }

    public Url createUrl(SysUser user, Url url) {
        Assert.notNull(user, "请先登录！");
        Assert.notNull(url, "参数不能为空！");
        checkParam(url, true);
        setDefaultValue(user,url,true);
        int id = urlRepository.insert(url);
        url.setId(id);
        return url;
    }

    public Url modifyUrl(SysUser user, Url url) {
        Assert.notNull(user, "请先登录！");
        Assert.notNull(url, "参数不能为空！");
        checkParam(url, false);
        setDefaultValue(user,url,false);
        urlRepository.update(url);
        return url;
    }

    private void checkParam(Url url, boolean insert) {
        if (!insert) {
            Assert.notNull(url.getId(), "id不能为空！");
        }
        Assert.hasText(url.getUrl(), "路径不能为空！");
        Assert.hasText(url.getModuleName(), "模块名称不能为空!");
    }

    private void setDefaultValue(SysUser user, Url url, boolean insert) {
        if (insert) {
            url.setCreator(user.getId());
            url.setCreateTime(new Date());
        } else {
            url.setModifier(user.getId());
            url.setModifyTime(new Date());
        }
    }

    public Url queryById(Integer id) {
        Assert.notNull(id, "参数不能为空!");
        Url url = urlRepository.selectUrlById(id);
        return url;
    }

    public Integer deleteUrl(Integer id) {
        Assert.notNull(id, "参数不能为空!");
        return urlRepository.delete(id);
    }

//    public List<Url> queryByClientId(String clientId) {
//        List<Url> res = urlRepository.selectByClientId(clientId);
//        return res;
//    }

}
