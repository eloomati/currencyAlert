package io.mhetko.dataprovider.utils;

public class JwtUtil {
    private static final String SECRET_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9eyJzdWIiOiIxMjM0IiwiYXVkIjpbImFkbWluIl0sImlzcyI6Im1hc29uLm1ldGFtdWcubmV0IiwiZXhwIjox";
    private static final long EXPIRATION_TIME = 86400000; // 24 godziny

    public static String createJWT(String id, String issuer, String subject, long ttlMillis) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        JwtBuilder builder = Jwts.builder()
                .id(id)
                .issuer(issuer)
                .subject(subject)
                .issuedAt(now)
                .signWith(key, SignatureAlgorithm.HS256);

        if (ttlMillis >= 0) {
            Date exp = new Date(nowMillis + ttlMillis);
            builder.expiration(exp);
        }

        return builder.compact();
    }

    public static Claims decodeJWT(String jwt) {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt);
        return jws.getPayload();
    }
}
