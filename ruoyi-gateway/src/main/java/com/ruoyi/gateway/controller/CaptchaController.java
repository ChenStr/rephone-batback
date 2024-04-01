package com.ruoyi.gateway.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.gateway.service.ValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author Chen Zhenyang
 * @Data 2024/3/30
 * @apiNote
 */
@RestController
public class CaptchaController {

    @Autowired
    ValidateCodeService validateCodeService;

    /**
     * 生成验证码
     * @param
     * @return
     */
    @GetMapping("captchaImage")
    public R<?> captchaImage() throws IOException {
        // 用户登录
        // 获取登录token
        return R.ok(validateCodeService.createCaptcha());
    }

}
