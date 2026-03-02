package org.example.service;


import org.example.config.HibernateUtil;
import org.example.config.JwtUtil;
import org.example.dto.UserDto;
import org.example.model.*;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jvnet.hk2.annotations.Service;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository = new UserRepository();
    private final RoleRepository roleRepository = new RoleRepository();

    public Object register(UserDto userDto) {

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setPassword(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt()));
            session.persist(user);

            UserDetail detail = new UserDetail();
            detail.setName(userDto.getName());
            detail.setAddress(userDto.getAddress());
            detail.setPhoneNumber(userDto.getPhoneNumber());
            detail.setUser(user);
            session.persist(detail);

            Role role = roleRepository.findByName(session, userDto.getRole());

            if(role != null) {
                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setRole(role);
                session.persist(userRole);
            }else {
                throw new RuntimeException("Role not found");
            }

            tx.commit();
            return detail;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }

    }

    public Object login(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null || !BCrypt.checkpw(user.getPassword(), existingUser.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        UserRole userRole = userRepository.findRolesByUser(existingUser);
        String token = JwtUtil.generateToken(user.getUsername(), userRole.getRole().getId(), existingUser.getId());
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        return map;
    }
}
