package com.codecollab.modules.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("demo")
@SpringBootTest
class DemoAuthContextTest {

    @Autowired
    private UserAccountService userAccountService;

    @Test
    void shouldLoadDemoAuthServiceWithoutDatabase() {
        AuthenticatedUser user = userAccountService.authenticate("admin", "Admin@123456");

        assertThat(user.username()).isEqualTo("admin");
        assertThat(user.roles()).contains("ADMIN");
        assertThat(user.menus()).isNotEmpty();
    }
}
