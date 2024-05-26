package com.ruoyi.sqsm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sqsm.domain.ProductBrand;
import com.ruoyi.sqsm.domain.ProductCategory;
import com.ruoyi.system.api.domain.SysUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Chen Zhenyang
 * @Data 2024/4/17
 * @apiNote 商品品牌 服务层
 */
public interface ProductBrandService extends IService<ProductBrand> {

    /**
     * 校验参数键名是否唯一
     *
     * @param brand 参数信息
     * @return 结果 0:不唯一  1:唯一
     */
    public String checkBrandKeyUnique(ProductBrand brand);

    /**
     * 查询列表
     * @param brand 筛选条件
     * @return 查询信息
     */
    List<ProductBrand> getProductBrandList(ProductBrand brand);

    /**
     * 根据ID查询详情信息 (所有字段/ 主要用于管理员、更新前查询数据查漏补缺)
     * @param id 商品品牌id
     * @return 商品品牌详细信息
     */
    public ProductBrand selectProductBrandInfo(Long id);

    /**
     * 根据ID与字段权限信息获取产品品类
     * @param brand 产品品类
     * @return 产品品类信息
     */
    public ProductBrand selectProductBrandById(ProductBrand brand);

    /**
     * 新增保存商品品牌信息
     *
     * @param brand 商品品类信息
     * @return 结果
     */
    public Boolean insertProductBrand(ProductBrand brand);

    /**
     * 修改保存商品品牌信息
     *
     * @param brand 商品品牌信息
     * @return 结果
     */
    public Boolean updateProductBrand(ProductBrand brand);

    /**
     * 禁用商品品牌管理信息
     *
     * @param id 商品品牌ID
     * @return 结果
     */
    public Boolean deleteProductBrandById(Long id);

    /**
     * 删除商品品牌管理信息
     *
     * @param id 商品品牌ID
     * @return 结果
     */
    public Boolean removeProductBrandById(Long id);

    /**
     * 上传品牌图片
     * @param file
     * @return url 品牌地址
     */
    public String uploadBrandImg(MultipartFile file);

    /**
     * 导入品牌数据
     *
     * @param brands 品牌数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    public String importBrands(List<ProductBrand> brands, Boolean isUpdateSupport, String operName);

}
