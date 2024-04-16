package com.ruoyi.sqsm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.constant.UserConstants;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.bean.BeanValidators;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.sqsm.domain.ProductCategory;
import com.ruoyi.sqsm.domain.vo.TreeSelect;
import com.ruoyi.sqsm.mapper.ProductCategoryMapper;
import com.ruoyi.sqsm.service.ProductCategoryService;
import com.ruoyi.system.api.domain.SysDept;
import com.ruoyi.system.api.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 参数配置 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Autowired
    protected Validator validator;

    @Override
    public String checkCategoryKeyUnique(ProductCategory category) {
        Long id = StringUtils.isNull(category.getId()) ? -1L : category.getId();
        LambdaQueryWrapper<ProductCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductCategory::getCode,category.getCode());
        ProductCategory category1 = this.getOne(queryWrapper);
        if (StringUtils.isNotNull(category1) && category1.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public List<ProductCategory> getProductCategoryList(ProductCategory category) {
        return this.list(getQueryWrapper(category));
    }

    @Override
    public List<ProductCategory> getProductCategoryExcludeId(ProductCategory category) {
        QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(category.getRow());
        queryWrapper.ne("id",category.getId());
        return this.list(queryWrapper);
    }

    @Override
    public List<ProductCategory> buildDeptTree(List<ProductCategory> categorys) {
        List<ProductCategory> returnList = new ArrayList<ProductCategory>();
        List<Long> tempList = new ArrayList<>();
        for (ProductCategory category : categorys)
        {
            tempList.add(category.getId());
        }
        for (ProductCategory category : categorys)
        {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(category.getParentsId()))
            {
                recursionFn(categorys, category);
                returnList.add(category);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = categorys;
        }
        return returnList;
    }

    @Override
    public List<TreeSelect> buildProductCategoryTreeSelect(List<ProductCategory> category) {
        List<ProductCategory> categoryTrees = buildDeptTree(category);
        return categoryTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    @Override
    public ProductCategory selectProductCategoryInfo(Long id) {
        return this.getById(id);
    }

    @Override
    public ProductCategory selectProductCategoryById(ProductCategory category) {
        QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(category.getRow()).eq("id",category.getId());
        return this.getOne(queryWrapper);
    }

    @Override
    public Boolean insertProductCategory(ProductCategory category) {
        ProductCategory info = this.getById(category.getParentsId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.PRODUCT_CATEGORY_NORMAL.equals(info.getStatus()))
        {
            throw new ServiceException("父级分类被异常，不允许新增");
        }
        // 设置祖籍
        category.setAncestors(info.getAncestors() + "," + category.getParentsId());
        // 设置创建人与创建时间
        category = updateOrInsertRead(category,true);
        return this.save(category);
    }

    @Override
    public Boolean updateProductCategory(ProductCategory category) {
        ProductCategory newCategory = this.getById(category.getParentsId());
        ProductCategory oldCategory = this.getById(category.getId());
        if (StringUtils.isNotNull(newCategory) && StringUtils.isNotNull(oldCategory))
        {
            String newAncestors = newCategory.getAncestors() + "," + newCategory.getId();
            String oldAncestors = oldCategory.getAncestors();
            category.setAncestors(newAncestors);
            updateProductCategoryChildren(category.getId(), newAncestors, oldAncestors);
        }
        // 设置更新人与更新时间
        category = updateOrInsertRead(category,false);
        Boolean result = this.update(getUpdate(category));
        if (UserConstants.PRODUCT_CATEGORY_NORMAL.equals(category.getStatus()) && StringUtils.isNotEmpty(category.getAncestors())
                && !StringUtils.equals("0", category.getAncestors()))
        {
            // 如果该分类是启用状态，则启用该分类的所有上级分类
            updateParentDeptStatusNormal(category);
        }
        return result;
    }

    @Override
    public Boolean deleteProductCategoryById(Long id) {
        LambdaUpdateWrapper<ProductCategory> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(ProductCategory::getId,id)
                    .set(ProductCategory::getStatus,UserConstants.PRODUCT_CATEGORY_DISABLE);
        return this.update(queryWrapper);
    }

    @Override
    public Integer hasChildByProductCategoryId(Long id) {
        Integer result = this.baseMapper.hasChildByProductCategoryId(id);
        return result;
    }

    @Override
    public String importProductCategory(List<ProductCategory> cateList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(cateList) || cateList.size() == 0)
        {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (ProductCategory cate : cateList)
        {
            try
            {
                if (Objects.equals(cate.getId(),null))
                {
                    BeanValidators.validateWithException(validator, cate);

                    cate.setCreateBy(operName);
                    cate.setCreateTime(new Date());
                    this.insertProductCategory(cate);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、品类 " + cate.getName() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    BeanValidators.validateWithException(validator, cate);
                    cate.setUpdateBy(operName);
                    cate.setUpdateTime(new Date());
                    this.update(getUpdate(cate));
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、品类 " + cate.getName() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、品类 " + cate.getName() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、品类 " + cate.getName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }


    private QueryWrapper getQueryWrapper(ProductCategory category) {
        QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(category.getRow());
        if (StringUtils.isNotBlank(category.getName())) {
            queryWrapper.lambda().like(ProductCategory::getName,category.getName());
        }
        if (StringUtils.isNotBlank(category.getCode())) {
            queryWrapper.lambda().like(ProductCategory::getCode,category.getCode());
        }
        if (!Objects.equals(category.getSort(),null)) {
            queryWrapper.lambda().eq(ProductCategory::getSort,category.getSort());
        }
        if (StringUtils.isNotBlank(category.getStatus())) {
            queryWrapper.lambda().eq(ProductCategory::getStatus,category.getStatus());
        }
        if (!Objects.equals(category.getCost(),null)) {
            queryWrapper.lambda().eq(ProductCategory::getCost,category.getCost());
        }
        return queryWrapper;
    }

    private LambdaUpdateWrapper getUpdate(ProductCategory category) {
        // 首先修改的东西一定要在权限列表中有，否则一概不处理
        List<String> list = new ArrayList<>();
        LambdaUpdateWrapper<ProductCategory> updateWrapper = new LambdaUpdateWrapper<>();
        if (StringUtils.isNotBlank(category.getRow())) {
            list = Arrays.asList(category.getRow() .split(","));
        } else {
            return updateWrapper;
        }
        // 没有id的也一概不处理
        if (!Objects.equals(category.getId(),null)) {
            updateWrapper.eq(ProductCategory::getId,category.getId());
        } else {
            return updateWrapper;
        }
        // 可以为空的字段
        if (list.contains("remark")) {
            updateWrapper.set(ProductCategory::getRemark,category.getRemark());
        }
        // 不可以为空的字段
        if (list.contains("name") && StringUtils.isNotBlank(category.getName())) {
            updateWrapper.set(ProductCategory::getName,category.getName());
        }
        if (list.contains("code") && StringUtils.isNotBlank(category.getCode())) {
            updateWrapper.set(ProductCategory::getCode,category.getCode());
        }
        if (list.contains("sort") && Objects.equals(category.getSort(),null)) {
            updateWrapper.set(ProductCategory::getSort,category.getSort());
        }
        if (list.contains("status") && StringUtils.isNotBlank(category.getStatus())) {
            updateWrapper.set(ProductCategory::getStatus,category.getStatus());
        }
        if (list.contains("cost") && Objects.equals(category.getCost(),null)) {
            updateWrapper.set(ProductCategory::getStatus,category.getStatus());
        }
        if (list.contains("ancestors") && StringUtils.isNotBlank(category.getAncestors())) {
            updateWrapper.set(ProductCategory::getAncestors,category.getAncestors());
        }
        if (list.contains("update_by") && StringUtils.isNotBlank(category.getCreateBy())) {
            updateWrapper.set(ProductCategory::getCreateBy,category.getCreateBy());
        }
        if (list.contains("update_time") && Objects.equals(category.getCreateTime(),null)) {
            updateWrapper.set(ProductCategory::getCreateBy,category.getCreateTime());
        }
        return updateWrapper;
    }

    /**
     * 递归返回树方法
     */
    private void recursionFn(List<ProductCategory> list, ProductCategory t)
    {
        // 得到子节点列表
        List<ProductCategory> childList = getChildList(list, t);
        t.setChildren(childList);
        for (ProductCategory tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<ProductCategory> getChildList(List<ProductCategory> list, ProductCategory t)
    {
        List<ProductCategory> tlist = new ArrayList<ProductCategory>();
        Iterator<ProductCategory> it = list.iterator();
        while (it.hasNext())
        {
            ProductCategory n = it.next();
            if (StringUtils.isNotNull(n.getParentsId()) && n.getParentsId().longValue() == t.getId().longValue())
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<ProductCategory> list, ProductCategory t)
    {
        return getChildList(list, t).size() > 0 ? true : false;
    }


    /**
     * 修改子元素关系
     *
     * @param id 被修改的分类ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateProductCategoryChildren(Long id, String newAncestors, String oldAncestors)
    {
        List<ProductCategory> children = this.baseMapper.selectChildrenProductCategoryById(id);
        for (ProductCategory child : children)
        {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0)
        {
            this.baseMapper.updateProductCategoryChildren(children);
        }
    }

    /**
     * 修改该分类的父级分类状态
     *
     * @param category 当前分类
     */
    private void updateParentDeptStatusNormal(ProductCategory category)
    {
        String ancestors = category.getAncestors();
        Long[] ids = Convert.toLongArray(ancestors);
        this.baseMapper.updateProductCategoryStatusNormal(ids);
    }

    /**
     * 新增或者修改前的准备
     * @param category 数据
     * @param isInsert 是否新增
     * @return
     */
    private ProductCategory updateOrInsertRead(ProductCategory category,Boolean isInsert) {
        if (isInsert) {
            category.setCreateBy(SecurityUtils.getUsername());
            category.setCreateTime(new Date());
        } else {
            category.setUpdateBy(SecurityUtils.getUsername());
            category.setUpdateTime(new Date());
        }
        return category;
    }

}
