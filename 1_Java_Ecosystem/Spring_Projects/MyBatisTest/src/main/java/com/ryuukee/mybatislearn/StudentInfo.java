package com.ryuukee.mybatislearn;

import lombok.Data;

@Data
public class StudentInfo {
    Integer ID;
    String name;
    String password;
    String gender;
    String birthPlace;
}
