package graphql.kickstart.autoconfigure.editor.voyager;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** @author Guilherme Blanco */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
    value = "graphql.voyager.enabled",
    havingValue = "true",
    matchIfMissing = true)
@EnableConfigurationProperties(VoyagerPropertiesConfiguration.class)
public class VoyagerAutoConfiguration {

  @Bean
  VoyagerController voyagerController(VoyagerIndexHtmlTemplate voyagerIndexHtmlTemplate) {
    return new VoyagerController(voyagerIndexHtmlTemplate);
  }

  @Bean
  VoyagerIndexHtmlTemplate voyagerIndexHtmlTemplate(
      final VoyagerPropertiesConfiguration voyagerPropertiesConfiguration) {
    return new VoyagerIndexHtmlTemplate(voyagerPropertiesConfiguration);
  }
}
