package graphql.kickstart.autoconfigure.web.servlet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import graphql.kickstart.autoconfigure.web.servlet.GraphQLSubscriptionWebsocketProperties.CsrfProperties;
import java.util.List;
import java.util.Map;
import javax.websocket.server.HandshakeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;

@ExtendWith(MockitoExtension.class)
class WsCsrfFilterTest {

  private CsrfProperties csrfProperties = new CsrfProperties();
  @Mock private WsCsrfTokenRepository tokenRepository;
  @Mock private HandshakeRequest handshakeRequest;

  @Test
  void givenCsrfDisabled_whenDoFilter_thenDoesNotLoadToken() {
    csrfProperties.setEnabled(false);
    WsCsrfFilter filter = new WsCsrfFilter(csrfProperties, tokenRepository);
    filter.doFilter(handshakeRequest);

    verify(tokenRepository, never()).loadToken(any());
  }

  @Test
  void givenCsrfEnabledAndRepositoryNull_whenDoFilter_thenDoesNotGetTokenFromRequest() {
    csrfProperties.setEnabled(true);
    WsCsrfFilter filter = new WsCsrfFilter(csrfProperties, null);
    filter.doFilter(handshakeRequest);

    verify(handshakeRequest, never()).getParameterMap();
  }

  @Test
  void givenNoTokenInSession_whenDoFilter_thenGenerateAndSaveToken() {
    csrfProperties.setEnabled(true);
    when(tokenRepository.loadToken(handshakeRequest)).thenReturn(null);
    CsrfToken csrfToken = mock(CsrfToken.class);
    when(tokenRepository.generateToken(handshakeRequest)).thenReturn(csrfToken);

    WsCsrfFilter filter = new WsCsrfFilter(csrfProperties, tokenRepository);
    filter.doFilter(handshakeRequest);

    verify(tokenRepository).saveToken(csrfToken, handshakeRequest);
  }

  @Test
  void givenDifferentActualToken_whenDoFilter_thenThrowsException() {
    csrfProperties.setEnabled(true);
    CsrfToken csrfToken = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "some-token");
    when(tokenRepository.loadToken(handshakeRequest)).thenReturn(csrfToken);
    when(handshakeRequest.getParameterMap())
        .thenReturn(Map.of("_csrf", List.of("different-token")));

    WsCsrfFilter filter = new WsCsrfFilter(csrfProperties, tokenRepository);
    assertThatThrownBy(() -> filter.doFilter(handshakeRequest))
        .isInstanceOf(AccessDeniedException.class)
        .hasMessage(
            "Invalid CSRF Token 'different-token' was found on the request parameter '_csrf'.");
  }

  @Test
  void givenSameToken_whenDoFilter_thenDoesNotThrow() {
    csrfProperties.setEnabled(true);
    CsrfToken csrfToken = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "some-token");
    when(tokenRepository.loadToken(handshakeRequest)).thenReturn(csrfToken);
    when(handshakeRequest.getParameterMap()).thenReturn(Map.of("_csrf", List.of("some-token")));

    WsCsrfFilter filter = new WsCsrfFilter(csrfProperties, tokenRepository);
    assertDoesNotThrow(() -> filter.doFilter(handshakeRequest));

    verify(tokenRepository).loadToken(handshakeRequest);
  }
}
