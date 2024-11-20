package com.example.sauna.service;

import com.example.sauna.controller.form.TasksForm;
import com.example.sauna.repository.TasksRepository;
import com.example.sauna.repository.entity.Tasks;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TasksService {
    @Autowired
    TasksRepository tasksRepository;

    /*
     * レコード全件取得処理
     */
    public List<TasksForm> findAllTasks(String startDate, String endDate, Integer status, String content) {
        //絞り込み日時の入力有無のチェック
        //入力なしの場合はデフォルト値をセット
        if (!StringUtils.isBlank(startDate)) {
            startDate = startDate + " 00:00:00";
        } else {
            startDate = "2020-01-01 00:00:00";
        }
        if (!StringUtils.isBlank(endDate)) {
            endDate = endDate + " 23:59:59";
        } else {
            endDate = "2100-12-31 23:59:59";
        }
        //startDateとendDateをDate型に変換
        Date StartDate = null;
        Date EndDate = null;
        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StartDate = sdFormat.parse(startDate);
            EndDate = sdFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //ステータス・タスク内容の入力有無チェック
        //ステータス・タスク内容入力ありの場合
        List<Tasks> results = null;
        if (status != null && !StringUtils.isBlank(content)) {
            results = tasksRepository.findByLimitDateBetweenAndStatusAndContentOrderByUpdatedDateDesc(StartDate, EndDate, status, content);
            //ステータスのみ入力ありの場合
        } else if (status != null && StringUtils.isBlank(content)) {
            results = tasksRepository.findByLimitDateBetweenAndStatusOrderByUpdatedDateDesc(StartDate, EndDate, status);
            //タスク内容のみ入力ありの場合
        } else if (status == null && !StringUtils.isBlank(content)) {
            results = tasksRepository.findByLimitDateBetweenAndContentOrderByUpdatedDateDesc(StartDate, EndDate, content);
            //ステータス・タスク内容入力なしの場合
        } else {
            results = tasksRepository.findByLimitDateBetweenOrderByUpdatedDateDesc(StartDate, EndDate);
        }
        List<TasksForm> tasks = setTasksForm(results);
        return tasks;
    }

    /*
     * DBから取得したデータをFormに設定
     */
    private List<TasksForm> setTasksForm(List<Tasks> results) {
        List<TasksForm> tasks = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            TasksForm task = new TasksForm();
            Tasks result = results.get(i);
            task.setId(result.getId());
            task.setContent(result.getContent());
            task.setStatus(result.getStatus());
            task.setLimitDate(result.getLimitDate());
            task.setCreatedDate(result.getCreatedDate());
            task.setUpdatedDate(result.getUpdatedDate());
            tasks.add(task);
        }
        return tasks;
    }

    /*
     * レコード削除
     */
    public void deleteTasks(Integer id) {
        tasksRepository.deleteById(id);
    }

    /*
     * 編集画面表示のため、レコード参照
     */
    public TasksForm editTasks(Integer id) {
        List<Tasks> results = new ArrayList<>();
        results.add((Tasks)tasksRepository.findById(id).orElse(null));
        //DBから取得した値をsetTasksFormメソッドでEntity→Formに詰め直して、Controllerに戻す
        if (results.get(0) == null) {
            return null;
        }

        List<TasksForm> tasks = setTasksForm(results);
        return tasks.get(0);
    }

    /*
     * レコード追加or編集（タスク追加・投稿編集）
     */
    public void saveTasks(TasksForm reqTasks) {
        Tasks saveTasks = setTasksEntity(reqTasks);
        tasksRepository.save(saveTasks);
    }

    /*
     * 取得した情報をEntityに設定
     */
    private Tasks setTasksEntity(TasksForm reqTasks) {
        Tasks task = new Tasks();
        task.setId(reqTasks.getId());
        task.setContent(reqTasks.getContent());
        task.setStatus(reqTasks.getStatus());
        task.setLimitDate(reqTasks.getLimitDate());

        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(nowDate);
        try {
            task.setUpdatedDate(sdf.parse(currentTime));
            if (reqTasks.getCreatedDate() == null) {
                task.setCreatedDate(sdf.parse(currentTime));
            } else {
                task.setCreatedDate(reqTasks.getCreatedDate());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return task;
    }
    //ステータス変更処理
    public void editStatus(Integer status, Integer id){
        tasksRepository.editStatus(status, id);
    }
}
