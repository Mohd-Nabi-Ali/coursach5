package com.pack.Entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "descriptions")
@Getter
@Setter
@ToString
public class Description implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String img;
    private String text;

    @OneToOne(fetch = FetchType.EAGER)
    private Tour tour;
}