package com.portfolio.wms.warehouse.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String address;

    public Warehouse(String name, String code, String address) {
        this.name = name;
        this.code = code;
        this.address = address;
    }

    public void update(String name, String address) {
        this.name = name;
        this.address = address;
    }
}