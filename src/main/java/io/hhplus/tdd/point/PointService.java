package io.hhplus.tdd.point;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static io.hhplus.tdd.point.TransactionType.USE;

import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.PointRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    private final PointHistoryRepository pointHistoryRepository;

    public UserPoint chargeUserPoint(long id, long amount) {
        if (amount < 100) {
            throw new IllegalArgumentException("충전 포인트는 100 이상이어야 합니다.");
        }

        UserPoint userPoint = insertUserPointOrUpdate(id, amount);
        pointHistoryRepository.insert(id, amount, CHARGE, System.currentTimeMillis() % 1000);
        return userPoint;
    }

    public UserPoint getUserPoint(long id) {
        return pointRepository.selectById(id);
    }

    public UserPoint userPointUse(long id, long amount) {

        UserPoint userPoint = pointRepository.selectById(id);

        if (userPoint.point() < amount)
            throw new IllegalArgumentException("보유 포인트가 부족합니다.");

        long usedPoint = usedPointCalculator(userPoint.point(), amount);
        UserPoint resultUserPoint = insertUserPointOrUpdate(userPoint.id(), usedPoint);
        pointHistoryRepository.insert(id, amount, USE, System.currentTimeMillis() % 1000);

        return resultUserPoint;
    }

    private long usedPointCalculator(long userPoint, long minusAmount) {
        return userPoint - minusAmount;
    }

    private UserPoint insertUserPointOrUpdate(long id, long amount) {
        return pointRepository.insertOrUpdate(id, amount);
    }

}