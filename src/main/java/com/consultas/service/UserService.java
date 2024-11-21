package com.consultas.service;

import com.consultas.config.exceptions.InvalidPasswordException;
import com.consultas.config.exceptions.LoginAlreadyExistsException;
import com.consultas.config.exceptions.UserNotFoundException;
import com.consultas.dto.CredentialsDto;
import com.consultas.dto.SignUpDto;
import com.consultas.dto.UserDto;
import com.consultas.entity.User;
import com.consultas.mappers.UserMapper;
import com.consultas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserDto login(CredentialsDto credentialsDto) {
        User user = userRepository.findByLogin(credentialsDto.login())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado. Verifique o login informado."));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }
        throw new InvalidPasswordException("Senha inválida. Tente novamente.");
    }

    public UserDto register(SignUpDto userDto) {
        Optional<User> optionalUser = userRepository.findByLogin(userDto.login());

        if (optionalUser.isPresent()) {
            throw new LoginAlreadyExistsException("Este login já está em uso.");
        }

        User user = userMapper.signUpToUser(userDto);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.password())));

        User savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

    public UserDto findByLogin(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
        return userMapper.toUserDto(user);
    }
}
