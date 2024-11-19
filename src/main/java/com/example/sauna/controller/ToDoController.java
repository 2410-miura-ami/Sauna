package com.example.sauna.controller;

import com.example.sauna.controller.form.TasksForm;
import com.example.sauna.service.TasksService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

        //Formにステータスのデフォルト値「１」をセット
        tasksForm.setStatus(1);
        // 投稿をテーブルに格納
        tasksService.saveTasks(tasksForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * タスク編集画面初期表示
     */
    //@GetMapping("/edit")
    @GetMapping("/edit")
    public ModelAndView editContent(@RequestParam(name = "editId", required=false) String id, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView();
        //IDのnull,数字チェック
        List<String> errorMessageId = new ArrayList<String>();
        //IDのnull,数字チェック
        if((id == null) || (!id.matches("^[0-9]+$"))) {
            errorMessageId.add("不正なパラメータです");
            //エラーメッセージを格納して、top画面へ遷移
            redirectAttributes.addFlashAttribute("errorMessages", errorMessageId);
            //top画面にリダイレクト
            return new ModelAndView("redirect:/");
        }

        //idをIntegerに変換
        int taskId = Integer.parseInt(id);

        //編集するタスクを取得
        TasksForm tasksForm = tasksService.editTasks(taskId);

        //Idの存在チェック
        if(tasksForm == null) {
            errorMessageId.add("不正なパラメータです");
            //エラーメッセージを格納して、トップ画面へ遷移
            redirectAttributes.addFlashAttribute("errorMessages", errorMessageId);
            //top画面にリダイレクト
            return new ModelAndView("redirect:/");
        }

        //編集時にsessionに格納したエラーメッセージを取得
        List<String> errorMessages = (List<String>) session.getAttribute("errorMessages");
        //「エラーメッセージが空じゃなければ、エラーメッセージをセットする。」mav.addObjectすることで画面表示の準備できる
        if((errorMessages != null) && (!errorMessages.isEmpty())) {
            mav.addObject("errorMessages", errorMessages);
        }
        //エラーメッセージが常に出てきてしまうので、格納後にセッションを破棄する
        session.invalidate();

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
    public ModelAndView updateContent(@PathVariable Integer id, @ModelAttribute("editTasksForm") @Validated TasksForm tasksForm, BindingResult result) {
        //エラーチェック
        /*
        if(result.hasErrors()){
            ModelAndView modelAndView = new ModelAndView("/edit");
            return modelAndView;
        }
        */

        //バリデーション
        List<String> errorMessages = new ArrayList<String>();

        if (result.hasErrors()) {
            //エラーがあったら、エラーメッセージを格納する
            //BindingResultのgetFieldErrors()により、エラーメッセージの取得
            for (FieldError error : result.getFieldErrors()){
                String message = error.getDefaultMessage();
                //取得したエラーメッセージをエラーメッセージのリストに格納
                errorMessages.add(message);
            }

            // セッションに保存（リダイレクトしても値を保存できるようにするため）
            session.setAttribute("errorMessages", errorMessages);

            //編集画面に遷移
            return new ModelAndView("redirect:/edit/{id}");
        }

        // タスクを更新するためTasksServiceのsaveTasksを実行
        tasksForm.setId(id);
        //このidのレコードを参照して持ってくる
        TasksForm tasksForm1 = tasksService.editTasks(id);

        tasksForm.setStatus(tasksForm1.getStatus());
        tasksService.saveTasks(tasksForm);
        //トップ画面へ戻る
        return new ModelAndView("redirect:/");
    }

}