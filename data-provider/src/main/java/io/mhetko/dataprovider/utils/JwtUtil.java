package io.mhetko.dataprovider.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9eyJzdWIiOiIxMjM0IiwiYXVkIjpbImFkbWluIl0sImlzcyI6Im1hc29uLm1ldGFtdWcubmV0IiwiZXhwIjox";
    private static final long EXPIRATION_TIME = 86400000; // 24 godziny

    public static String createJWT(String id, String issuer, String subject, long ttlMillis) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(key, SignatureAlgorithm.HS256);

        if (ttlMillis >= 0) {
            Date exp = new Date(nowMillis + ttlMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    public static Claims decodeJWT(String jwt) {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        Jws<Claims> jws = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwt);
        return jws.getBody();
    }
}