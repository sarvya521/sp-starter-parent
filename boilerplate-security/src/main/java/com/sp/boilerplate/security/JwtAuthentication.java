package com.sp.boilerplate.security;

import com.nimbusds.jwt.JWTClaimsSet;
import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class JwtAuthentication extends AbstractAuthenticationToken {

  private final Object principal;
  private JWTClaimsSet jwtClaimsSet;

  public JwtAuthentication(Object principal, JWTClaimsSet jwtClaimsSet,
      Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.jwtClaimsSet = jwtClaimsSet;
    super.setAuthenticated(true);
  }

  public Object getCredentials() {
    return null;
  }

  public Object getPrincipal() {
    return this.principal;
  }

  public JWTClaimsSet getJwtClaimsSet() {
    return this.jwtClaimsSet;
  }
}
