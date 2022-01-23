package io.wisoft.labhomepage.controller;

import io.wisoft.labhomepage.dto.LoginResponse;
import io.wisoft.labhomepage.service.OauthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OauthRestController {

  private final OauthService oauthService;

  public OauthRestController(OauthService oauthService){
    this.oauthService=oauthService;
  }

  @GetMapping("/login/oauth/{oauthProvider}")
  public ResponseEntity<LoginResponse> login(@PathVariable String oauthProvider, @RequestParam String code){
    LoginResponse loginResponse = oauthService.login(oauthProvider,code);
    return ResponseEntity.ok().body(loginResponse);
  }

}
