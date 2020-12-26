package tradesystemsimplified.user.jwt;

import lombok.Data;

@Data
public class AuthenticationRequest {

    private String login;
    private String password;
}
