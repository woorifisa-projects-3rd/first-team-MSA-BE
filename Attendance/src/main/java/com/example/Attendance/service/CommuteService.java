package com.example.Attendance.service;

import com.example.Attendance.repository.CommuteRepository;
import com.example.Attendance.dto.CommuteByPresidentRequest;
import com.example.Attendance.dto.CommuteMonthlyResponse;
import com.example.Attendance.error.CustomException;
import com.example.Attendance.error.ErrorCode;
import com.example.Attendance.model.Commute;
import com.example.Attendance.model.StoreEmployee;
import com.example.Attendance.repository.CommuteRepository;
import com.example.Attendance.repository.StoreEmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommuteService {

    private final CommuteRepository commuteRepository;
    private final StoreEmployeeRepository storeEmployeeRepository;

    @Transactional
    public boolean goToWork(StoreEmployee storeEmployee){
        Optional<Commute> lastCommute= commuteRepository.findTopByStoreEmployeeIdOrderByStartTimeDesc(storeEmployee.getId());
        LocalDateTime now= LocalDateTime.now();
        Commute commute= Commute.createCommuteCheckIn(now.toLocalDate(),now,storeEmployee);
        if(lastCommute.isPresent() &&lastCommute.get().getEndTime()==null){
            commuteRepository.save(commute);
            return false;
        }
        commuteRepository.save(commute);
        return true;
    }

    @Transactional
    public void leaveWork(StoreEmployee storeEmployee){
        Commute commute= commuteRepository
                .findTopByStoreEmployeeIdOrderByStartTimeDesc(storeEmployee.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.INVALID_COMMUTE));
        if(commute.getEndTime()!=null){
            throw new CustomException(ErrorCode.MISSING_GO_TO_WORK_RECODE);
        }
        commute.setEndTime(LocalDateTime.now());
    }

    @Transactional
    public void addDailyCommuteByPresident( CommuteByPresidentRequest request, int seId) {
        log.info("seid"+ seId);
        StoreEmployee employee = storeEmployeeRepository.findById(seId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Commute commute = request.toEntity(employee);
        commuteRepository.save(commute);
    }

    @Transactional
    public void updateDailyCommuteByPresident( CommuteByPresidentRequest request, int commuteId) {

        long commuteDuration = request.getEndTime() == null ?
                0L :
                calculateDuration(request.getStartTime(), request.getEndTime());

        commuteRepository.updateCommute(request.getStartTime(), request.getEndTime(),
                request.getCommuteDate(), commuteDuration, commuteId);
    }

    private long calculateDuration(LocalDateTime startTime, LocalDateTime endTime) {

        return  ChronoUnit.MINUTES.between(startTime, endTime);
    }

    @Transactional
    public void deleteDailyCommuteByPresident(int commuteid) {
        commuteRepository.deleteById(commuteid);
    }

    public List<CommuteMonthlyResponse> getMonthlyCommuteList(int storeId, int year, int month) {
        return commuteRepository.findMonthlyCommutesByStore(storeId,year,month)
                .stream().map(CommuteMonthlyResponse::from).toList();
    }
}
