package com.cg.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @atuther
 */
public class JwtUtil {

    //密钥
    public static final String SECRET = "sgfdsopljkhsl;o437632";
    //过期时间:秒 (改为24小时)
    public static final int EXPIRE = 3600 * 24;

    /**
     * 生成Token
     * @param employeeId
     * @return
     * @throws Exception
     */
    public static String createToken(Integer employeeId) throws Exception {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.SECOND, EXPIRE);
        Date expireDate = nowTime.getTime();

        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        String token = JWT.create()
                .withHeader(map)
                .withClaim("employeeId", employeeId)
                .withIssuedAt(new Date())
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(SECRET));

        System.out.println("Token 创建时间: " + new Date());
        System.out.println("Token 过期时间: " + expireDate);
        System.out.println("生成的 Token: " + token);

        return token;
    }

    /**
     * 验证Token
     * @param token
     * @return
     * @throws Exception
     */
    public static Map<String, Claim> verifyToken(String token) throws Exception {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET))
                    .build();
            
            DecodedJWT jwt = verifier.verify(token);
            
            // 检查是否过期
            Date expiresAt = jwt.getExpiresAt();
            if (expiresAt != null && expiresAt.before(new Date())) {
                throw new RuntimeException("Token has expired");
            }
            
            System.out.println("Token verification successful");
            System.out.println("Expiration time: " + expiresAt);
            
            return jwt.getClaims();
        } catch (JWTVerificationException e) {
            System.out.println("Token verification failed: " + e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Token verification error: " + e.getMessage());
            throw new RuntimeException("Token verification error", e);
        }
    }

    /**
     * 解析Token
     * @param token
     * @return
     */
    public static Map<String, Claim> parseToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaims();
        } catch (Exception e) {
            System.out.println("Token parsing error: " + e.getMessage());
            throw new RuntimeException("Failed to parse token", e);
        }
    }

}
