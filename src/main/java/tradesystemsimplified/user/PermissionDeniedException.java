package tradesystemsimplified.user;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PermissionDeniedException extends RuntimeException {

    public PermissionDeniedException(String message) {
        super(message);
    }

}