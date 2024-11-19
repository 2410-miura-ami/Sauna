package com.example.sauna.controller.form;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class TasksForm {
    private int id;
    private String content;
    private Integer status;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date limitDate;
    private Date createdDate;
    private Date updatedDate;
}
