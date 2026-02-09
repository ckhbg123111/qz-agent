package com.zhongjia.web.controller;

import com.zhongjia.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/hello")
@Tag(name = "Hello", description = "示例接口")
public class HelloController {

    @GetMapping
    @Operation(summary = "问候接口", description = "返回带名字的问候语")
    public Result<String> hello(@RequestParam(defaultValue = "world") @NotBlank String name) {
        return Result.success("Hello, " + name + "!");
    }

    @GetMapping("/secure")
    @Operation(summary = "受保护接口", description = "需要带 JWT 才能访问")
    public Result<String> secureHello(@RequestParam(defaultValue = "world") @NotBlank String name) {
        return Result.success("Secure hello, " + name + "!");
    }
}
