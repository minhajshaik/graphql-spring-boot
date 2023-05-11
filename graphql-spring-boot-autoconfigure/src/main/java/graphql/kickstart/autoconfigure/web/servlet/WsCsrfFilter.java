package graphql.kickstart.autoconfigure.web.servlet;

import static org.springframework.util.CollectionUtils.firstElement;

import graphql.kickstart.autoconfigure.web.servlet.GraphQLSubscriptionWebsocketProperties.CsrfProperties;
import java.util.Objects;
import javax.websocket.server.HandshakeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.CsrfToken;

@RequiredArgsConstructor
class WsCsrfFilter {

  private final CsrfProperties csrfProperties;
  private final WsCsrfTokenRepository tokenRepository;

  void doFilter(HandshakeRequest request) {
    if (csrfProperties.isEnabled() && tokenRepository != null) {
      CsrfToken csrfToken = tokenRepository.loadToken(request);
      boolean missingToken = csrfToken == null;
      if (missingToken) {
        csrfToken = tokenRepository.generateToken(request);
        tokenRepository.saveToken(csrfToken, request);
      }

      String actualToken =
          firstElement(request.getParameterMap().get(csrfToken.getParameterName()));
      if (!Objects.equals(csrfToken.getToken(), actualToken)) {
        throw new AccessDeniedException(
            "Invalid CSRF Token '"
                + actualToken
                + "' was found on the request parameter '"
                + csrfToken.getParameterName()
                + "'.");
      }
    }
  }
}
