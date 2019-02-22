package edu.miami.cs.hallefields.quepasa;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 *
 * Courtesy of http://www.edumobile.org/
 *             android/send-email-on-button-
 *             click-without-email-chooser/
 *
 * Downloaded: Feb 21, 2019
 * Modified by Halle Fields on February 21, 2019
 *
 */

public class SMTPAuthenticator extends Authenticator  {

    private String username;
    private String password;

    public SMTPAuthenticator(String username, String password) {
        super();

        this.username = username;
        this.password = password;
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        if ((username != null) && (username.length() > 0) && (password != null)
                && (password.length() > 0)) {

            return new PasswordAuthentication(username, password);
        }

        return null;
    }
}