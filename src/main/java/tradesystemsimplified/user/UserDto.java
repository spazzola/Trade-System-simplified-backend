package tradesystemsimplified.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserDto {

    private String login;
    private String email;
    private String password;
    private String role;
}
