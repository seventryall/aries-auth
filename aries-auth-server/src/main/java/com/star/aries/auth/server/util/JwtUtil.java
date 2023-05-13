package com.star.aries.auth.server.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String SIGN_KEY = "45bb2632-b737-423b-91f1-42849884428f";

    public static String createToken(Map<String, String> map, Integer expires) throws Exception {
//        //创建日历
//        Calendar instance = Calendar.getInstance();
//        //设置过期时间
//        instance.add(Calendar.SECOND, expires);

        //创建jwt builder对象
        JWTCreator.Builder builder = JWT.create();

        //payload
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });

        //指定过期时间
        String token = builder.withExpiresAt(new Date(System.currentTimeMillis()+expires*1000L))
                //设置加密方式
                .sign(Algorithm.HMAC256(SIGN_KEY));
        //返回tokean
        return token;
    }

    /**
     * 解析token
     *
     * @param token 输入混淆payload后的token
     */
    public static DecodedJWT verify(String token) throws Exception {
        //如果token无效
        if (token == null || "".equals(token)) {
            throw new JWTDecodeException("无效的token！");
        }
        //解析token
        //String dToken = deConfoundPayload(token);
        //创建返回结果
        return JWT.require(Algorithm.HMAC256(SIGN_KEY)).build().verify(token);

    }

    public static void main(String[] args) {
        try {
            String access_token="123";
            DecodedJWT verify = JwtUtil.verify(access_token);
            System.out.println("jti:" + verify.getClaim("jti").asString());
            System.out.println("exp:" + verify.getExpiresAt());
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}
