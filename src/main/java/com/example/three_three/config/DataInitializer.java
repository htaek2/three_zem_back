package com.example.three_three.config;

import com.example.three_three.repository.BuildingRepository;
import com.example.three_three.repository.MemberRepository;
import com.example.three_three.entity.Building;
import com.example.three_three.entity.Floor;
import com.example.three_three.entity.Member;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String adminId = "admin@zem.com";
        String adminPw = "1111";

        // 계정이 없으면 생성
        if (memberRepository.findByEmailId(adminId).isEmpty()) {

            // 초기 계정 생성
            Member adminMember = new Member(
                null,
                adminId,
                adminPw
            );

            memberRepository.save(adminMember);
        }

        Member memberData = memberRepository.findByEmailId(adminId).orElse(null);
        Integer memberId = memberData.getMemberId();

        // 빌딩 데이터가 없으면 추가
        if (buildingRepository.findByMemberId(memberData.getMemberId()).isEmpty()) {

            String buildingName = "토리빌딩";
            String address = "대전 서구 계룡로491번길 86 미래융합교육원";
            float totalArea = 200 * 4;

            Building building = new Building(
                null,
                memberId,
                buildingName,
                address,
                totalArea
            );

            Building buildingData = buildingRepository.findByMemberId(memberId).orElse(null);

            int numOfFloor = 4;

            // 층 데이터
            for (int i = 0; i < numOfFloor; i++){
                Floor floor = new Floor(
                    null,
                    buildingData.getBuildingId(),
                    i + 1
                );

                switch (i) {
                    case 0:
                        
                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:
                    
                        break;
                }

                // 층별 장비 데이터
                for (int j = 0; j < )
            }
            


            buildingRepository.save(building);
        }
    }
}
