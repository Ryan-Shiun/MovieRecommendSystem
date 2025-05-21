package service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import dao.AppUserDAO;
import exception.DataAccessRuntimeException;
import exception.EmailAlreadyExistsException;
import exception.InvalidCredentialException;
import exception.UserAlreadyExistsException;
import exception.UserNotFoundException;
import model.javaBean.AppUser;

public class AppUserService {
	private AppUserDAO userDao = new AppUserDAO();
	
	// register user
	public void register(String userName, String email, String password) {
        // check account exists
		try {
	        // 建立 AppUser 物件，createdAt 可由 DB 預設
	        AppUser newUser = new AppUser(0, userName, email, password, LocalDateTime.now());
	        userDao.insert(newUser);
		} catch (SQLException e) {
			throw new UserAlreadyExistsException(userName);
		}
    }
	
	// login
	public Optional<AppUser> login(String userName, String password) {
		try {
			var userOpt = userDao.findByUsername(userName);
			if (userOpt.isEmpty()) {
				throw new UserNotFoundException(userName);
	        }
            var user = userOpt.get();
            
            if (!user.password().equals(password)) {
            	throw new InvalidCredentialException();
            }
            return userOpt;
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("登入過程發生問題", e);
		}       
    }
	
	// get user by userId
	public Optional<AppUser> getUserById(int userId) {
        try {
        	var userOpt = userDao.findById(userId);
        	if (userOpt.isEmpty()) {
        		throw new UserNotFoundException("此 ID 找不到會員");
        	}
			return userOpt;
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("登入過程發生問題", e);
		} 
    }
	
	// get user by userName
	public Optional<AppUser> getUserByName(String name) {		
        try {
        	var userOpt = userDao.findByUsername(name);
        	if (userOpt.isEmpty()) {
        		throw new UserNotFoundException(name);
        	}
			return userOpt;
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("登入過程發生問題", e);
		} 
    }
	
	// update email
	public void updateEmail(int userId, String newEmail) {		
        try {
        	userDao.updateEmail(userId, newEmail);
		} catch (SQLException e) {
			throw new EmailAlreadyExistsException();
		} 
    }
	
	// update password
	public void updatePassword(int userId, String newPassword) {
        try {
			userDao.updatePassword(userId, newPassword);
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("更新密碼時發生錯誤", e);
		}
    }
	
	// delete account
	public void deleteUser(int userId) {
        try {
        	userDao.delete(userId);
		} catch (SQLException e) {
			throw new DataAccessRuntimeException("更新密碼時發生錯誤", e);
		}        
    }
}
