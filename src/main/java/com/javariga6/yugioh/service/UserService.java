package com.javariga6.yugioh.service;

import com.javariga6.yugioh.exceptions.EmailInUseException;
import com.javariga6.yugioh.exceptions.ResourceNotFoundException;
import com.javariga6.yugioh.model.*;
import com.javariga6.yugioh.repository.PassRecoveryTokenRepository;
import com.javariga6.yugioh.repository.RoleRepository;
import com.javariga6.yugioh.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    final RoleRepository roleRepository;
    final SecurityService securityService;
    final EmailService emailService;
    final PassRecoveryTokenRepository passRecoveryTokenRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, SecurityService securityService, EmailService emailService, PassRecoveryTokenRepository passRecoveryTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.securityService = securityService;
        this.emailService = emailService;
        this.passRecoveryTokenRepository = passRecoveryTokenRepository;
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }


    public UserTO saveUser(UserTO userTo) {
        if(userRepository.existsByEmail(userTo.getEmail())){
            throw new EmailInUseException();
        }
        User user = new User();
        user.setEmail(userTo.getEmail());
        user.setName(userTo.getName());
        user.setSurname(userTo.getSurname());
        user.setPassword(
                passwordEncoder.encode(userTo.getPassword())
        );


        Role role = roleRepository.getFirstByRole("ROLE_USER");
        if (role == null) {
            role = new Role();
            role.setRole("ROLE_USER");
            roleRepository.save(role);
        }


        user.setRole(role);
        User savedUser = userRepository.save(user);
        UserTO userTO = new UserTO();
        userTO.setEmail(user.getEmail());
        userTO.setName(user.getName());
        userTO.setSurname(user.getSurname());
        userTO.setRole(user.getRole());
        return userTO;
    }

    @Transactional
    public User getUserById(Long id) {
        return userRepository.getOne(id);
    }

    @Transactional
    public Role getRole(Long id){
        return userRepository.getOne(id).getRole();
    }

//    public User getUserByEmail(String email) {
//        return userRepository.getFirstByEmail(email);
//    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public void delete(User user) {
        User userFromRepo = userRepository.findFirstByEmail(user.getEmail())
                .orElseThrow(ResourceNotFoundException::new);
        userRepository.delete(userFromRepo);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void updateUser(User user) {
        User userFromRepo = userRepository.getOne(user.getId());
        userFromRepo.setName(user.getName());
        userFromRepo.setEmail(user.getEmail());
        userFromRepo.setSurname(user.getSurname());
        userRepository.save(userFromRepo);
    }

    public void updateThisUser(UserTO userTO) {
        User user = userRepository.getOne(securityService.getUserId());
        user.setEmail(userTO.getEmail());
        user.setName(userTO.getName());
        user.setSurname(userTO.getSurname());
        if (userTO.getPassword() != null) {
            user.setPassword(
                    passwordEncoder.encode(
                            userTO.getPassword()
                    )
            );
        }
        userRepository.save(user);
    }

    public void sendRecoveryToken(PassResetRequest request) {
        User user = userRepository.findFirstByEmail(request.getEmail())
                .orElseThrow(ResourceNotFoundException::new);
        if (user == null) return;
        PassRecoveryToken token = passRecoveryTokenRepository.findFirstByUser(user);
        if (token==null) {
            token = new PassRecoveryToken();
        }
        String tokenStr = UUID.randomUUID().toString();
        token.setToken(tokenStr);
        token.setUser(user);
        passRecoveryTokenRepository.save(token);
        this.emailService.sendSimpleMessage(
                request.getEmail(),
                "Yugioh store password recovery",
                request.getHost() + "/users/password/reset/" + tokenStr
                );
    }

    public void passReset(ResetRequest request) {
        PassRecoveryToken token = passRecoveryTokenRepository.findFirstByToken(
                request.getTokenStr()
        );
        User user = token.getUser();
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        userRepository.save(user);
        passRecoveryTokenRepository.delete(token);
    }

    public UserTO makeUserAdmin(User user) {
        User userFromRepo = userRepository.findById(user.getId())
                .orElseThrow(ResourceNotFoundException::new);
        Role role = roleRepository.findFirstByRole("ROLE_ADMINISTRATOR")
                .orElse(createAdminRole("ROLE_ADMINISTRATOR"));
        userFromRepo.setRole(role);
        userRepository.save(userFromRepo);
        UserTO userTO = new UserTO();
        userTO.setRole(userFromRepo.getRole());
        userTO.setSurname(userFromRepo.getSurname());
        userTO.setName(userFromRepo.getName());
        userTO.setEmail(userFromRepo.getEmail());
        return userTO;
    }

    private Role createAdminRole(String roleName){
        Role role = new Role();
        role.setRole(roleName);
        role = roleRepository.save(role);
        return role;
    }
}
