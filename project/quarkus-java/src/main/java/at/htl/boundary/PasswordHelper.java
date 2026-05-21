package at.htl.boundary;

import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class PasswordHelper {
    public String hashPassword(String plainPassword) {

        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Passwort darf nicht leer sein");
        }

        return BCrypt.hashpw(
                plainPassword,
                BCrypt.gensalt()
        );
    }

    public boolean verifyPassword(
            String plainPassword,
            String hashedPassword
    ) {

        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        return BCrypt.checkpw(
                plainPassword,
                hashedPassword
        );
    }
}