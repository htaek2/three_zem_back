package com.ThreeZem.three_zem_back.data.dto.auth;

import com.ThreeZem.three_zem_back.data.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    private String username;
    private String email;
    private String password;
}
