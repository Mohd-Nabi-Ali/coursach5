package com.pack.Entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "tours")
@Getter
@Setter
@ToString
public class Tour implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String start;
    private String finish;
    private int price;
    private String date;
    private int count;

    @OneToOne(mappedBy = "tour")
    private Description description;

    @ManyToMany(mappedBy = "tours")
    private Set<User> users;
}

