package com.star.aries.auth.server.endpoint;

import com.star.aries.auth.common.pojo.MsgResult;
import com.star.aries.auth.common.pojo.PageResult;
import com.star.aries.auth.common.util.MsgResultUtil;
import com.star.aries.auth.dal.entity.SysClient;
import com.star.aries.auth.dal.entity.Url;
import com.star.aries.auth.server.service.ClientService;
import com.star.aries.auth.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClientEndpoint {
    @Autowired
    private ClientService clientService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/client/{id}", method = RequestMethod.GET)
    public MsgResult<SysClient> queryClient(@PathVariable("id") Integer id) {
        SysClient sysClient = clientService.queryClient(id);
        return MsgResultUtil.buildSuccess(sysClient);
    }


    @RequestMapping(value = "/clients", method = RequestMethod.GET)
    public MsgResult<PageResult<List<SysClient>>> queryClientList(String searchText, @RequestParam(required = true, defaultValue = "1") int pageNo,
                                                                  @RequestParam(required = true, defaultValue = "20") int pageSize) {
        PageResult<List<SysClient>> res = clientService.queryClientList(searchText, pageNo, pageSize);
        return MsgResultUtil.buildSuccess(res);
    }


    @RequestMapping(value = "/client", method = RequestMethod.POST)
    public MsgResult<SysClient> createClient(@RequestBody SysClient sysClient) {
        clientService.createClient(sysClient);
        return MsgResultUtil.buildSuccess(sysClient);
    }

    @RequestMapping(value = "/client", method = RequestMethod.PUT)
    public MsgResult<Url> modifyClient(@RequestBody SysClient sysClient) {
        clientService.modifyClient(sysClient);
        return MsgResultUtil.buildSuccess(sysClient);
    }

    @RequestMapping(value = "/client/{id}", method = RequestMethod.DELETE)
    public MsgResult<Integer> deleteClient(@PathVariable("id") Integer id) {
        Integer res = clientService.deleteClient(id);
        return MsgResultUtil.buildSuccess(res);
    }
}
