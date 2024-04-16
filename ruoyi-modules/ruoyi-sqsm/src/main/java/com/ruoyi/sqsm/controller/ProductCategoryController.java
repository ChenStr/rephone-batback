package com.ruoyi.sqsm.controller;

import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.sqsm.domain.ProductCategory;
import com.ruoyi.sqsm.service.ProductCategoryService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;

/**
 * 产品分类 信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/product/category")
public class ProductCategoryController extends BaseController
{

    @Autowired
    ProductCategoryService categoryService;

    /**
     * 获取产品分类列表
     */
    @RequiresPermissions("sqsm:product:category:list")
    @GetMapping("/list")
    public AjaxResult list(ProductCategory category)
    {
        List<ProductCategory> categorys = categoryService.getProductCategoryList(category);
        return AjaxResult.success(categorys);
    }

    /**
     * 查询产品分类列表（排除节点）
     */
    @RequiresPermissions("sqsm:product:category:list")
    @GetMapping("/list/exclude")
    public AjaxResult excludeChild(ProductCategory category)
    {
        List<ProductCategory> categorys = categoryService.getProductCategoryExcludeId(category);
        Iterator<ProductCategory> it = categorys.iterator();
        while (it.hasNext())
        {
            ProductCategory d = (ProductCategory) it.next();
            if (d.getId().intValue() == category.getId()
                    || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), category.getId() + ""))
            {
                it.remove();
            }
        }
        return AjaxResult.success(categorys);
    }

    /**
     * 根据产品分类ID获取详细信息
     */
    @RequiresPermissions("sqsm:product:category:query")
    @GetMapping(value = "/get/{id}")
    public AjaxResult getInfo(@PathVariable(value = "id") Long id)
    {
        return AjaxResult.success(categoryService.selectProductCategoryInfo(id));
    }

    /**
     * 根据产品分类ID获取详细信息
     */
    @RequiresPermissions("sqsm:product:category:query")
    @GetMapping(value = "/query")
    public AjaxResult getQuery(ProductCategory productCategory)
    {
        return AjaxResult.success(categoryService.selectProductCategoryById(productCategory));
    }

    /**
     * 获取产品分类下拉树列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect(ProductCategory productCategory)
    {
        List<ProductCategory> productCategorys = categoryService.getProductCategoryList(productCategory);
        return AjaxResult.success(categoryService.buildProductCategoryTreeSelect(productCategorys));
    }

    /**
     * 新增产品分类
     */
    @RequiresPermissions("sqsm:product:category:add")
    @Log(title = "商品-商品分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ProductCategory category)
    {
//        if (UserConstants.NOT_UNIQUE.equals(categoryService.checkCategoryKeyUnique(category)))
//        {
//            return AjaxResult.error("新增产品分类'" + category.getName() + "'失败，产品编码已存在");
//        }
        return toAjax(categoryService.insertProductCategory(category));
    }

    /**
     * 修改产品分类
     */
    @RequiresPermissions("sqsm:product:category:add")
    @Log(title = "商品-商品分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ProductCategory category)
    {
//        if (UserConstants.NOT_UNIQUE.equals(categoryService.checkCategoryKeyUnique(category)))
//        {
//            return AjaxResult.error("修改产品分类'" + category.getName() + "'失败，产品编码已存在");
//        }
        if (category.getParentsId().equals(category.getId()))
        {
            return AjaxResult.error("修改产品分类'" + category.getName() + "'失败，上级分类不能是自己");
        }
        return toAjax(categoryService.updateProductCategory(category));
    }

    /**
     * 删除产品分类
     */
    @RequiresPermissions("sqsm:product:category:remove")
    @Log(title = "商品-商品分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable(value = "id") Long id)
    {
        if (categoryService.hasChildByProductCategoryId(id)>0)
        {
            return AjaxResult.error("存在下级部门,不允许删除");
        }
//        if (categoryService.checkDeptExistProduct(deptId))
//        {
//            return AjaxResult.error("产品分类存在产品,不允许删除");
//        }
        return toAjax(categoryService.deleteProductCategoryById(id));
    }

    @Log(title = "商品-商品分类", businessType = BusinessType.IMPORT)
    @RequiresPermissions("sqsm:product:category:import")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<ProductCategory> util = new ExcelUtil<ProductCategory>(ProductCategory.class);
        List<ProductCategory> cateList = util.importExcel(file.getInputStream());
        String operName = SecurityUtils.getUsername();
        String message = categoryService.importProductCategory(cateList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @Log(title = "商品-商品分类", businessType = BusinessType.EXPORT)
    @RequiresPermissions("sqsm:product:category:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProductCategory category)
    {
        List<ProductCategory> list = categoryService.getProductCategoryList(category);
        ExcelUtil<ProductCategory> util = new ExcelUtil<ProductCategory>(ProductCategory.class);
        util.exportExcel(response, list, "产品分类数据");
    }

}
