package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.manageusers.ChangePassword;
import com.simpleerp.simpleerpapp.dtos.manageusers.UpdateUserData;
import com.simpleerp.simpleerpapp.dtos.warehouse.AssignedUser;
import com.simpleerp.simpleerpapp.exception.ApiBadRequestException;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
import com.simpleerp.simpleerpapp.models.User;
import com.simpleerp.simpleerpapp.repositories.UserRepository;
import com.simpleerp.simpleerpapp.security.userdetails.UserDetailsI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UsersService {

    private final UserRepository userRepository;
    PasswordEncoder encoder;

    @Autowired
    public UsersService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public User getUserProfileData(){
        String username = getCurrentUserUsername();
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
    }

    @Transactional
    public boolean updateUserProfileData(UpdateUserData updateUserData){
        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
        if(user.getIsDeleted()){
            throw new ApiExpectationFailedException("exception.userDeleted");
        }
        boolean dataChanged = false;
        if(updateUserData.getEmail() != null && !Objects.equals(updateUserData.getEmail(), "")
                && !updateUserData.getEmail().equals(user.getEmail())){
            if (userRepository.existsByEmail(updateUserData.getEmail())) {
                throw new ApiBadRequestException("exception.emailUsed");
            }
            user.setEmail(updateUserData.getEmail());
            dataChanged = true;
        }
        if(updateUserData.getName() != null && !Objects.equals(updateUserData.getName(), "")
                && !updateUserData.getName().equals(user.getUsername())){
            user.setName(updateUserData.getName());
            dataChanged = true;
        }
        if(updateUserData.getSurname() != null && !Objects.equals(updateUserData.getSurname(), "")
                && !updateUserData.getSurname().equals(user.getSurname())){
            user.setSurname(updateUserData.getSurname());
            dataChanged = true;
        }
        if(updateUserData.getPhone() != null && !Objects.equals(updateUserData.getPhone(), "")){

            String phoneNumber = updateUserData.getPhone();
            phoneNumber = phoneNumber.replaceAll("\\s+", "");
            if(!phoneNumber.startsWith("+48")){
                phoneNumber = "+48" + phoneNumber;
            }
            if(!updateUserData.getPhone().equals(user.getPhone())) {
                user.setPhone(phoneNumber);
                dataChanged = true;
            }
        } else if (Objects.equals(updateUserData.getPhone(), "")){
            user.setPhone("");
            dataChanged = true;
        }
        if(dataChanged) {
            user.setUpdateDate(LocalDateTime.now());
        }
        return dataChanged;
    }

    @Transactional
    public void changePassword(ChangePassword changePassword){
        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
        if(user.getIsDeleted()){
            throw new ApiExpectationFailedException("exception.userDeleted");
        }
        if(encoder.matches(changePassword.getOldPassword(), user.getPassword())){
            if(encoder.matches(changePassword.getNewPassword(), user.getPassword())){
                throw new ApiBadRequestException("exception.newPasswordSameAsOld");
            } else {
                user.setPassword(encoder.encode(changePassword.getNewPassword()));
            }
        } else {
            throw new ApiBadRequestException("exception.wrongOldPassword");
        }
    }

    private String getCurrentUserUsername(){
        UserDetailsI userDetails = (UserDetailsI) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }

    public AssignedUser loadAssignedUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
        return new AssignedUser(user.getId(), user.getName(),
                user.getSurname(), user.getEmail(), user.getPhone());
    }
}
