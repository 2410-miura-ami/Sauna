package com.example.sauna.controller;

import com.example.sauna.controller.form.TasksForm;
import com.example.sauna.service.TasksService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
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
     * タスク追加画面表示
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
     * タスク追加処理
     */
    @PostMapping("/add")
    public ModelAndView addTasks(@ModelAttribute("formModel") @Validated TasksForm tasksForm, BindingResult result,  RedirectAttributes redirectAttributes, Model model){
        //現在日時を00:00:00:00で取得
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();
        //入力されたタスク期限を取得
        Date limitDate = tasksForm.getLimitDate();

        //エラーメッセージの準備
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add("無効な日付です");

        //タスク内容にエラーがあり、タスク期限が昨日以前である場合
        if (result.hasErrors() && (limitDate != null && limitDate.compareTo(today) < 0)) {
            // エラーメッセージをセット
            model.addAttribute("errorMessages", errorMessages);
            return new ModelAndView("/new");
        //タスク内容にエラーがあり、タスク期限が今日以降または空である場合
        }else if(result.hasErrors() && !(limitDate != null && limitDate.compareTo(today) < 0)){
            return new ModelAndView("/new");
        //タスク内容のみにエラーがなく、タスク期限が昨日以前である場合
        }else if(!result.hasErrors() && (limitDate != null && limitDate.compareTo(today) < 0)){
            // エラーメッセージをセット
            model.addAttribute("errorMessages", errorMessages);
            return new ModelAndView("/new");
        }

        /*// エラーチェック
        if (result.hasErrors()) {
            //タスク期限が今日以降であるかどうかのチェック（昨日以前である場合はエラーメッセージをセット）
            if (limitDate != null && limitDate.compareTo(today) < 0) {
                // エラーメッセージを設定
                List<String> errorMessages = new ArrayList<>();
                errorMessages.add("無効な日付です");
                model.addAttribute("errorMessages", errorMessages);
            }
            return new ModelAndView("/new");
        }
        //タスク期限が今日以降であるかどうかのチェック（昨日以前である場合はエラーメッセージをセット）
        if (limitDate != null && limitDate.compareTo(today) < 0) {
            // エラーメッセージを設定
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("無効な日付です");
            // フラッシュ属性としてエラーメッセージを設定
            model.addAttribute("errorMessages", errorMessages);
            return new ModelAndView("/new");
        }*/

        //Formにステータスのデフォルト値「１」をセット
        tasksForm.setStatus(1);
        // 投稿をテーブルに格納
        tasksService.saveTasks(tasksForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }
}