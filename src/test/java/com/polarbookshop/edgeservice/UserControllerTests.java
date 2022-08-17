package com.polarbookshop.edgeservice;

import com.polarbookshop.edgeservice.config.SecurityConfig;
import com.polarbookshop.edgeservice.user.User;
import com.polarbookshop.edgeservice.user.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTests {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void whenNotAuthenticatedThen401() {
        this.webClient
                .get()
                .uri("/user")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void whenAuthenticatedThenReturnUser() {
        var expectedUser = new User("jon.snow", "Jon", "Snow", List.of("employee", "customer"));

        this.webClient.mutateWith(configureMockOidcLogin(expectedUser))
                .get()
                .uri("/user")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(User.class).value(user -> assertThat(user).isEqualTo(expectedUser));
    }

    private SecurityMockServerConfigurers.OidcLoginMutator configureMockOidcLogin(User expectedUser) {
        return SecurityMockServerConfigurers.mockOidcLogin().idToken(
                builder -> {
                    builder.claim(StandardClaimNames.PREFERRED_USERNAME, expectedUser.getUsername());
                    builder.claim(StandardClaimNames.GIVEN_NAME, expectedUser.getFirstName());
                    builder.claim(StandardClaimNames.FAMILY_NAME, expectedUser.getLastName());
                    builder.claim("roles", expectedUser.getRoles());
                }
        );
    }
}
