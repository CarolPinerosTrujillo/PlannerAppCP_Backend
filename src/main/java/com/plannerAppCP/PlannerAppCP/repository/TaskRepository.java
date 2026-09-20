package com.plannerAppCP.PlannerAppCP.repository;

import com.plannerAppCP.PlannerAppCP.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDeviceId(String deviceId);
    List<Task> findByUserEmail(String userEmail);
}