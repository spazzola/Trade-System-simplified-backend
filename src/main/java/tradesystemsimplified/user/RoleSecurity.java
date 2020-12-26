package tradesystemsimplified.user;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tradesystemsimplified.user.userdetails.MyUserDetails;

import java.util.NoSuchElementException;

@Log4j2
@Service
public class RoleSecurity {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";

    private Logger logger = LogManager.getLogger(RoleSecurity.class);

    @Autowired
    private UserDao userDao;


    @Transactional
    public void checkAdminRole(Authentication authentication) {
        final MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        final User user = userDao.findByLogin(myUserDetails.getUsername())
                .orElseThrow(NoSuchElementException::new);

        if(!ADMIN_ROLE.equals(user.getRole())) {
            logger.error("Brak autoryzacji dla: " + user.getLogin());
            throw new PermissionDeniedException();
        }
    }

    @Transactional
    public void checkUserRole(Authentication authentication) {
        final MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        final User user = userDao.findByLogin(myUserDetails.getUsername())
                .orElseThrow(NoSuchElementException::new);

        if(!USER_ROLE.equals(user.getRole()) && !ADMIN_ROLE.equals(user.getRole())) {
            logger.error("Brak autoryzacji dla: " + user.getLogin());
            throw new PermissionDeniedException();
        }
    }

}