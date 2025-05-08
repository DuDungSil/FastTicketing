package org.example.performance.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.common.utils.SeatCodeGenUtil;
import org.example.performance.domain.entity.Seat;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SeatsGenerateService {

    private final SeatCodeGenUtil seatCodeGenUtil;

    // row, column 기준으로 좌석 코드 생성
    public List<Seat> generateSeats(int row, int column) {
        List<String> seatCodes = seatCodeGenUtil.generateSeatCodeList(row, column);

        List<Seat> seats = seatCodes.stream()
                .map(code -> new Seat(code))
                .collect(Collectors.toList());

        return seats;
    }

}
