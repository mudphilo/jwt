package com.daride.jwt;

import com.daride.jwt.model.Permission;
import com.daride.jwt.model.Role;
import com.daride.jwt.model.User;
import io.jsonwebtoken.Claims;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JWTTest {

    public int TestUserID = 123;
    public int TestUserStatus = 1;
    public String TestUserName = "kamau";
    public String TestRoleName = "admin";

    public static void setEnv(String key, String value) {

        try {

            Map<String, String> env = System.getenv();
            Class<?> cl = env.getClass();
            Field field = cl.getDeclaredField("m");
            field.setAccessible(true);
            Map<String, String> writableEnv = (Map<String, String>) field.get(env);
            writableEnv.put(key, value);

        } catch (Exception e) {

            throw new IllegalStateException("Failed to set environment variable", e);
        }
    }

    @Test
    public void JWTTest() throws Exception {

        setEnv("DARIDE_JWT_DURATION_HOURS","72");
        setEnv("DARIDE_JWT_SECRET","eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTY2MzAxODY0NSwiaWF0IjoxNjYzMDE4NjQ1fQ.q9SwFW4jkhSpQKupbFOZVwdzQKnnsI73BZJZT-lDr1E");
        setEnv("DARIDE_JWT_ISSUER","da-ride.com");

        //int userID, String username, int userStatus, Role role
        List<Permission> permissionList = new ArrayList<>();
        Permission permission1 = new Permission();
        permission1.setModule("user");
        permission1.setScope("all");
        permission1.setActions(Arrays.asList("create", "read", "update", "delete"));

        Permission permission2 = new Permission("trip", "all", Arrays.asList("create", "read", "update", "delete"));
        Permission permission3 = new Permission("payment", "all", Arrays.asList("read"));

        permissionList.add(permission1);
        permissionList.add(permission2);
        permissionList.add(permission3);

        Role role = new Role(TestRoleName, permissionList);

        User user = new User(TestUserID, TestUserStatus, TestUserName, role);

        String res = JWT.generateJWT(user);
        assertNotNull(res, "generateJWT should return not null");
        System.out.println("got token 1 " + res);

        res = JWT.generateJWT(TestUserID, TestUserName, TestUserStatus, role);
        assertNotNull(res, "generateJWT should return not null");
        System.out.println("got token 2 " + res);

        System.out.println();
        System.out.println("=========== TEST TOKEN DECODING ========");
        System.out.println();

        Claims claims = JWT.decodeJWT(res);
        assertNotNull(res, "decodeJWT should return not null");
        Double userID = claims.get("user_id",Double.class);
        assertEquals("decoded token should return int 1 as user ID",TestUserID,userID.intValue());

        System.out.println();
        System.out.println("=========== TEST GETING USER FROM TOKEN ========");
        System.out.println();

        User userObject = JWT.getUser(res);
        assertNotNull("getUser should return not null",userObject);
        assertEquals("decoded object be equal to encoded object",user.getUserID(),userObject.getUserID());

        System.out.println();
        System.out.println("=========== TEST CHECK PERMISSIONS ========");
        System.out.println();

        assertTrue("Token should have permission to create all user", JWT.hasPermission(res, "user", "create", "all"));

    }

}
