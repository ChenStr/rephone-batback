package com.ruoyi.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.report.domain.SysConfig;
import com.ruoyi.report.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;

import java.util.List;

/**
 * 参数配置 信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/config")
public class SysConfigController extends BaseController
{

    @Autowired
    ISysConfigService service;

    /**
     * 根据参数编号获取详细信息
     */
    @GetMapping(value = "/get")
    public AjaxResult getInfo()
    {
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysConfig::getConfigType,"Y");
        List<SysConfig> configList = service.list(queryWrapper);
        return AjaxResult.success(configList);
    }
}
