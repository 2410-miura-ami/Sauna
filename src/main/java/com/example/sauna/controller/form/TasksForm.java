package com.example.sauna.controller.form;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TasksForm {
    private int id;
    private String content;
    private int status;
    private Date limitDate;
    private Date createdDate;
    private Date updatedDate;
}
