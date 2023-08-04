package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(Set<Long> ids, Integer from, Integer size) {
        log.info("UserServiceImpl.getAllUsers: {}, {}, {} - Started", ids, from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        List<UserDto> userDtoList;
        if (ids == null || ids.isEmpty()) {
            userDtoList = userRepository.findAll(pageable).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            userDtoList = userRepository.findAllByIdIn(ids, pageable).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        log.info("UserServiceImpl.getAllUsers: {} - Finished", userDtoList);
        return userDtoList;
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        log.info("UserServiceImpl.addUser: {} - Started", newUserRequest);
        User user = userRepository.save(userMapper.toUser(newUserRequest));
        UserDto userDto = userMapper.toUserDto(user);
        log.info("UserServiceImpl.addUser: {} - Finished", userDto);
        return userDto;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("UserServiceImpl.deleteUser: {} - Started", userId);
        findUserById(userId);
        userRepository.deleteById(userId);
        log.info("UserServiceImpl.deleteUser: {} - Finished", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long userId) {
        log.info("UserServiceImpl.findUserById: {} ", userId);
        return  userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Пользователь с ID : %s не найден", userId))
                );
    }
}
