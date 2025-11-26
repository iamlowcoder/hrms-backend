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

        // Check if user has ADMIN or HR role
        boolean hasAdminOrHrRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN") || authority.equals("ROLE_HR"));

        if (hasAdminOrHrRole) {
            return new AuthorizationDecision(true);
        }

        // Check if user is updating themselves
        String requestPath = context.getRequest().getRequestURI();
        String username = auth.getName();
        
        // Extract user ID from path like /api/users/123
        String[] pathParts = requestPath.split("/");
        if (pathParts.length >= 4 && pathParts[1].equals("api") && pathParts[2].equals("users")) {
            String userIdInPath = pathParts[3];
            
            // If the username matches the user ID in the path, allow access
            // Note: This is a basic implementation. For email-based usernames, enhance this
            // to lookup the user ID from UserDetails or UserRepository to properly match
            // the authenticated user's ID with the path parameter.
            if (username.equals(userIdInPath)) {
                return new AuthorizationDecision(true);
            }
        }

        return new AuthorizationDecision(false);
    }
}

