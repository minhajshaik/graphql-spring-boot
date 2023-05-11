package graphql.kickstart.autoconfigure.web.servlet;

import javax.websocket.server.HandshakeRequest;

public interface WsCsrfTokenRepository {

  WsCsrfToken loadToken(HandshakeRequest request);

  WsCsrfToken generateToken(HandshakeRequest request);

  void saveToken(WsCsrfToken csrfToken, HandshakeRequest request);
}
