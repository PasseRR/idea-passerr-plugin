package com.github.passerr.idea.plugins.spring.web

/**
 * controller方法支持注解
 * @date 2021/06/25 18:20
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
enum MappingAnnotation {
    GET_MAPPING("org.springframework.web.bind.annotation.GetMapping", "GET"),
    POST_MAPPING("org.springframework.web.bind.annotation.PostMapping", "POST"),
    PUT_MAPPING("org.springframework.web.bind.annotation.PutMapping", "PUT"),
    DELETE_MAPPING("org.springframework.web.bind.annotation.DeleteMapping", "DELETE"),
    PATCH_MAPPING("org.springframework.web.bind.annotation.PatchMapping", "PATCH"),
    REQUEST_MAPPING("org.springframework.web.bind.annotation.RequestMapping")

    String name
    private String method

    MappingAnnotation(String name, String method) {
        this.name = name
        this.method = method
    }

    MappingAnnotation(String name) {
        this.name = name
    }
}