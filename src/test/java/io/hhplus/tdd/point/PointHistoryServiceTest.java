package io.hhplus.tdd.point;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static io.hhplus.tdd.point.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.UserPoint;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PointHistoryServiceTest {

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private PointHistoryService pointHistoryService;

    @DisplayName("특정 유저의 포인트 충전/이용 내역을 조회한다.")
    @Test
    void getUserPointHistory() {
        // given
        long now = System.currentTimeMillis() % 1000;
        pointHistoryRepository.insert(1L, 2000L, CHARGE, now);
        pointHistoryRepository.insert(1L, 1000L, USE, now);
        pointHistoryRepository.insert(2L, 3000L, CHARGE, now);
        pointHistoryRepository.insert(2L, 3000L, USE, now);

        // when
        List<PointHistory> pointHistoryList = pointHistoryService.getUserPointHistory(1L);

        // then
        assertThat(pointHistoryList).hasSize(2);
        assertThat(pointHistoryList).extracting( "userId", "amount", "type", "updateMillis")
                             .containsExactlyInAnyOrder(
                                 tuple(1L, 2000L, CHARGE, now),
                                 tuple(1L, 1000L, USE, now)
                             );
    }

}
