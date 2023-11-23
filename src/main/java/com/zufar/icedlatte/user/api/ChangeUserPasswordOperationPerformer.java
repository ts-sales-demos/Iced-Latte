package com.zufar.icedlatte.user.api;

import com.zufar.icedlatte.openapi.dto.ChangeUserPasswordRequest;
import com.zufar.icedlatte.security.api.SecurityPrincipalProvider;
import com.zufar.icedlatte.user.exception.InvalidOldPasswordException;
import com.zufar.icedlatte.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeUserPasswordOperationPerformer {

    private final SingleUserProvider singleUserProvider;
    private final UserRepository userRepository;
    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void changeUserPassword(final ChangeUserPasswordRequest changeUserPasswordRequest) throws InvalidOldPasswordException {
        UUID userId = securityPrincipalProvider.getUserId();

        String oldPasswordFromRequest = changeUserPasswordRequest.getOldPassword();
        String oldPasswordInDatabase = singleUserProvider.getUserEntityById(userId).getPassword();

        if (!passwordEncoder.matches(oldPasswordFromRequest, oldPasswordInDatabase)) {
            log.error("User with id = {} provided incorrect password.", userId);
            throw new InvalidOldPasswordException(userId);
        }

        String newPassword = changeUserPasswordRequest.getNewPassword();
        String newEncryptedPassword = passwordEncoder.encode(newPassword);

        userRepository.changeUserPassword(newEncryptedPassword, userId);
    }
}