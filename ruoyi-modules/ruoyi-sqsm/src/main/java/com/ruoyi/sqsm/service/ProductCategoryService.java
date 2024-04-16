package com.ruoyi.sqsm.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sqsm.domain.ProductCategory;
import com.ruoyi.sqsm.domain.vo.TreeSelect;
import com.ruoyi.system.api.domain.SysDept;
import com.ruoyi.system.api.domain.SysUser;

import java.util.List;

/**
 * 产品品类 服务层
 * 
 * @author ruoyi
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 校验参数键名是否唯一
     *
     * @param category 参数信息
     * @return 结果 0:不唯一  1:唯一
     */
    public String checkCategoryKeyUnique(ProductCategory category);


    /**
     * 查询列表
     * @param category 筛选条件
     * @return 查询信息
     */
    List<ProductCategory> getProductCategoryList(ProductCategory category);

    /**
     * 查询列表，但排除指定id的数据
     * @param category 筛选条件
     * @return 查询信息
     */
    List<ProductCategory> getProductCategoryExcludeId(ProductCategory category);

    /**
     * 构建前端所需要树结构
     *
     * @param categorys 产品品类列表
     * @return 下拉树结构列表
     */
    public List<ProductCategory> buildDeptTree(List<ProductCategory> categorys);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param category 产品品类
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildProductCategoryTreeSelect(List<ProductCategory> category);

    /**
     * 根据ID查询详情信息 (所有字段/ 主要用于管理员、更新前查询数据查漏补缺)
     * @param id 产品品类id
     * @return 产品品类详细信息
     */
    public ProductCategory selectProductCategoryInfo(Long id);

    /**
     * 根据ID与字段权限信息获取产品品类
     * @param category 产品品类
     * @return 产品品类信息
     */
    public ProductCategory selectProductCategoryById(ProductCategory category);

    /**
     * 新增保存产品品类信息
     *
     * @param category 产品品类信息
     * @return 结果
     */
    public Boolean insertProductCategory(ProductCategory category);

    /**
     * 修改保存产品品类信息
     *
     * @param category 产品品类信息
     * @return 结果
     */
    public Boolean updateProductCategory(ProductCategory category);

    /**
     * 删除产品品类管理信息
     *
     * @param deptId 产品品类ID
     * @return 结果
     */
    public Boolean deleteProductCategoryById(Long deptId);

    /**
     * 是否存在产品品类子节点
     *
     * @param id 产品品类ID
     * @return 结果
     */
    public Integer hasChildByProductCategoryId(Long id);

    /**
     * 导入产品品类数据
     *
     * @param cateList 产品品类数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    public String importProductCategory(List<ProductCategory> cateList, Boolean isUpdateSupport, String operName);

}
