package com.ruoyi.sqsm.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.sqsm.service.ProductBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * 产品分类 信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/file")
public class SqsmFileController extends BaseController
{

    @Autowired
    ProductBrandService brandService;

    /**
     * 上传品牌文件
     */
    @Log(title = "上传品牌文件", businessType = BusinessType.UPDATE)
    @PostMapping("/product/uploadBrand")
    public AjaxResult avatar(@RequestParam("file") MultipartFile file)
    {
        String url = brandService.uploadBrandImg(file);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("imgUrl", url);
        return ajax;
    }

}
