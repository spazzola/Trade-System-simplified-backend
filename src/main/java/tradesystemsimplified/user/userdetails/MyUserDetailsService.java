package tradesystemsimplified.user.userdetails;


import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tradesystemsimplified.user.User;
import tradesystemsimplified.user.UserDao;

import java.util.NoSuchElementException;


@AllArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {

    private UserDao userDao;


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        final User user = userDao.findByLogin(login)
                .orElseThrow(NoSuchElementException::new);
        return new MyUserDetails(user);
    }
/*
    public boolean login(LoginForm loginForm) {
        final UserDetails userDetails = loadUserByUsername(loginForm.getLogin());
        return passwordEncoder.matches(loginForm.getPassword(), userDetails.getPassword());
    }
*/
}
