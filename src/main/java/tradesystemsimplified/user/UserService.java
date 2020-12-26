package tradesystemsimplified.user;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {

    private UserDao userDao;
    private EmailValidation emailValidation;
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;


    @Transactional
    public User registerUser(UserDto userDto) {
        if (!emailValidation.validateEmail(userDto.getEmail())) {
            throw new RuntimeException("Email error");
        }
        final String encryptedPassword = passwordEncoder.encode(userDto.getPassword());
        final User user = User.builder()
                .login(userDto.getLogin())
                .password(encryptedPassword)
                .email(userDto.getEmail())
                .role(userDto.getRole())
                .build();

        return userDao.save(user);
    }

    public List<UserDto> getAll() {
        return userMapper.toDto(userDao.findAll());
    }

}