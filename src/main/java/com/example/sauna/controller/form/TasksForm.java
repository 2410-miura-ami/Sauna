package com.example.sauna.controller.form;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class TasksForm {
    private int id;

    @NotBlank(message = "タスクを入力してください")
    @Size(max = 140, message = "タスクは140文字以内で入力してください")
    private String content;

    private Integer status;

    @NotNull(message = "期限を設定してください")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date limitDate;

    private Date createdDate;

    private Date updatedDate;
}
