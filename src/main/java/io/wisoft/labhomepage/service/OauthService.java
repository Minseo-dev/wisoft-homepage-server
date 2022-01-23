package io.wisoft.labhomepage.service;

import io.wisoft.labhomepage.dto.*;

import io.wisoft.labhomepage.repository.InMemoryProviderRepository;
import io.wisoft.labhomepage.repository.MemberRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@Service
public class OauthService {

  private final InMemoryProviderRepository inMemoryProviderRepository;
  private final MemberRepository memberRepository;
  private final JwtTokenProvider jwtTokenProvider;

  public OauthService(InMemoryProviderRepository inMemoryProviderRepository,MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
    this.inMemoryProviderRepository = inMemoryProviderRepository;
    this.memberRepository=memberRepository;
    this.jwtTokenProvider=jwtTokenProvider;
  }

  public LoginResponse login(String providerName, String code) {
    //프론트에서 넘어온 oauthProvider 이름을 통해 InMemoryProviderRepository 에서 OauthProvider 가져오기
    OauthProvider oauthProvider = inMemoryProviderRepository.findByProviderName(providerName);

    //access token 가져오기
    OauthTokenResponse oauthTokenResponse = getToken(code, oauthProvider);

    // 유저 정보 가져오기
    UserProfile userProfile = getUserProfile(providerName, oauthTokenResponse, oauthProvider);

    // TODO 유저 DB에 저장
    Member member = saveOrUpdate(userProfile);

    //우리 애플리케이션의 JWT 토큰
    String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(member.getId()));
    String refreshToken = jwtTokenProvider.createRefreshToken();

    //TODO 레디스에 refresh Token 추가

    return LoginResponse.builder()
        .id(member.getId())
        .name(member.getName())
        .email(member.getEmail())
        .imageUrl(member.getImageUrl())
        .role(member.getRole())
        .tokenType("Bearer")
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();

  }

  private Member saveOrUpdate(UserProfile userProfile){

    Member member = memberRepository.findByOauthId(userProfile.getOauthId())
        .map(entity->entity.update(
            userProfile.getEmail(),userProfile.getName(),userProfile.getImageUrl()))
        .orElseGet(userProfile::toMember);
    return memberRepository.save(member);

  }

  private OauthTokenResponse getToken(String code, OauthProvider oauthProvider) {
    return WebClient.create()
        .post()
        .uri(oauthProvider.getTokenUrl())
        .headers(header -> {
          header.setBasicAuth(oauthProvider.getClientId(), oauthProvider.getClientSecret());
          header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
          header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
          header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
        })
        .bodyValue(tokenRequest(code, oauthProvider))
        .retrieve()
        .bodyToMono(OauthTokenResponse.class)
        .block();
  }

  private MultiValueMap<String, String> tokenRequest(String code, OauthProvider oauthProvider) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("code", code);
    formData.add("grant_type", "authorization_code");
    formData.add("redirect_uri", oauthProvider.getRedirectUrl());
    return  formData;
  }

  private UserProfile getUserProfile(String providerName, OauthTokenResponse oauthTokenResponse, OauthProvider oauthProvider){
    Map<String, Object> userAttributes = getUserAttributes(oauthProvider,oauthTokenResponse);
    return OauthAttributes.extract(providerName,userAttributes);
  }

  private Map<String, Object> getUserAttributes(OauthProvider oauthProvider, OauthTokenResponse oauthTokenResponse){
    return WebClient.create()
        .get()
        .uri(oauthProvider.getUserInfoUrl())
        .headers(header->header.setBearerAuth(oauthTokenResponse.getAccessToken()))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>(){})
        .block();
  }

}
