package com.yonyougov.bootchat.auth.config;

import com.yonyougov.bootchat.base.user.User;
import com.yonyougov.bootchat.base.user.UserService;
import com.yonyougov.bootchat.serializer.CruxSerializer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtTokenGeneratorImpl implements TokenGenerator {

    private final String secretKey;

    private final int tokenTimeout;
    private final UserService userService;
    private final CruxSerializer cruxSerializer;

    public JwtTokenGeneratorImpl(
            @Value("${yondif.jwt.secret-key:'ibootchat'}")
            String secretKey,
            @Value("${yondif.jwt.token-timeout:86400000}")
            int tokenTimeout, UserService userService, CruxSerializer cruxSerializer) {
        this.secretKey = secretKey;
        this.tokenTimeout = tokenTimeout;
        this.userService = userService;
        this.cruxSerializer = cruxSerializer;
    }

    @Override
    public String generator(User user) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = Base64.decodeBase64(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder().claim("id", user.getId())
                .claim("email", user.getEmail())
                .signWith(signingKey, signatureAlgorithm)
                .setExpiration(DateUtils.addMilliseconds(new Date(), tokenTimeout))
                .serializeToJsonWith(stringMap -> cruxSerializer.serialize(stringMap).getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    @SuppressWarnings("unchecked")
    @Override
    public User decodeToken(String token) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = Base64.decodeBase64(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .deserializeJsonWith(bytes -> cruxSerializer.deserialize(new String(bytes, StandardCharsets.UTF_8), Map.class))
                .build()
                .parseClaimsJws(token)
                .getBody();
        String userId = claims.get("id", String.class);
        return userService.findById(userId);
    }
}
