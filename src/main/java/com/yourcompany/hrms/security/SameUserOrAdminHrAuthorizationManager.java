package com.yourcompany.hrms.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

public class SameUserOrAdminHrAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        Authentication auth = authentication.get();
        
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        boolean hasAdminOrHrRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN") || authority.equals("ROLE_HR"));

        if (hasAdminOrHrRole) {
            return new AuthorizationDecision(true);
        }

        String requestPath = context.getRequest().getRequestURI();
        String username = auth.getName();
        

        String[] pathParts = requestPath.split("/");
        if (pathParts.length >= 4 && pathParts[1].equals("api") && pathParts[2].equals("users")) {
            String userIdInPath = pathParts[3];

            if (username.equals(userIdInPath)) {
                return new AuthorizationDecision(true);
            }
        }

        return new AuthorizationDecision(false);
    }
}

