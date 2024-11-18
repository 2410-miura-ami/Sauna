package com.example.sauna.controller;

import com.example.sauna.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

public class ToDoController {
    @Autowired
    TasksService tasksService;

    /*
     * タスク削除処理
     */
    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteContent(@PathVariable Integer id) {

        //タスクを削除する
        tasksService.deleteTasks(id);

        //投稿をテーブルから削除した後、トップ画面へ戻る
        return new ModelAndView("redirect:/");
    }
}
