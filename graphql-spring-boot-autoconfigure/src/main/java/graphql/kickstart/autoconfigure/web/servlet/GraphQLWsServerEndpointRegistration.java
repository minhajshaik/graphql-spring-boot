package graphql.kickstart.autoconfigure.web.servlet;

import graphql.kickstart.servlet.GraphQLWebsocketServlet;
import java.util.List;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import org.springframework.context.Lifecycle;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

/**
 * @author Andrew Potter
 */
public class GraphQLWsServerEndpointRegistration extends ServerEndpointRegistration
    implements Lifecycle {

  private final GraphQLWebsocketServlet servlet;
  private final List<String> allowedOrigins;

  public GraphQLWsServerEndpointRegistration(
      String path, GraphQLWebsocketServlet servlet, List<String> allowedOrigins) {
    super(path, servlet);
    this.servlet = servlet;
    this.allowedOrigins = allowedOrigins;
  }

  @Override
  public boolean checkOrigin(String originHeaderValue) {
    if (originHeaderValue == null || originHeaderValue.isBlank()) {
      return allowedOrigins.isEmpty();
    }
    String originToCheck = trimTrailingSlash(originHeaderValue);
    if (!allowedOrigins.isEmpty()) {
      if (allowedOrigins.contains("*")) {
        return true;
      }
      return allowedOrigins.stream()
          .map(this::trimTrailingSlash)
          .anyMatch(originToCheck::equalsIgnoreCase);
    }
    return true;
  }

  private String trimTrailingSlash(String origin) {
    return (origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin);
  }

  @Override
  public void modifyHandshake(
      ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
    super.modifyHandshake(sec, request, response);
    servlet.modifyHandshake(sec, request, response);
  }

  @Override
  public void start() {
    // do nothing
  }

  @Override
  public void stop() {
    servlet.beginShutDown();
  }

  @Override
  public boolean isRunning() {
    return !servlet.isShutDown();
  }
}
