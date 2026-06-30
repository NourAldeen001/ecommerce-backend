package com.ecommerce.ecommerce_backend.user;

import com.ecommerce.ecommerce_backend.role.Role;
import com.ecommerce.ecommerce_backend.role.RoleRepository;
import com.ecommerce.ecommerce_backend.user.dto.RegisterRequest;
import com.ecommerce.ecommerce_backend.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration rejected -" +
                    " email already exists: {}", request.getEmail());
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found. Please seed the roles table."));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .build();

        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);
        log.info("User register successfully - userId={}, email={}",
                savedUser.getId(), savedUser.getEmail());
        return userMapper.toResponse(savedUser);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }
}
