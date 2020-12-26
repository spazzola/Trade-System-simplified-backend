package tradesystemsimplified.user;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class EmailValidation {

    private final UserDao userDao;
    //private final Logger logger = LogManager.getLogger(UserService.class);


    public EmailValidation(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean validateEmail(String email) {
        return validateEmailBody(email) && isEmailExist(email);
    }

    private boolean validateEmailBody(String email) {
        if (email.contains("@") && email.contains(".")) {
            return true;
        } else {
            //logger.info("Email: " + email + " doesn't contains \"@\" or \".\"");
            return false;
        }
    }


    private boolean isEmailExist(String email) {
        if (userDao.findByEmail(email) == null) {
            return true;
        }
        //logger.info("This email already exists");
        return false;
    }

}