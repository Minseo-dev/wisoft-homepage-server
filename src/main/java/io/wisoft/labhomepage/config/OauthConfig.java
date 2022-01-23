package io.wisoft.labhomepage.config;

import io.wisoft.labhomepage.adapter.OauthAdapter;
import io.wisoft.labhomepage.properties.OauthProperties;
import io.wisoft.labhomepage.repository.InMemoryProviderRepository;
import io.wisoft.labhomepage.dto.OauthProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

//값을 실제로 사용하기 위한 상태
@Configuration
@EnableConfigurationProperties(OauthProperties.class)
public class OauthConfig {

  private final OauthProperties oauthProperties;

  public OauthConfig(OauthProperties oauthProperties) {
    this.oauthProperties = oauthProperties;
  }

  @Bean
  public InMemoryProviderRepository inMemoryProviderRepository() {
    Map<String, OauthProvider> oauthProviders = OauthAdapter.getOauthProviders(oauthProperties);
    return new InMemoryProviderRepository(oauthProviders);
  }


}
