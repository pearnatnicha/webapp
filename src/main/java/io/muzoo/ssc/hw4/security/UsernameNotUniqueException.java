package io.muzoo.ssc.hw4.security;

public class UsernameNotUniqueException extends UserServiceException{
    public UsernameNotUniqueException(String message) {
        super(message);
    }
}
