package ru.valerykorzh.springdemo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.valerykorzh.springdemo.domain.Account;
import ru.valerykorzh.springdemo.domain.Image;
import ru.valerykorzh.springdemo.repository.AccountRepository;
import ru.valerykorzh.springdemo.service.AccountService;
import ru.valerykorzh.springdemo.service.ImageService;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ImageService imageService;

    @Override
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findOneByEmail(email);
    }

    @Override
    public List<Account> findByName(String name) {
        return accountRepository.findByName(name);
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account save(Account account) {
        if (account.getId() != null) {
            Account accountToPut = accountRepository.findById(account.getId()).orElseThrow();
            accountToPut.setEmail(account.getEmail());
            accountToPut.setName(account.getName());
            return accountRepository.save(accountToPut);
        }

        int avatarSize = 40;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(account.getEmail().getBytes());
            byte[] digest = md.digest();
            String hash = DatatypeConverter.printHexBinary(digest).toLowerCase();
            String avatarSource = "https://www.gravatar.com/avatar/%s?d=identicon&s=%d";
            String avatarUrl = String.format(avatarSource, hash, avatarSize);

            Image avatar = new Image();

            try(InputStream is = new URL(avatarUrl).openStream()) {
                String avatarData = Base64.getEncoder().encodeToString(is.readAllBytes());
                avatar.setData(avatarData);

            } catch (MalformedURLException ex) {
                throw new RuntimeException("There is a problem while downloading image", ex);
            } catch (IOException ioEx) {
                throw new RuntimeException("Problem with saving image", ioEx);
            }

            account.setAvatar(avatar);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    @Override
    public void deleteById(Long id) {
        try {
            accountRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            log.info("Delete non existing entity with id=" + id, ex);
        }
    }
}
