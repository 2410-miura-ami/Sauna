package com.example.sauna.repository;

import com.example.sauna.repository.entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Integer> {
    //絞り込み表示
    //日付とステータス・タスク内容で絞り込みした場合
    public List<Tasks> findByLimitDateBetweenAndStatusAndContentOrderByUpdatedDateDesc(Date startDate, Date endDate, Integer status, String content);
    //日付とステータス・のみで絞り込みした場合
    public List<Tasks> findByLimitDateBetweenAndStatusOrderByUpdatedDateDesc(Date startDate, Date endDate, Integer status);
    //日付とタスク内容のみで絞り込みした場合
    public List<Tasks> findByLimitDateBetweenAndContentOrderByUpdatedDateDesc(Date startDate, Date endDate, String content);
    //日付のみで絞り込みした場合（日付の絞り込みなしも含む）
    public List<Tasks> findByLimitDateBetweenOrderByUpdatedDateDesc(Date startDate, Date endDate);
}
