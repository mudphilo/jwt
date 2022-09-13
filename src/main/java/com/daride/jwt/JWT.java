package com.daride.jwt;

import com.daride.jwt.model.Permission;
import com.daride.jwt.model.Role;
import com.daride.jwt.model.User;
import com.google.gson.Gson;
import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

public class JWT {

    public static final long HOURTOMILLISECONDS = 3600000 ;
    public static final String USERNAME_FIELD = "username";
    public static final String USER_ID_FIELD = "user_id";
    public static final String ROLE_FIELD = "role";
    public static final String USER_STATUS = "user_status";
    public static final String JWT_SECRET = "DARIDE_JWT_SECRET";

    private JWT() {
        throw new IllegalStateException("JWT class");
    }

    /**
     * generate a new JWT token from the supplied details
     * @param userID user ID
     * @param username user name
     * @param userStatus user status
     * @param role user Role
     * @return encoded JWT token
     */
    public static String generateJWT(int userID, String username, int userStatus, Role role) {

        String duration = System.getenv("DARIDE_JWT_DURATION_HOURS");
        long ttlMillis = Long.parseLong(duration) * HOURTOMILLISECONDS;

        // Generate GWT
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setIssuer(System.getenv("DARIDE_JWT_ISSUER"))
                .claim(USERNAME_FIELD,username)
                .claim(USER_ID_FIELD,userID)
                .claim(USER_STATUS,userStatus)
                .claim(ROLE_FIELD,role)
                .signWith(getKey(), SignatureAlgorithm.HS256);

        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {

            long expMillis = System.currentTimeMillis() + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }


        return builder.compact();
    }

    public static Key getKey() {

        //We will sign our JWT with our ApiKey secret
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = System.getenv(JWT_SECRET).getBytes();
        return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    }
    /**
     * generate a new JWT token from the supplied user object
     * @param user user object
     * @return encoded JWT token
     */
    public static String generateJWT(User user) {

        String duration = System.getenv("DARIDE_JWT_DURATION_HOURS");
        long ttlMillis = Long.parseLong(duration) * HOURTOMILLISECONDS;


        // Generate GWT
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setIssuer(System.getenv("DARIDE_JWT_ISSUER"))
                .claim(USERNAME_FIELD,user.getUsername())
                .claim(USER_ID_FIELD,user.getUserID())
                .claim(USER_STATUS,user.getUserStatus())
                .claim(ROLE_FIELD,user.getRole())
                .signWith(getKey(),SignatureAlgorithm.HS256);

        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {

            long expMillis = System.currentTimeMillis() + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }


        return builder.compact();
    }

    /**
     * Decodes supplied JWT
     * @param jwtToken JWT Token string
     * @return Claims Object
     * @throws JwtException is thrown is decoding the token fails
     */
    public static Claims decodeJWT(String jwtToken) throws JwtException {

        return Jwts.parserBuilder()
                .setSigningKey(System.getenv(JWT_SECRET).getBytes())
                .build()
                .parseClaimsJws(jwtToken).getBody();
    }

    /**
     * Decodes supplied JWT
     * @param jwtToken JWT Token string
     * @return User Object
     * @throws JwtException is thrown is decoding the token fails
     */
    public static User getUser(String jwtToken) throws JwtException {

        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = decodeJWT(jwtToken);

        User user = new User();
        user.setUsername(claims.get(USERNAME_FIELD).toString());
        user.setUserID(claims.get(USER_ID_FIELD,Double.class).intValue());
        user.setUserStatus(claims.get(USER_STATUS,Double.class).intValue());
        user.setRole(new Gson().fromJson(claims.get("role").toString(),Role.class));
        return user;

    }

    /**
     * Checks if the supplied JWT Token has allowed permissions
     * @param jwtToken JWT token
     * @param module module to check against
     * @param action  action to check against
     * @param scope scope to check against
     * @return a boolean, true if the conditions are met otherwise false
     * @throws JwtException thrown in case of decoding errors
     */
    public static boolean hasPermission(String jwtToken, String module, String action, String scope) throws JwtException {

        Permission selectedPermission = null;

        // check module
        Role role = getUser(jwtToken).getRole();

        for (Permission permission : role.getPermissions()) {

            if(permission.getModule().equals(module) && permission.getScope().equals(scope)) {

                selectedPermission = permission;
                break;
            }
        }

        if(selectedPermission == null ) {

            return false;
        }

        return selectedPermission.getActions().contains(action);
    }
}
