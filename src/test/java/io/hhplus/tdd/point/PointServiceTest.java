package io.hhplus.tdd.point;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.hhplus.tdd.point.repository.PointHistoryRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PointServiceTest {

  @InjectMocks
  @Autowired
  private PointService pointService;

  @Mock
  @Autowired
  private PointHistoryRepository pointHistoryRepository;

  @Test
  @DisplayName("특정 유저의 포인트를 충전한다.")
  void chargeUserPointTest() {
    // given
    long id = 1L;
    long amount = 1000L;

    // when
    UserPoint userPoint = pointService.chargeUserPoint(id, amount);

    // then
    assertThat(userPoint.point()).isNotNull();
    assertThat(userPoint).extracting("id", "point")
        .contains(id, amount);
  }

  @Test
  @DisplayName("포인트 충전 시 정보가 잘 저장 되었는지 확인한다.")
  void userPointChargeHistoryTest() {
    // given
    long id = 1L;
    long amount = 1000L;

    // 충전 히스토리를 Mock 설정
    PointHistory chargeHistory = new PointHistory(id, id, amount, CHARGE, System.currentTimeMillis());
    when(pointHistoryRepository.selectAllByUserId(id)).thenReturn(List.of(chargeHistory));

    // when
    pointService.chargeUserPoint(id, amount);
    List<PointHistory> pointHistoryList = pointHistoryRepository.selectAllByUserId(id);

    // then
    assertThat(pointHistoryList).hasSize(1);
    assertThat(pointHistoryList).extracting( "userId", "amount", "type")
        .containsExactlyInAnyOrder(
            tuple(id, amount, CHARGE)
        );

    // verify 호출 여부
    verify(pointHistoryRepository, times(1)).selectAllByUserId(id);
  }

  @Test
  @DisplayName("충전하려는 포인트가 100원보다 크지 않으면 충전에 실패한다")
  void failToChargeIfAmountIsNotGreaterThan100() {
    // given
    long id = 1L;
    long chargeAmount = 90L;

    // when & then
    assertThrows(IllegalArgumentException.class, () -> {
      pointService.chargeUserPoint(id, chargeAmount);
    }, "충전할 포인트는 100원 이상이어야 합니다.");
  }
}
