package com.star.aries.auth.server.endpoint;


import com.star.aries.auth.common.pojo.MsgResult;
import com.star.aries.auth.common.pojo.PageResult;
import com.star.aries.auth.common.util.MsgResultUtil;
import com.star.aries.auth.dal.entity.SysUser;
import com.star.aries.auth.dal.entity.Url;
import com.star.aries.auth.server.service.UrlService;
import com.star.aries.auth.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class UrlEndpoint {
    @Autowired
    private UrlService urlService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/url/{id}", method = RequestMethod.GET)
    public MsgResult<Url> queryUrl(@PathVariable("id") Integer id) {
        Url url = urlService.queryById(id);
        return MsgResultUtil.buildSuccess(url);
    }


    @RequestMapping(value = "/urls", method = RequestMethod.GET)
    public MsgResult<PageResult<List<Url>>> queryUrlList(String searchText, @RequestParam(required = true, defaultValue = "1") int pageNo,
                                                         @RequestParam(required = true, defaultValue = "20") int pageSize) {
        PageResult<List<Url>> res = urlService.queryByPage(searchText, pageNo, pageSize);
        return MsgResultUtil.buildSuccess(res);
    }


    @RequestMapping(value = "/url", method = RequestMethod.POST)
    public MsgResult<Url> createUrl(Principal user, @RequestBody Url url) {
        SysUser userInfo = userService.queryCurUser(user);
        urlService.createUrl(userInfo, url);
        return MsgResultUtil.buildSuccess(url);
    }

    @RequestMapping(value = "/url", method = RequestMethod.PUT)
    public MsgResult<Url> modifyUrl(Principal user,@RequestBody Url url) {
        SysUser userInfo = userService.queryCurUser(user);
        urlService.modifyUrl(userInfo, url);
        return MsgResultUtil.buildSuccess(url);
    }

    @RequestMapping(value = "/url/{id}", method = RequestMethod.DELETE)
    public MsgResult<Integer> deleteUrl(@PathVariable("id") Integer id) {
        Integer res = urlService.deleteUrl(id);
        return MsgResultUtil.buildSuccess(res);
    }

//    @RequestMapping(
//            value = {"/list/all"},
//            method = {RequestMethod.POST}
//    )
//    public MsgResult<List<Url>> queryAll(@RequestParam(value = "clientId", required = false) String clientId) {
//        List<Url> list = urlService.queryByClientId(clientId);
//        return MsgResultUtil.buildSuccess(list);
//    }
}
