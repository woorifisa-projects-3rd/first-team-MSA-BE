package com.example.User.integration;

import com.example.User.dto.login.ReqRegist;
import com.example.User.service.RedisTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
@SpringBootTest(properties = {
})
@AutoConfigureMockMvc  // MockMvc 자동 설정 추가
@Transactional
public class MockMvcTest {

    @Autowired
    private MockMvc mockMvc;  // MockMvc 추가

    @MockBean
    private RedisTokenService redisTokenService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("회원가입 실패 - 중복 이메일")
    public void regist_fail_duplicate_email() throws Exception {
        // given
        ReqRegist reqRegist = new ReqRegist();
        reqRegist.setEmail("j525252p@gmail.com");
        reqRegist.setPassword("password123!");
        reqRegist.setName("홍길동");
        reqRegist.setAddress("서울시 강남구");
        reqRegist.setBirthDate(LocalDate.of(1990, 1, 1));
        reqRegist.setPhoneNumber("01012345678");
        reqRegist.setTermsAccept(true);

        String content = objectMapper.writeValueAsString(reqRegist);

        mockMvc.perform(post("/president/regist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(jsonPath("$.code").value("DB_DUPLICAE_ERROR"))
                .andDo(print());
    }
}
