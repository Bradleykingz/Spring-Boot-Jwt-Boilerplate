package com.retroshift.springjwtauthboilerplate.utils;

import com.retroshift.springjwtauthboilerplate.spring.entity.UserEntity;
import com.retroshift.springjwtauthboilerplate.spring.jwt.JwtUser;
import com.retroshift.springjwtauthboilerplate.spring.persistence.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class JwtUtils {

    @Autowired
    private UserService userService;

    private static final String AUDIENCE_UNKNOWN = "unknown";
    private static final String AUDIENCE_WEB = "web";
    private static final String AUDIENCE_MOBILE = "mobile";
    private static final String AUDIENCE_TABLET = "tablet";

    private String SECRET = "BDDD4E6EA0AEEEAE60A6191217D4237F55F4EAC810728419BB153DB56A74BBE5";

    private Logger logger = Logger.getLogger(getClass().getName());

    private Clock clock = DefaultClock.INSTANCE;

    public String getClaimsFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getTokenFromHeader(String header) {
        if (!StringUtils.isEmpty(header))
            return header.replace("Bearer ", "");
        else return null;
    }

    public String getAuthorizationHeaderFromRequest(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String header = getAuthorizationHeaderFromRequest(request);
        return getTokenFromHeader(header);
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Object getObjectFromToken(String token, String object) {
        Claims claims = getClaimsObjectFromToken(token);
        return claims.get(object);
    }

    public Object getObjectFromRequest(HttpServletRequest request, String object) {
        String token = getTokenFromRequest(request);
        Claims claims = getClaimsObjectFromToken(token);
        return claims.get(object);
    }

    public String getSchoolStringFromRequest(HttpServletRequest request) {
        String school = request.getHeader("X-School");
        if (!StringUtils.isEmpty(school)) {
            return school;
        }

        school = (String) getObjectFromRequest(request, "school");
        return school;
    }

    public String getAuthorityFromRequest(HttpServletRequest request) {
        return (String) getObjectFromRequest(request, "authority");
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String getAudienceFromToken(String token) {
        return getClaimFromToken(token, Claims::getAudience);
    }

    public UserEntity getUserFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);

        String username = getClaimsFromToken(token);

        UserEntity userEntity = null;
        if (!StringUtils.isEmpty(username)) {
            userEntity = userService.findByEmail(username);
        }

        return userEntity;
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);

        return expiration.before(clock.now());
    }

    private String generateAudience(Device device) {
        String audience = AUDIENCE_UNKNOWN;

        if (device != null) {
            if (device.isNormal()) {
                audience = AUDIENCE_WEB;
            } else if (device.isTablet()) {
                audience = AUDIENCE_TABLET;
            } else if (device.isMobile()) {
                audience = AUDIENCE_MOBILE;
            }
        }

        return audience;
    }

    public String generateToken(Map<String, Object> claims, Device device) {
        return doGenerateToken(
                claims,
                (String) claims.get("email"),
                generateAudience(device)
        );

    }

    private Date calculateExpirationDate() {
        double expiration = 8.64e+7;
        return new Date(System.currentTimeMillis() + (long) expiration);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, String audience) {
        Date now = new Date();
        Date expirationDate = calculateExpirationDate();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setAudience(audience)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        final String username = getClaimsFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);
        //final Date expiration = getExpirationDateFromToken(token);
        return (
                username.equals(user.getEmail())
                        && !isTokenExpired(token)
                        && !isCreatedBeforeLastPasswordReset(created, user.getLastResetDate())
        );
    }

    public Claims getClaimsObjectFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsTFunction) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claimsTFunction.apply(claims);
    }

}
