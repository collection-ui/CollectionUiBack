package com.collectionuiback.module.account;

import com.collectionuiback.module.account.exception.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException(email + " is not registered"));
    }

    public Account saveOrUpdate(Account account) {
        Account persisted = accountRepository.findByEmail(account.getEmail())
                .orElseGet(() -> saveIfAbsent(account))
                .updateProfile(account.getName(), account.getPicture());

        return accountRepository.save(persisted);
    }

    private Account saveIfAbsent(Account account) {
        try {
            return accountRepository.save(account);
        } catch (DataIntegrityViolationException e) {
            return findByEmail(account.getEmail());
        }
    }
}
