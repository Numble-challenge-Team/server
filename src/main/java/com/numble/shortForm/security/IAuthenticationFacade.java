package com.numble.shortForm.security;

import org.springframework.security.core.Authentication;


public interface IAuthenticationFacade {
    Authentication getAuthentication();
}
