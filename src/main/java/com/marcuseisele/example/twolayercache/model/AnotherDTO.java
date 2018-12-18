package com.marcuseisele.example.twolayercache.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AnotherDTO implements Serializable {

    private static final long serialVersionUID = 6076109187134928213L;

    private int id;
    private String someThing;
    private String anotherThing;
}