package io.wisoft.labhomepage.dto;

public enum Role {
  Guest("ROLE_GUEST"),
  User("ROLE_USER");

  private final String key;

  Role(String key){
    this.key = key;
  }

  public String getKey(){
    return key;
  }

}
