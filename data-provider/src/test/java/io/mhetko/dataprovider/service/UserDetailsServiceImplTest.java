package io.mhetko.dataprovider.service;

import io.mhetko.dataprovider.model.AppUser;
import io.mhetko.dataprovider.model.Role;
import io.mhetko.dataprovider.model.enums.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private AppUserServiceImpl userService;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userService = mock(AppUserServiceImpl.class);
        userDetailsService = new UserDetailsServiceImpl(userService);
    }

    @Test
    void shouldLoadUserByUsernameWithAuthorities() {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setPasswordHash("pass");
        user.setActive(true);

        Role role = new Role();
        role.setName(RoleName.ROLE_USER);
        user.setRoles(Collections.singleton(role));

        when(userService.findByUsername("testuser")).thenReturn(user);

        UserDetails details = userDetailsService.loadUserByUsername("testuser");

        assertThat(details.getUsername()).isEqualTo("testuser");
        assertThat(details.getPassword()).isEqualTo("pass");
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities()).extracting("authority")
                .containsExactly(RoleName.ROLE_USER.name());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userService.findByUsername("notfound")).thenReturn(null);

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("notfound"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: notfound");
    }
}