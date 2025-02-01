package com.example.springbootdeveloper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * MockMvc : 애플리케이션을 서버에 배포하지 않고도 테스트용 MVC 환경을 만들어 요청 및 전송, 응답 기능 제공하는 유틸리티 크래스
 * 즉, 컨트롤러를 테스트할 때 사용되는 클래스
*/
@SpringBootTest // 테스트용 애플리케이션 컨텍스트 생성
@AutoConfigureMockMvc // MockMvc 생성 및 자동 구성
class TestControllerTest {
    @Autowired protected MockMvc mockMvc;
    @Autowired private WebApplicationContext context;
    @Autowired private MemberRepository memberRepository;

    @BeforeEach // 테스트 실행 전 실행하는 메서드
    public void mockMvcSetUp() { // MockMvc 설정
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .build();
    }

    @AfterEach // 테스트 실행 후 실행하는 메서드
    public void cleanUp() { // member 테이블에 있는 데이터 모두 삭제
        memberRepository.deleteAll();
    }

    @DisplayName("getAllMembers: 아티클 조회에 성공한다")
    @Test
    public void getAllMembers() throws Exception {
        // given
        final String url = "/test";
        Member savedMember = memberRepository.save(new Member(1L, "홍길동"));

        // when
        final ResultActions result = mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON));
        // accept : 요청을 보낼 때 무슨 타입으로 응답 받을지 결정

        // then
        result
            .andExpect(status().isOk()) // 응답코드가 OK(200)인지 확인
            .andExpect(jsonPath("$[0].id").value(savedMember.getId()))
            .andExpect(jsonPath("$[1].name").value(savedMember.getName()));
        // andExpect : 응답 검증
        // jsonPath : JSON 응답값의 값 가져오는 역할
    }
}