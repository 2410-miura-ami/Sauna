package com.example.sauna.controller;

import com.example.sauna.controller.form.TasksForm;
import com.example.sauna.service.TasksService;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ToDoController {
    @Autowired
    TasksService tasksService;

    @Autowired
    HttpSession session;
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
    public ModelAndView editContent(@PathVariable String id) {
        ModelAndView mav = new ModelAndView();
        //IDのnull,数字チェック
        List<String> errorMessages = new ArrayList<String>();
        //IDのnull,数字チェック
        if(!id.matches("^[0-9]+$") || (StringUtils.isBlank(id))) {
            errorMessages.add("不正なパラメータです");
            //エラーメッセージを格納して、top画面へ遷移
            mav.addObject("errorMessages", errorMessages);
            //top画面にリダイレクト
            return new ModelAndView("redirect:/");
        }

        //idをIntegerに変換
        int taskId = Integer.parseInt(id);

        //編集するタスクを取得
        TasksForm tasksForm = tasksService.editTasks(taskId);

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

        //タスク期限をDate型へ変換
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String currentTime = sdf.parse(tasksForm.getLimitDate());


        // タスクを更新するためTasksServiceのsaveTasksを実行
        tasksForm.setId(id);
        //このidのレコードを参照して持ってくる
        TasksForm tasksForm1 = tasksService.editTasks(id);

        tasksForm.setStatus(tasksForm1.getStatus());
        tasksService.saveTasks(tasksForm);
        // その後、rootディレクトリ、つまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        //投稿をテーブルに格納した後、その投稿を表示させてトップ画面へ戻るという仕様
        return new ModelAndView("redirect:/");
    }

}