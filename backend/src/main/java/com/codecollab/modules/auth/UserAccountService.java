package com.codecollab.modules.auth;

public interface UserAccountService {

    AuthenticatedUser authenticate(String username, String password);

    AuthenticatedUser findByUsername(String username);
}
