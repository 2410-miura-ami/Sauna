package com.example.sauna.controller;

import com.example.sauna.controller.form.TasksForm;
import com.example.sauna.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ToDoController {
    @Autowired TasksService tasksService;

    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top(@RequestParam(name = "startDate",  required = false)String startDate, @RequestParam(name = "endDate", required = false)String endDate, @RequestParam(name = "status",  required = false)Integer status, @RequestParam(name = "content",  required = false)String content) {
        ModelAndView mav = new ModelAndView();
        // 投稿を全件取得(絞り込み情報を引数とする)
        List<TasksForm> tasksData = tasksService.findAllTasks(startDate, endDate, status, content);
        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを保管
        mav.addObject("tasks", tasksData);
        // 絞り込み情報ほ保管
        mav.addObject("startDate", startDate);
        mav.addObject("endDate", endDate);
        mav.addObject("currentStatus", status);
        mav.addObject("content", content);

        return mav;
    }
}
