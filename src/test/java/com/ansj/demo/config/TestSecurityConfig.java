package com.ansj.demo.config;

import com.ansj.demo.domain.UserAccount;
import com.ansj.demo.repository.UserAccountRepository;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean private UserAccountRepository userAccountRepository;

    @BeforeTestMethod
    public void securitySetUp() {
        BDDMockito.given(userAccountRepository.findById(BDDMockito.anyString()))
                .willReturn(Optional.of(UserAccount.of(
                        "ansjtest", "pw", "ansj@test-mail.com", "ansj-test", "test memo"
        )));
    }
}
