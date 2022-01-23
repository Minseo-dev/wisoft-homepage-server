package io.wisoft.labhomepage.repository;

import io.wisoft.labhomepage.dto.OauthProvider;

import java.util.HashMap;
import java.util.Map;

public class InMemoryProviderRepository {

  private final Map<String, OauthProvider> oauthProviders;

  public InMemoryProviderRepository(Map<String, OauthProvider> oauthProviders) {
    this.oauthProviders = new HashMap<>(oauthProviders);
  }

  public OauthProvider findByProviderName(String name) {
    return oauthProviders.get(name);
  }

}
