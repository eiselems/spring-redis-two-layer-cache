package com.marcuseisele.example.twolayercache.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Order implements Serializable {

    private static final long serialVersionUID = -3281088656501163895L;
    private int id;

    private long orderNumber;
}
