package com.example.sauna.controller;

import com.example.sauna.controller.form.TasksForm;
import com.example.sauna.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class ToDoController {
    @Autowired
    TasksService tasksService;

    /*
     * タスク表示処理
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

    /*
     * ステータス変更処理
     */
    @PostMapping("/editStatus/{id}")
    public ModelAndView editStatus(@PathVariable Integer id, @RequestParam(name="status") Integer status) {
        tasksService.editStatus(status, id);
        return new ModelAndView("redirect:/");
    }

    /*
     * 新規投稿画面表示
     */
    @GetMapping("/new")
    public ModelAndView newTasks() {
        ModelAndView mav = new ModelAndView();
        // 空のformを準備
        TasksForm tasksForm = new TasksForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", tasksForm);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addTasks(@ModelAttribute("formModel") @Validated TasksForm tasksForm, BindingResult result){
        //エラーチェック
        if(result.hasErrors()){
            ModelAndView modelAndView = new ModelAndView("/new");
            return modelAndView;
        }
        //タスク期限が今日以降であるかのチェック
        Date date = new Date();
        //現在日時と比較して過去の場合にture
        if(tasksForm.getLimitDate().compareTo(date) < 0){

        }
        //Formにステータスのデフォルト値「１」をセット
        tasksForm.setStatus(1);
        // 投稿をテーブルに格納
        tasksService.saveTasks(tasksForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }
}