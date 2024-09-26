package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.UserPoint;

public interface PointRepository {
    UserPoint insertOrUpdate(long id, long amount);

}
