package com.example.myapplication2.api.objects;

import java.io.Serializable;

public class UserIdObject implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    public UserIdObject(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
