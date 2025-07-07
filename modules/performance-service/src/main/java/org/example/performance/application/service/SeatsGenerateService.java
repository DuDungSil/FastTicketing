package org.example.performance.application.service;

import java.util.ArrayList;
import java.util.List;
import org.example.performance.domain.entity.Seat;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SeatsGenerateService {

    // 좌석 생성: row (행 개수), column (열 개수)
    public List<Seat> generateSeats(int row, int column) {
        List<Seat> seats = new ArrayList<>();

        for (int rowIndex = 1; rowIndex <= row; rowIndex++) {
            for (int colIndex = 1; colIndex <= column; colIndex++) {
                seats.add(new Seat(rowIndex, colIndex));
            }
        }

        return seats;
    }
}
