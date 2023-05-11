package graphql.kickstart.autoconfigure.web.servlet;

import javax.websocket.server.HandshakeRequest;
import org.springframework.security.web.csrf.CsrfToken;

public interface WsCsrfTokenRepository {

  CsrfToken loadToken(HandshakeRequest request);

  CsrfToken generateToken(HandshakeRequest request);

  void saveToken(CsrfToken csrfToken, HandshakeRequest request);
}
