package tradesystemsimplified.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    User findByEmail(String email);
    Optional<User> findByLogin(String login);

}
