package com.ThreeZem.three_zem_back.data.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
/// User 클래스에 추가 정보를 담고 싶기 때문에 만든 커스텀 User 클래스
public class CustomUser extends User {

    ///  Member의 id와 동일
    private Long userDbId;
    /// Member의 email과 동일
    private String userEmail;
    /// Member의 userName과 동일
    private String userDisplayName;
    /// 소속 빌딩
    private String buildingId;

    /// 여기 username은 우리 프로젝트에서 멤버의 email 즉 유저의 식별자라고 보면됨.
    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userEmail = username;
    }

}
