package com.star.aries.auth.server.security;

import com.alibaba.fastjson.JSON;
import com.star.aries.auth.dal.entity.SysClient;
import com.star.aries.auth.dal.repository.ClientRepository;
import com.star.aries.auth.server.property.SecurityOAuth2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 客户端信息初始化
 */
@Component
public class AriesClientDetailsService implements ClientDetailsService, ClientRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(AriesClientDetailsService.class);

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    SecurityOAuth2Properties securityOAuth2Properties;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        try {
            SysClient client = clientRepository.selectByClientId(clientId);
            if(client.getAccessTokenValidity()==null){
                client.setAccessTokenValidity(securityOAuth2Properties.getAccessTokenValiditySeconds());
            }
            ClientDetails clientDetails = ClientDetailsConvert.convert(client);
            return clientDetails;
        } catch (EmptyResultDataAccessException ex) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        } catch (Exception ex) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {

    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {

    }

    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {

    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {

    }

    @Override
    public List<ClientDetails> listClientDetails() {
        return null;
    }

    private static class ClientDetailsConvert {
        public static ClientDetails convert(SysClient c) {
            ExtendsBaseClientDetails details = new ExtendsBaseClientDetails(c.getClientId(), c.getResources(),
                    c.getScope(),
                    c.getAuthorizedGrantTypes(), c.getRoles(), c.getWebServerRedirectUri());
            details.setClientSecret(new BCryptPasswordEncoder().encode(c.getClientSecret()));
            if (c.getAccessTokenValidity() != null) {
                details.setAccessTokenValiditySeconds(c.getAccessTokenValidity());
            } else {
                details.setAccessTokenValiditySeconds(3600);
            }
            if (c.getRefreshTokenValidity() != null) {
                details.setRefreshTokenValiditySeconds(c.getRefreshTokenValidity());
            } else {
                details.setRefreshTokenValiditySeconds(2592000);
            }
            String json = c.getAdditionalInformation();
            if (json != null) {
                try {
                    Map<String, Object> additionalInformation = JSON.parseObject(json, Map.class);
                    details.setAdditionalInformation(additionalInformation);
                } catch (Exception var6) {
                    AriesClientDetailsService.logger.warn("Could not decode JSON for additional information: " +
                            details, var6);
                }
            }
            String scopes = c.getScope();
            if (scopes != null) {
                details.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(scopes));
            }
            return details;
        }
    }
}
