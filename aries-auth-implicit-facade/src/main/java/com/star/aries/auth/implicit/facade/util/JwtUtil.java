package com.star.aries.auth.implicit.facade.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.aries.auth.implicit.facade.config.AuthClientConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private ObjectMapper objectMapper = new ObjectMapper();
    private Signer signer;
    private SignatureVerifier verifier;

    @Autowired
    AuthClientConfigProperties authClientConfigProperties;

    public Jwt decode(String token) {
        try {
            setVerifier();
            Jwt jwt = JwtHelper.decodeAndVerify(token, verifier);
            return jwt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> decodeHeader(String token) {
        Map<String, String> headers = JwtHelper.headers(token);
        return headers;
    }

    public String encode(String content, Map<String, String> headers, Integer expires) {
        String token = null;
        try {
            setSigner();
            Map<String, Object> map = objectMapper.readValue(content, Map.class);
            map.put("exp", expires);
            content = objectMapper.writeValueAsString(map);
            token = JwtHelper.encode(content, signer, headers).getEncoded();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return token;
    }

    public boolean isExpired(Jwt jwt) throws Exception {
        try {
            Long expiration=getExpiration(jwt);
            return new Date(expiration).before(new Date());
        } catch (Exception ex) {
            throw ex;
        }
    }

    public Long getExpiration(Jwt jwt) throws Exception{
        try {
            Map<String, Object> map = objectMapper.readValue(jwt.getClaims(), Map.class);
            return (Integer) map.get("exp") * 1000L;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void setSigner() {
        if (signer == null) {
            signer = new MacSigner(authClientConfigProperties.getJwtSignKey());
        }
    }

    private void setVerifier() {
        if (verifier == null) {
            verifier = new MacSigner(authClientConfigProperties.getJwtSignKey());
        }
    }

}
