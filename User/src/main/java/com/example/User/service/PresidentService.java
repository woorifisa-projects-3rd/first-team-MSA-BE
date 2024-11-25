package com.example.User.service;


import com.example.User.dto.login.ReqIdFindData;
import com.example.User.dto.login.ReqLoginData;
import com.example.User.dto.login.ReqPwChange;
import com.example.User.dto.login.ReqRegist;
import com.example.User.dto.president.PresidentInfoResponse;
import com.example.User.dto.presidentupdate.PresidentUpdateRequest;
import com.example.User.error.CustomException;
import com.example.User.error.ErrorCode;
import com.example.User.model.President;
import com.example.User.repository.PresidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PresidentService {
    private final PresidentRepository presidentRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Integer validateLogin(ReqLoginData reqLoginData) {
        President president =presidentRepository.findByEmail(reqLoginData.getEmail())
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(reqLoginData.getPassword(), president.getPassword())){
            throw new CustomException(ErrorCode.PASSWORD_NOT_CORRECT);
        }
        return president.getId();
    }

    @Transactional
    public Integer regist(ReqRegist reqRegist) {

        boolean result= presidentRepository.existsByEmailOrPhoneNumber(
                reqRegist.getEmail(), reqRegist.getPhoneNumber());
        if(result)
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);

        reqRegist.setPassword(passwordEncoder.encode(reqRegist.getPassword()));
        President president= reqRegist.toEntity();
        presidentRepository.save(president);
        return president.getId();
    }

    @Transactional
    public void remove(Integer id){
        //확인 후 삭제하기
        presidentRepository.deleteById(id);
    }

    //사장 정보 수정
    @Transactional
    public void updatePresident(Integer id, PresidentUpdateRequest presidentUpdateRequest) {
        presidentRepository.updatePhoneNumberAndBirthDate(
                id,presidentUpdateRequest.getPhoneNumber(),presidentUpdateRequest.getBirthDate());
    }

    public President findById(Integer id) {
        return presidentRepository.findById(id)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
    //사장 아이디 찾기
    @Transactional
    public String findByNameAndPhoneNumber(ReqIdFindData reqIdFindData){
        President president = presidentRepository.findByNameAndPhoneNumber(
                reqIdFindData.getName(),
                reqIdFindData.getPhoneNumber()
        ).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_BY_NAME_AND_PHONE));

        String presidentEmail = president.getEmail();

        return presidentEmail;
    }

    @Transactional
    public void changePassword(Integer id,ReqPwChange reqpwChange) {
        // 1. 사장님 조회
        President president = presidentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 현재 비밀번호 확인
        if (!passwordEncoder.matches(reqpwChange.getBeforePassword(), president.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_CORRECT);
        }
        // 4. 새 비밀번호 인코딩 후 저장
        president.setPassword(passwordEncoder.encode(reqpwChange.getNewPassword()));
        presidentRepository.save(president);
    }

    public PresidentInfoResponse getPresidentInfo(Integer presidentId)
    {
        return PresidentInfoResponse.of(presidentRepository.findById(presidentId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        )) ;
    }

    // 이메일과 이름 일치하는지 확인
    public President validateEmailAndName(String email, String name) {
        President president = presidentRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.PRESIDENT_NOT_FOUND));
        if(!president.getName().equals(name)) {
            throw new CustomException(ErrorCode.MISMATCH_EMAIL);
        }
        return president;
    }

    @Transactional
    public void updatePassword(String password, President president) {
        String encodedPassword = passwordEncoder.encode(password); // 패스워드 암호화

        president.setPassword(encodedPassword);
        presidentRepository.save(president);
    }
}
