package campus.lostfound.service;

import campus.lostfound.exception.UserNotFoundException;
import campus.lostfound.model.User;
import campus.lostfound.repository.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) throws Exception {
        if (user == null) {
            throw new IllegalArgumentException("User details cannot be null.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Please provide a valid campus email address.");
        }
        if (user.getPassword() == null || user.getPassword().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters.");
        }

        User existing = userRepository.findByEmail(user.getEmail());
        if (existing != null) {
            throw new IllegalArgumentException("An account with email " + user.getEmail() + " already exists.");
        }

        return userRepository.save(user);
    }

    public User login(String email, String password) throws UserNotFoundException {
        if (email == null || password == null) {
            throw new UserNotFoundException("Email and password are required.");
        }

        User user = userRepository.findByEmail(email.trim());
        if (user == null || !user.getPassword().equals(password)) {
            throw new UserNotFoundException("Invalid email or password. Please try again.");
        }

        return user;
    }

    public User getUserById(int userId) throws UserNotFoundException {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
