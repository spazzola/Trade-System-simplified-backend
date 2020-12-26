package tradesystemsimplified.user;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tradesystemsimplified.user.jwt.AuthenticationRequest;
import tradesystemsimplified.user.jwt.AuthenticationResponse;
import tradesystemsimplified.user.jwt.JwtUtil;
import tradesystemsimplified.user.security.SecurityConfiguration;
import tradesystemsimplified.user.userdetails.MyUserDetailsService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@AllArgsConstructor
@Log4j2
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private UserService userService;
    private UserMapper userMapper;
    private MyUserDetailsService myUserDetailsService;
    private JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private Logger logger;


    @GetMapping("/getAll")
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PostMapping("/register")
    public UserDto registerUser(@RequestBody UserDto userDto) throws Exception {
        User user = userService.registerUser(userDto);
        return userMapper.toDto(user);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
                                                       HttpServletRequest httpServletRequest) throws Exception {

        String ipAddress = SecurityConfiguration.getClientIpAddress(httpServletRequest);

        logger.info("Logowanie na konto: " + authenticationRequest.getLogin() + " , IP: " + ipAddress);

//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authenticationRequest.getLogin(), authenticationRequest.getPassword())
//            );
//        } catch (BadCredentialsException e) {
//            logger.error("Blad logowania, nieprawidlowe haslo");
//            throw new RuntimeException("Nieprawidłowe hasło!");
//        }

        UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getLogin());
        boolean isMatch = passwordEncoder.matches(authenticationRequest.getPassword(), userDetails.getPassword());
        String jwt;

        if (!isMatch) {
            logger.error("Blad logowania, nieprawidlowe haslo");
            throw new RuntimeException("Nieprawidłowe hasło!");
        } else {

            jwt = jwtUtil.generateToken(userDetails);

            logger.info("Zalogowano");
        }

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}