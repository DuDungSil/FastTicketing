package org.example.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class SeatCodeGenUtil {

    // row, col 생성
    public List<String> generateSeatCodeList(int maxRow, int maxColumn) {
        List<String> seatCodeList = new ArrayList<>();

        for (char row = 'A'; row < 'A' + maxRow; row++) {
            for (int col = 1; col <= maxColumn; col++) {
                String seatNumber = row + String.valueOf(col);
                seatCodeList.add(seatNumber);
            }
        }

        return seatCodeList;
    }

}
