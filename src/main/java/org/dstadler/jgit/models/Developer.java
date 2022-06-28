package org.dstadler.jgit.models;

import lombok.Data;

@Data
public class Developer {
    private String name;
    private String email;

    public Developer(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
