package com.ansj.demo.config;

import com.ansj.demo.domain.UserAccount;
import com.ansj.demo.dto.UserAccountDto;
import com.ansj.demo.repository.UserAccountRepository;
import com.ansj.demo.service.UserAccountService;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

//    @MockBean private UserAccountRepository userAccountRepository;

    @MockBean private UserAccountService userAccountService;

    @BeforeTestMethod
    public void securitySetUp() {
//        BDDMockito.given(userAccountRepository.findById(BDDMockito.anyString()))
//                .willReturn(Optional.of(UserAccount.of(
//                        "unoTest", "pw", "ansj@test-mail.com", "ansj-test", "test memo"
//        )));
        BDDMockito.given(userAccountService.searchUser(BDDMockito.anyString()))
                .willReturn(Optional.of(createUserAccountDto()));
        BDDMockito.given(userAccountService.saveUser(anyString(), anyString(),anyString(), anyString(), anyString()))
                .willReturn(createUserAccountDto());
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "unoTest", "pw", "ansj@test-mail.com", "ansj-test", "test memo"
        );
    }
}
