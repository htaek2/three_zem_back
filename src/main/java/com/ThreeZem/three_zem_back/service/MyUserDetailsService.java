package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.common.CustomUser;
import com.ThreeZem.three_zem_back.data.entity.Building;
import com.ThreeZem.three_zem_back.repository.BuildingRepository;
import com.ThreeZem.three_zem_back.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.DataException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final BuildingRepository buildingRepository;

    @Override
    /// 사용자의 아이디(이메일)로 사용자의 정보를 가져온다
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var result = memberRepository.findByEmail(username);

        if (result.isEmpty()) {
            throw new UsernameNotFoundException("등록된 유저 정보가 없음");
        }
        var user = result.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("관리자"));

        // 이 정보는 Controller에서 Authentication 매개변수로 들어가게 됨.
        CustomUser customUser = new CustomUser(user.getEmail(), user.getPassword(), authorities);
        customUser.setUserDisplayName(user.getUserName());
        customUser.setUserDbId(user.getId());

        Optional<Building> building = buildingRepository.findByMemberId(customUser.getUserDbId());
        if (building.isEmpty()) {
            throw new UsernameNotFoundException("유저 정보가 소속된 빌딩 정보 없음");
        }
        else {
            customUser.setBuildingId(String.valueOf(building.get().getId()));
        }

        return customUser;
    }

}
