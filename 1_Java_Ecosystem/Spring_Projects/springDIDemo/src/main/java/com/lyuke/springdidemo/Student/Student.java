package com.lyuke.springdidemo.Student;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Student {

    private String name;
    private String denger;
    private String email;
    private Integer age;


}
