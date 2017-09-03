/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.security;

import se.backede.webservice.exception.AuthorizationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
@Singleton
@Startup
public class KeyProvider {

    final Logger log = LoggerFactory.getLogger(KeyProvider.class);

    public Key KEY = MacProvider.generateKey();

    public String generateJWT(String user, UriInfo uriInfo) throws AuthorizationException {

        log.debug("KEY {}", KEY.toString());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = now.plusMinutes(15L);

        Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
        Instant expireDate = expire.atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .setSubject(user)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(Date.from(instant))
                .setExpiration(Date.from(expireDate))
                .signWith(SignatureAlgorithm.HS512, KEY)
                .compact();
    }

    public Boolean isJWTOk(String JWTToken) throws AuthorizationException {
        try {
            Jwts.parser().setSigningKey(KEY).parseClaimsJws(JWTToken);
        } catch (SignatureException e) {
            log.error("Error when checking if JWT token is ok ErrorMessage: {}", e.getMessage());
            throw new AuthorizationException("JWT token not ok!");
        }
        return true;
    }

}
