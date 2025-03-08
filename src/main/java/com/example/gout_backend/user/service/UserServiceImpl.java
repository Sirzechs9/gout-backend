package com.example.gout_backend.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gout_backend.auth.service.AuthService;
import com.example.gout_backend.common.enumeration.RoleEnum;
import com.example.gout_backend.common.exception.CredentialExistsException;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.user.dto.UserCreationDto;
import com.example.gout_backend.user.dto.UserInfoDto;
import com.example.gout_backend.user.dto.UserUpdateDto;
import com.example.gout_backend.user.model.User;
import com.example.gout_backend.user.repository.UserRepositoy;
import com.example.gout_backend.user.repository.UserRoleRepository;
import com.example.gout_backend.wallet.WalletService;

@Service
public class UserServiceImpl implements  UserService{

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepositoy userRepositoy;
    private final WalletService walletService;
    private final AuthService authService;
    private final RoleService roleService;

    public UserServiceImpl(AuthService authService, UserRepositoy userRepositoy, WalletService walletService, RoleService roleService){
        this.authService = authService;
        this.userRepositoy = userRepositoy;
        this.walletService = walletService;
        this.roleService = roleService;
    }

    //Get user Dto by id 
    @Override
    public UserInfoDto getUserDtoById(int id){ 
       // The reason why return as dto is we can cut some field out before sent to user
        var user = getUserById(id);
        return new UserInfoDto(user.id(), user.firstName(), user.lastName(), user.phoneNumber());
    }

    @Override
    public User getUserById(int id){
        return userRepositoy.findById(id)
                    .orElseThrow(()-> new EntityNotFoundException(String.format("User Id: %d not found", id)));
    }
    
    //Create user + login credential + wallet
    @Override
    @Transactional
    public UserInfoDto createUser(UserCreationDto body) {
        // 1. Find existing credential
        var existsCred = authService.findCredentialByUsername(body.email());
        if (existsCred.isPresent()){
            throw new CredentialExistsException(String.format("User: %s exists", body.email()));
        }
        // 2. create user
        var prepareUser = new User(null, body.firstName(), body.lastName(), body.phoneNumber());
        var newUser = userRepositoy.save(prepareUser);

        // 3. binding role
        var userRole = roleService.bindingNewUser(newUser.id(), RoleEnum.CONSUMER);
        
        // 4. create credential
        var userCredential = authService.createConsumerCredential(newUser.id(), body.email(), body.password());

        // 5. create wallet for user
        var UserWallet = walletService.createConsumerWallet(newUser.id());

        return  new UserInfoDto(newUser.id(), newUser.firstName(), newUser.firstName(), newUser.phoneNumber());
    }


    //update user
    @Override
    public UserInfoDto updateUser(int id, UserUpdateDto body) {

        // 1. find user (getUserById)
        var user = getUserById(id);
        var prepareUser = new User(user.id(), body.firstName(), body.lastName(), user.phoneNumber());
        var updatedUser = userRepositoy.save(prepareUser);
        return new UserInfoDto(updatedUser.id(), updatedUser.firstName(), updatedUser.lastName(), updatedUser.phoneNumber());
    }
        

    //delte user + creadentail and wallet removeal (cascade)
    @Override
    @Transactional
    public boolean deleteUserById(int id) {
        // 1. Find is user existing?
        var user = getUserById(id);

        // 2. find every resource under this userId and delete 

        authService.deleteCredentialByUserId(user.id());
        logger.info("delete credential for user id {}",id);
        walletService.deleteWalletByUserId(user.id());
        logger.info("delete wallet for user id {}",id);
        userRepositoy.delete(user);
        
        return true;
    }


}
