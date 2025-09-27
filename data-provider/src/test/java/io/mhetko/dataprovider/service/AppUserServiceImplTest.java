package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.model.AppUser;
import io.mhetko.dataprovider.model.Role;
import io.mhetko.dataprovider.model.enums.RoleName;
import io.mhetko.dataprovider.repository.AppUserRepository;
import io.mhetko.dataprovider.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AppUserServiceImplTest {

    private AppUserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private AppUserServiceImpl service;

    @BeforeEach
    void setUp() {
        userRepository = mock(AppUserRepository.class);
        roleRepository = mock(RoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new AppUserServiceImpl(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void shouldFindByUsername() {
        AppUser user = new AppUser();
        when(userRepository.findByUsername("test")).thenReturn(java.util.Optional.of(user));

        AppUser result = service.findByUsername("test");

        assertThat(result).isSameAs(user);
        verify(userRepository).findByUsername("test");
    }

    @Test
    void shouldFindByEmail() {
        AppUser user = new AppUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        AppUser result = service.findByEmail("test@example.com");

        assertThat(result).isSameAs(user);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void shouldSaveUserWithEncodedPasswordAndRole() {
        AppUser user = new AppUser();
        user.setPasswordHash("plainPassword");
        Role role = new Role();
        role.setName(RoleName.ROLE_USER);

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(role);

        service.saveUser(user);

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(captor.capture());

        AppUser saved = captor.getValue();
        assertThat(saved.getPasswordHash()).isEqualTo("encodedPassword");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getRoles()).extracting(Role::getName)
                .contains(RoleName.ROLE_USER);
    }
}