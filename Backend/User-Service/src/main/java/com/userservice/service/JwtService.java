package com.userservice.service;

import org.springframework.stereotype.Service;
import java.security.Key;
import java.security.KeyStore.SecretKeyEntry;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.function.Function;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.management.RuntimeErrorException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private String secretKey = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    
//	 public JwtService() {
//		try {
//			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//			SecretKey sk = keyGen.generateKey();
//			secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			throw new RuntimeException(e);
//		}
//	}
//	 
//	 public void validateToken(final String token) {
//		 Jwts.parser()
//	        .verifyWith(getKey())  // Corrected way to set the signing key
//	        .build()
//	        .parseSignedClaims(token);	    }
//	 
    public boolean validateToken(String token) {
        try {
            Jws<Claims> res = Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return res != null; // Simplified return statement
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
//	public String generateToken(String username) {
//		
//		Map<String, Object> claims = new HashMap<>();
//		
//		return Jwts.builder()
//				.claims()
//				.add(claims)
//				.subject(username)
//				.issuedAt(new Date(System.currentTimeMillis()))
//				.expiration(new Date(System.currentTimeMillis()  + 1000 *60 *30 ))
//				.and()
//				.signWith(getKey())
//				.compact();
//	
//	}

	 public String generateToken(String userName) {
	        Map<String, Object> claims = new HashMap<>();
	        return createToken(claims, userName);
	    }

	    private String createToken(Map<String, Object> claims, String userName) {
	        return Jwts.builder()
	                .setClaims(claims)
	                .setSubject(userName)
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
	                .signWith(getKey()) // Default algorithm is HS256 if not specified
	                .compact();
	    }
	private SecretKey getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	
	public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

//    private Claims extractAllClaims(String token) {
//    	return Jwts.parser()
//                .verifyWith( getKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

//    public boolean validToken(String token, UserDetails userDetails) {
//        final String userName = extractUserName(token);
//        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
	
}
