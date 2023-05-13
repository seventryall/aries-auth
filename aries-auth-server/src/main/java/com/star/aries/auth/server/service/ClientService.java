package com.star.aries.auth.server.service;

import com.star.aries.auth.common.pojo.PageResult;
import com.star.aries.auth.common.util.Md5;
import com.star.aries.auth.common.util.StringRandomUtil;
import com.star.aries.auth.dal.entity.SysClient;
import com.star.aries.auth.dal.repository.ClientRepository;
import com.star.aries.auth.dal.repository.ClientResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

@Service
public class ClientService extends BaseService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientResourceRepository clientResourceRepository;

    public SysClient createClient(SysClient sysClient) {
        Assert.notNull(sysClient, "参数不能为空!");
        checkParam(sysClient, true);
        setDefaultValue(sysClient);
        clientRepository.insert(sysClient);
        return sysClient;
    }

    public SysClient modifyClient(SysClient sysClient) {
        Assert.notNull(sysClient, "参数不能为空!");
        checkParam(sysClient, false);
        sysClient.setModifyTime(new Date());
        clientRepository.update(sysClient);
        return sysClient;
    }

    private void checkParam(SysClient sysClient, boolean insert) {
        Assert.hasText(sysClient.getClientId(), "应用编号不能为空！");
        Assert.hasText(sysClient.getClientName(), "应用名不能为空!");
        //Assert.notNull(sysClient.getClientType(), "客户端类型不能为空，请选择应用或者服务！");
        if (!insert) {
            Assert.notNull(sysClient.getId(), "应用id不能为空");
        }
    }

    private void setDefaultValue(SysClient sysClient) {
        sysClient.setClientSecret(Md5.getInstance().md5_32(StringRandomUtil.randomStr(8)));
        sysClient.setScope("read,write,trust");
        sysClient.setAuthorizedGrantTypes(" password,authorization_code,refresh_token");
//        if (sysClient.getClientType() == 1) {
//            sysClient.setAuthorizedGrantTypes("password,client_credentials,refresh_token");
//        } else {
//            sysClient.setAuthorizedGrantTypes("client_credentials");
//        }
        sysClient.setResourceStatus(0);
        sysClient.setAutoApprove("1");
        sysClient.setCreateTime(new Date());
    }

    public Integer deleteClient(Integer id) {
        Assert.notNull(id, "参数不能为空！");
        return clientRepository.delete(id);
    }

    public SysClient queryClient(Integer id) {
        Assert.notNull(id, "参数不能为空");
        return clientRepository.selectById(id);
    }

    public PageResult<List<SysClient>> queryClientList(String searchText, Integer pageNo, Integer pageSize) {
        return clientRepository.selectListByPage(searchText, pageNo, pageSize);
    }
}
