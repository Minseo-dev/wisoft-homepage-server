package io.wisoft.labhomepage.adapter;

import io.wisoft.labhomepage.dto.OauthProvider;
import io.wisoft.labhomepage.properties.OauthProperties;

import java.util.HashMap;
import java.util.Map;

public class OauthAdapter {

  private OauthAdapter() {
  }

  //OauthProperties -> OauthProvider 변환
  public static Map<String, OauthProvider> getOauthProviders(OauthProperties oauthProperties) {

    Map<String, OauthProvider> oauthProvider = new HashMap<>();

    oauthProperties.getUser().forEach((key, value) -> oauthProvider.put(
        key, new OauthProvider(value, oauthProperties.getProvider().get(key))));

    return oauthProvider;

  }

}
