package com.example.sauna.controller;

import com.example.sauna.controller.form.TasksForm;
import com.example.sauna.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ToDoController {
    @Autowired
    TasksService tasksService;

    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top() {
        ModelAndView mav = new ModelAndView();
        // 投稿を全件取得
        List<TasksForm> tasksData = tasksService.findAllTasks();
        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを保管
        mav.addObject("tasks", tasksData);
        return mav;
    }

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