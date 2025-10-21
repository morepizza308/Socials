package de.joergmorbitzer.Socials.security;

import io.jsonwebtoken.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.*;

@Component
public class JwtTokenUtil implements Serializable {
    public static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60;

    private final String secret = "tokensecret";
    private final SecretKey key = Jwts.SIG.HS256.key().build();

    private Claims getClaimsFromToken(String token)
    {
        Claims claims = null;
        try
        {
            claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload();
        }
        catch (Exception e)
        {
            System.out.println("Fehler: " + e);
        }
        return claims;
    }
    public String getUserNameFromToken(String token)
    {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public Date getExpirationDateFromToken(String token)
    {
        return new Date();
    }

    public String generateToken(User user)
    {
        //Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
        //        .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token)
    {
        System.out.println("Start der Prozedur");
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(key)
                .build();
        try {
            jwtParser.parse(token);
            return true;
        }
        catch (Exception e) {
            System.out.println("Could not verify JWT token integrity!" + e);
            return false;
        }
    }
}
