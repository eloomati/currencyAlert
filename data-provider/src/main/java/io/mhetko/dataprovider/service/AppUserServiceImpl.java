package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.model.AppUser;
import io.mhetko.dataprovider.model.Role;
import io.mhetko.dataprovider.repository.AppUserRepository;
import io.mhetko.dataprovider.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserServiceImpl(AppUserRepository userRepository, RoleRepository roleRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUser findByUsername(String email) {
        return userRepository.findByUsername(email);
    }

    @Override
    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUser(AppUser user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setActive(true);
        Role userRole = roleRepository.findByName("ROLE_USER");
        user.setRoles(new HashSet<>(Arrays.asList(userRole)));
        userRepository.save(user);
    }
}
