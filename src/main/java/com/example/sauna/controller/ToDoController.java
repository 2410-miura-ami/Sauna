package com.example.sauna.controller;

import com.example.sauna.controller.form.TasksForm;
import com.example.sauna.service.TasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
     * タスク編集画面初期表示
     */
    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        //編集するタスクを取得
        TasksForm tasksForm = tasksService.editTasks(id);
        //編集する投稿を保管
        mav.addObject("editTasksForm", tasksForm);
        //画面遷移先を指定(edit.html)
        mav.setViewName("/edit");

        return mav;
    }

    /*
     * タスク編集処理
     */
    @PutMapping("/update/{id}")
    public ModelAndView updateContent(@PathVariable Integer id, @ModelAttribute("editTasksForm") TasksForm tasksForm) {
        //バリデーション
        //List<String> errorMessages = new ArrayList<String>();

        /*
        if (bindingResult.hasErrors()) {
            //エラーがあったら、エラーメッセージを格納する
            //BindingResultのgetFieldErrors()により、(フィールド名と)エラーメッセージを取得できます
            for (FieldError error : bindingResult.getFieldErrors()){
                String message = error.getDefaultMessage();
                //取得したエラーメッセージをエラーメッセージのリストに格納
                errorMessages.add(message);
            }

            // セッションに保存（リダイレクトしても値を保存できるようにするため）
            session.setAttribute("errorMessages", errorMessages);

            //編集画面に遷移
            return new ModelAndView("redirect:/edit/{id}");
        }
        */

        // タスクを更新するためTasksServiceのsaveTasksを実行
        tasksForm.setId(id);
        tasksService.saveTasks(tasksForm);
        // その後、rootディレクトリ、つまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        //投稿をテーブルに格納した後、その投稿を表示させてトップ画面へ戻るという仕様
        return new ModelAndView("redirect:/");
    }

}