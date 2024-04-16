package com.ruoyi.sqsm.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.sqsm.domain.ProductCategory;
import com.ruoyi.system.api.domain.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品分类 数据层
 * 
 * @author ruoyi
 */
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {

    /**
     * 修改子元素关系
     *
     * @param categories 子元素
     * @return 结果
     */
    public int updateProductCategoryChildren(@Param("categories") List<ProductCategory> categories);


    /**
     * 根据ID查询所有子分类
     *
     * @param id 分类ID
     * @return 分类列表
     */
    public List<ProductCategory> selectChildrenProductCategoryById(Long id);

    /**
     * 修改所在分类正常状态
     *
     * @param ids 分类ID组
     */
    public void updateProductCategoryStatusNormal(Long[] ids);

    /**
     * 是否存在子节点
     *
     * @param id 分类ID
     * @return 结果
     */
    public int hasChildByProductCategoryId(Long id);


}