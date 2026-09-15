package com.plannerAppCP.PlannerAppCP.repository;

import com.plannerAppCP.PlannerAppCP.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

}