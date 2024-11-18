package com.example.sauna.service;

import com.example.sauna.repository.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class TasksService {
    @Autowired
    TasksRepository tasksRepository;

    /*
     * レコード削除
     */
    public void deleteTasks(Integer id) {

        tasksRepository.deleteById(id);
    }
}
