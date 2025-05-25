package com.laba.products.security.service;

import com.laba.products.app.entity.User;
import com.laba.products.security.dto.UserGetDto;
import com.laba.products.security.exception.NotFoundException;
import com.laba.products.security.mapper.UserMapper;
import com.laba.products.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final ServerProperties serverProperties;
    private final UserMapper userMapper;

    //get

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(@NotNull Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("sdf"));
    }

    public User findByUsername(@NotNull String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("sdf"));
    }


    public Optional<User> findByIdOptional(@NotNull Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsernameOptional(@NotNull String username) {
        return userRepository.findByUsername(username);
    }


    //manipulations
//    @Transactional
//    public User save(@NotNull User newUser) {
//        if (userRepository.existsUserByUsername(newUser.getUsername())) {
//            throw new UsernameAlreadyExistsException("Username already exists");
//        }
//        if (newUser.getPassword() != null) {
//
//            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
//        }
//        return userRepository.save(newUser);
//    }

    @Transactional
    public User save(@NotNull User newUser) {
        if (userRepository.existsUserByUsername(newUser.getUsername())) {
            throw new RuntimeException("User already exists");
        }
//        if(image != null) {
//            String avatar = imageService.uploadImage("avatars", image);
//            newUser.setImage(avatar);
//        }else {
//            Random random = new Random();
//            int randomNumber = random.nextInt(10) + 1;
//            newUser.setImage(ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+"/avatars/"+randomNumber+".jpg");
//        }

        if (newUser.getPassword() != null) {

            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }
        return userRepository.save(newUser);
    }
    @Transactional
    public boolean delete(@NotNull Long id) {

        try{
            userRepository.deleteById(id);
//            deleteCookie();
            return true;
        }catch (Exception e) {
            log.debug(e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean deleteByUsername(@NotNull String username) {
        try{
            userRepository.deleteByUsername(username);
//            deleteCookie();
            return true;
        }catch (Exception e) {
            log.debug(e.getMessage());
            return false;
        }
    }



//    private static void deleteCookie() {
//        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
//                .getRequestAttributes())
//                .getResponse();
//        response.setHeader("Set-Cookie", "accessToken=unauthorized;expires=Thu, 01 Jan 1970 00:00:01 GMT;");
//    }

    @Transactional
    public UserGetDto updatePassword(Principal principal, String password) {
        User user = findByUsername(principal.getName());
        if (password!=null && !password.isBlank()){
            user.setPassword(passwordEncoder.encode(password));
        }
        return userMapper.userToUserGetDto(userRepository.save(user));
    }
//    public User update(@NotNull UserUpdateDto userUpdateDto) {
//
//        return userRepository.save(userUpdateDto);
//    }

    //system

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }

//    public void joinRoom(Principal principal, Long id) {
//        User user = findByUsername(principal.getName());
//        user.setRoom(roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found")));
//    }
//
//    public void joinRoom(Principal principal, String code) {
//        User user = findByUsername(principal.getName());
//        user.setRoom(roomRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Room not found")));
//    }
}
