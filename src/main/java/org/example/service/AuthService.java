package org.example.service;


import org.example.config.JwtUtil;
import org.example.dto.UserDto;
import org.example.model.*;
import org.example.repository.RoleRepository;
import org.example.repository.UserDetailRepository;
import org.example.repository.UserRepository;
import org.jvnet.hk2.annotations.Service;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

@Service
public class AuthService {

    private UserRepository userRepository = new UserRepository();
    private RoleRepository roleRepository = new RoleRepository();
    private UserDetailRepository userDetailRepository = new UserDetailRepository();

    public Object register(UserDto userDto) {

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);

        UserDetail detail = new UserDetail();
        detail.setName(userDto.getName());
        detail.setAddress(userDto.getAddress());
        detail.setPhoneNumber(userDto.getPhoneNumber());
        detail.setUser(user);
        userDetailRepository.save(detail);

        Role role = roleRepository.findByName(userDto.getRole());

        if(role != null) {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            roleRepository.saveUserRole(userRole);
        }else {
            throw new RuntimeException("Role not found");
        }
        return detail;


    }

    public Object login(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null || !BCrypt.checkpw(user.getPassword(), existingUser.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        UserRole userRole = userRepository.findRolesByUser(existingUser);
        UserDetail userDetail = userDetailRepository.findByUserId(existingUser.getId());
        String token = JwtUtil.generateToken(user.getUsername(), userRole.getRole().getId(), existingUser.getId(), userDetail.getName());
        HashMap<String, String> map = new HashMap<>();
        map.put("token", token);
        return map;
    }
}
