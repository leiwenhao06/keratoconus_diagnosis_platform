package com.cornea.management.service;

import com.cornea.management.dao.UserDAO;
import com.cornea.management.dto.LoginResponse;
import com.cornea.management.entity.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @PostConstruct
    public void initDefaultAdmin() {
        try {
            if (!userDAO.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPasswordHash(BCrypt.hashpw("admin123", BCrypt.gensalt()));
                admin.setDisplayName("系统管理员");
                admin.setRole("admin");
                userDAO.insert(admin);
                log.info("Default admin user created (admin/admin123)");
            }
        } catch (SQLException e) {
            log.error("Failed to create default admin user", e);
        }
    }

    public LoginResponse login(String username, String password) throws SQLException {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        User user = userOpt.get();
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        userDAO.updateToken(user.getId(), token);
        log.info("User '{}' logged in", username);
        return new LoginResponse(token, user.getUsername(), user.getDisplayName(), user.getRole());
    }

    @Transactional
    public void logout(String token) throws SQLException {
        Optional<User> userOpt = userDAO.findByToken(token);
        if (userOpt.isPresent()) {
            userDAO.updateToken(userOpt.get().getId(), null);
            log.info("User '{}' logged out", userOpt.get().getUsername());
        }
    }

    public Optional<User> getUserByToken(String token) throws SQLException {
        return userDAO.findByToken(token);
    }
}
