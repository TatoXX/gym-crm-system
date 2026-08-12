package org.epam.gym_crm_system1.security;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class TokenBlacklistService {

    private final Map<String, Date> blacklistedTokens = new ConcurrentHashMap<>();

    private final JwtService jwtService;

    public TokenBlacklistService(final JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public void blacklist(String token) {

        Date expiration = jwtService.extractExpiration(token);

        blacklistedTokens.put(token, expiration);
    }

    public boolean isBlacklisted(String token) {
        Date expiration = blacklistedTokens.get(token);

        if(expiration == null) {
            return false;
        }

        if(expiration.before(new Date())) {
            blacklistedTokens.remove(token);
            return false;
        }

        return true;
    }

}
