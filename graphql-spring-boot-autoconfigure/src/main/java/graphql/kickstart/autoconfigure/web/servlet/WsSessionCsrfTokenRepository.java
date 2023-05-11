package graphql.kickstart.autoconfigure.web.servlet;

import java.util.UUID;
import javax.servlet.http.HttpSession;
import javax.websocket.server.HandshakeRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

class WsSessionCsrfTokenRepository implements WsCsrfTokenRepository {

  private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

  private static final String DEFAULT_CSRF_TOKEN_ATTR_NAME =
      HttpSessionCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN");

  private String sessionAttributeName = DEFAULT_CSRF_TOKEN_ATTR_NAME;

  @Override
  public void saveToken(CsrfToken token, HandshakeRequest request) {
    HttpSession session = (HttpSession) request.getHttpSession();
    if (session != null) {
      if (token == null) {
        session.removeAttribute(this.sessionAttributeName);
      } else {
        session.setAttribute(this.sessionAttributeName, token);
      }
    }
  }

  @Override
  public CsrfToken loadToken(HandshakeRequest request) {
    HttpSession session = (HttpSession) request.getHttpSession();
    if (session == null) {
      return null;
    }
    return (CsrfToken) session.getAttribute(this.sessionAttributeName);
  }

  @Override
  public CsrfToken generateToken(HandshakeRequest request) {
    return new DefaultCsrfToken(
        "X-CSRF-TOKEN", DEFAULT_CSRF_PARAMETER_NAME, UUID.randomUUID().toString());
  }
}
