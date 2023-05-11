package graphql.kickstart.autoconfigure.web.servlet;

import static org.springframework.util.CollectionUtils.firstElement;

import java.util.Objects;
import javax.websocket.server.HandshakeRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class WsCsrfFilter {

  private final WsCsrfTokenRepository tokenRepository;

  void doFilter(HandshakeRequest request) {
    if (tokenRepository != null) {
      WsCsrfToken csrfToken = tokenRepository.loadToken(request);
      boolean missingToken = csrfToken == null;
      if (missingToken) {
        csrfToken = tokenRepository.generateToken(request);
        tokenRepository.saveToken(csrfToken, request);
      }

      String actualToken =
          firstElement(request.getParameterMap().get(csrfToken.getParameterName()));
      if (!Objects.equals(csrfToken.getToken(), actualToken)) {
        throw new IllegalStateException(
            "Invalid CSRF Token '"
                + actualToken
                + "' was found on the request parameter '"
                + csrfToken.getParameterName()
                + "'.");
      }
    }
  }
}
