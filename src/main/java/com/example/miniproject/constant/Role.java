package com.example.miniproject.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    USER("일반 회원"), ADMIN("관리자");

    String name = "";

}
