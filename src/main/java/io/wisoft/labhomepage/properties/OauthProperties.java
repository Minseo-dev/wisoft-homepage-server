package io.wisoft.labhomepage.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;


//값을 바인딩 할 수 있는 상태
@Getter
@ConfigurationProperties(prefix = "oauth2")
public class OauthProperties {
  private final Map<String, User> user = new HashMap<>();

  private final Map<String, Provider> provider = new HashMap<>();

  @Getter
  @Setter
  public static class User{
    private String clientId;
    private String clientSecret;
    private String redirectUri;
  }

  @Getter
  @Setter
  public static class Provider{
    private String tokenUri;
    private String userInfoUri;
    private String userNameAttribute;
  }

}
