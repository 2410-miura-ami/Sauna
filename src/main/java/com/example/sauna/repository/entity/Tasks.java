package com.example.sauna.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;


import java.util.Date;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Tasks {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String content;

    @Column
    private Integer status;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date limitDate;

    @Column(name = "created_date", insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
}
