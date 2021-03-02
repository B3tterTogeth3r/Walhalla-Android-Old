package de.walhalla.app.interfaces;

public interface PasswordCheck {
    /**
     * Set the signUp button clickable only if passwordSecurity
     * and passwordController have both set their place
     * in that array to true
     *
     * @param number The position to change
     * @param done   The value to change
     */
    void send(int number, boolean done);
}
