package com.plannerAppCP.PlannerAppCP.repository;

import com.plannerAppCP.PlannerAppCP.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.deviceId = :deviceId")
    List<Task> findByDeviceId(@Param("deviceId") String deviceId);

    @Query("SELECT t FROM Task t WHERE t.userEmail = :userEmail")
    List<Task> findByUserEmail(@Param("userEmail") String userEmail);
}