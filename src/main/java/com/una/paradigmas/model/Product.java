package com.una.paradigmas.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;
    @Column(unique = true)
    private String name;
    private float price;
    private LocalDate date;
    @Transient
    private int age;

    public Product() {
    }

    public Product(Long id, String name, float price, LocalDate date) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.date = date;
        this.age = age;
    }

    public Product(String name, float price, LocalDate date) {
        this.name = name;
        this.price = price;
        this.date = date;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getAge() {
        return Period.between(this.date, LocalDate.now()).getYears();
    }

}
