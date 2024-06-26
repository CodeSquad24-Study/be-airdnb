package team07.airbnb.integration.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team07.airbnb.common.auth.jwt.JwtUserDetails;
import team07.airbnb.common.auth.jwt.JwtUtil;
import team07.airbnb.data.user.dto.response.TokenUserInfo;
import team07.airbnb.data.user.enums.Role;
import team07.airbnb.entity.UserEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@Transactional
public class AuthHelper {

    @Autowired
    private JwtUtil jwtUtil;

    @PersistenceContext
    private EntityManager entityManager;

    private Map<Role, UserEntity> userInstances = new HashMap<>();

    public UserEntity getUserInstanceByRole(Role role) {
        return userInstances.get(role);
    }


    public String login(Role role) throws JsonProcessingException {
        UserEntity dummyUser = new UserEntity(null, "", role.getTitle(), "test@test.com", role, "regTest", new ArrayList<>());
        if (!userInstances.containsKey(role)) {
            userInstances.put(role, dummyUser);
        }

        entityManager.persist(dummyUser);
        entityManager.flush();

        TokenUserInfo dummyTUI = TokenUserInfo.of(dummyUser);
        JwtUserDetails dummyDetails = new JwtUserDetails(dummyTUI, null);


        return jwtUtil.generateToken(dummyDetails);
    }


    public void logout() {
        entityManager.createNativeQuery("TRUNCATE TABLE USERS");
        entityManager.flush();
        entityManager.clear();
    }
}
