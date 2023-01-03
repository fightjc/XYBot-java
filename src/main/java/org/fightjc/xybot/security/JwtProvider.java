package org.fightjc.xybot.security;

import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.fightjc.xybot.service.impl.GroupSwitchServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
public class JwtProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    /**
     * THIS IS NOT A SECURE PRACTICE! For simplicity, we are storing a static key here. Ideally, in a
     * microservices environment, this key would be kept on a config-server.
     */
    @Value("${security.jwt.secretKey}")
    private String secretKey;

    private final Duration expiration = Duration.ofDays(1);

    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
//        claims.put("key", "value"); // add more info

        Date validity = new Date(System.currentTimeMillis() + expiration.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(validity) // 过期时间
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        if (TextUtils.isEmpty(token)) {
            return null;
        }

        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            e.printStackTrace();
            logger.error("token解析失败:{}", e.toString());
        }
        return claims;
    }
}
