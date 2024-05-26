package com.ruoyi.sqsm.controller;

import com.ruoyi.common.core.constant.UserConstants;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.sqsm.domain.ProductBrand;
import com.ruoyi.sqsm.service.ProductBrandService;
import com.ruoyi.system.api.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Chen Zhenyang
 * @Data 2024/4/18
 * @apiNote
 */
@RestController
@RequestMapping("/product/brand")
public class ProductBrandController extends BaseController {

    @Autowired
    ProductBrandService brandService;

    /**
     * 获取商品品牌列表
     */
    @RequiresPermissions("sqsm:product:brand:list")
    @GetMapping("/page")
    public TableDataInfo page(ProductBrand brand)
    {
        startPage();
        List<ProductBrand> list = brandService.getProductBrandList(brand);
        return getDataTable(list);
    }

    /**
     * 获取商品品牌列表
     */
    @RequiresPermissions("sqsm:product:brand:list")
    @GetMapping("/list")
    public AjaxResult list(ProductBrand brand)
    {
        List<ProductBrand> brands = brandService.getProductBrandList(brand);
        return AjaxResult.success(brands);
    }


    /**
     * 根据商品品牌ID获取详细信息
     */
    @RequiresPermissions("sqsm:product:brand:query")
    @GetMapping(value = "/get/{id}")
    public AjaxResult getInfo(@PathVariable(value = "id") Long id)
    {
        return AjaxResult.success(brandService.selectProductBrandInfo(id));
    }

    /**
     * 根据商品品牌ID获取详细信息
     */
    @RequiresPermissions("sqsm:product:brand:query")
    @GetMapping(value = "/query")
    public AjaxResult getQuery(ProductBrand brand)
    {
        return AjaxResult.success(brandService.selectProductBrandById(brand));
    }


    /**
     * 新增商品品牌
     */
    @RequiresPermissions("sqsm:product:brand:add")
    @Log(title = "商品-商品品牌", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ProductBrand brand)
    {
        if (UserConstants.NOT_UNIQUE.equals(brandService.checkBrandKeyUnique(brand)))
        {
            return AjaxResult.error("新增商品品牌'" + brand.getName() + "'失败，品牌编码已存在");
        }
        return toAjax(brandService.insertProductBrand(brand));
    }

    /**
     * 修改商品品牌
     */
    @RequiresPermissions("sqsm:product:brand:add")
    @Log(title = "商品-商品品牌", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ProductBrand brand)
    {
        if (UserConstants.NOT_UNIQUE.equals(brandService.checkBrandKeyUnique(brand)))
        {
            return AjaxResult.error("修改商品品牌'" + brand.getName() + "'失败，品牌编码已存在");
        }
        return toAjax(brandService.updateProductBrand(brand));
    }

    /**
     * 删除商品品牌
     */
    @RequiresPermissions("sqsm:product:brand:remove")
    @Log(title = "商品-商品品牌", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable(value = "id") Long id)
    {
//        if (categoryService.checkDeptExistProduct(deptId))
//        {
//            return AjaxResult.error("商品品牌下存在未禁用产品,不允许删除");
//        }
        return toAjax(brandService.deleteProductBrandById(id));
    }

    @Log(title = "商品-商品品牌", businessType = BusinessType.EXPORT)
    @RequiresPermissions("sqsm:product:brand:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProductBrand brand)
    {
        List<ProductBrand> list = brandService.getProductBrandList(brand);
        ExcelUtil<ProductBrand> util = new ExcelUtil<ProductBrand>(ProductBrand.class);
        util.exportExcel(response, list, "商品品牌数据");
    }


    @Log(title = "商品-商品品牌", businessType = BusinessType.IMPORT)
    @RequiresPermissions("sqsm:product:brand:import")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<ProductBrand> util = new ExcelUtil<ProductBrand>(ProductBrand.class);
        List<ProductBrand> brands = util.importExcel(file.getInputStream());
        String operName = SecurityUtils.getUsername();
        String message = brandService.importBrands(brands, updateSupport, operName);
        return AjaxResult.success(message);
    }

}
