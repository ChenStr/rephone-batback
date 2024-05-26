package com.ruoyi.sqsm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.constant.UserConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.file.FileTypeUtils;
import com.ruoyi.common.core.utils.file.MimeTypeUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.sqsm.domain.ProductBrand;
import com.ruoyi.sqsm.mapper.ProductBrandMapper;
import com.ruoyi.sqsm.service.ProductBrandService;
import com.ruoyi.system.api.RemoteFileService;
import com.ruoyi.system.api.domain.SysFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @author Chen Zhenyang
 * @Data 2024/4/17
 * @apiNote 商品品牌 实现层
 */
@Service
public class ProductBrandServiceImppl extends ServiceImpl<ProductBrandMapper, ProductBrand> implements ProductBrandService {

    @Autowired
    private RemoteFileService remoteFileService;

    @Override
    public String checkBrandKeyUnique(ProductBrand brand) {
        Long id = StringUtils.isNull(brand.getId()) ? -1L : brand.getId();
        LambdaQueryWrapper<ProductBrand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductBrand::getCode,brand.getCode());
        ProductBrand brand1 = this.getOne(queryWrapper);
        if (StringUtils.isNotNull(brand1) && brand1.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public List<ProductBrand> getProductBrandList(ProductBrand brand) {
        brand = checkRows(brand);
        return this.list(getQueryWrapper(brand));
    }

    @Override
    public ProductBrand selectProductBrandInfo(Long id) {
        return this.getById(id);
    }

    @Override
    public ProductBrand selectProductBrandById(ProductBrand brand) {
        brand = checkRows(brand);
        QueryWrapper<ProductBrand> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(brand.getRow()).eq("id",brand.getId());
        return this.getOne(queryWrapper);
    }

    @Override
    public Boolean insertProductBrand(ProductBrand brand) {
        // 设置创建人与创建时间
        brand = updateOrInsertRead(brand,true);
        // 查询是否已经有相应的数据
        if(StringUtils.equals(UserConstants.NOT_UNIQUE,checkBrandKeyUnique(brand))) {
            throw new ServiceException("品牌编码已重复");
        } else {
            return this.save(brand);
        }
    }

    @Override
    public Boolean updateProductBrand(ProductBrand brand) {
        brand = updateOrInsertRead(brand,false);
        // 查询是否已经有相应的数据
        if(StringUtils.equals(UserConstants.UNIQUE,checkBrandKeyUnique(brand))) {
            return this.update(getUpdate(brand));
        } else {
            throw new ServiceException("修改后编码与其他数据一致不允许修改");
        }
    }

    @Override
    public Boolean deleteProductBrandById(Long id) {
        LambdaUpdateWrapper<ProductBrand> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(ProductBrand::getId,id)
                .set(ProductBrand::getStatus,UserConstants.EXCEPTION);
        return this.update(queryWrapper);
    }

    @Override
    public Boolean removeProductBrandById(Long id) {
        LambdaQueryWrapper<ProductBrand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductBrand::getId,id).eq(ProductBrand::getStatus,UserConstants.EXCEPTION);
        ProductBrand brand = this.getOne(queryWrapper);
        if (Objects.equals(brand,null)) {
            return false;
        } else {
            // 查询旗下是否有产品
            return this.removeById(id);
        }
    }

    @Override
    public String uploadBrandImg(MultipartFile file) {
        // 判断图片文件是否为空
        if (!file.isEmpty()) {
            // 获取图片格式
            String extension = FileTypeUtils.getExtension(file);
            if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION))
            {
                throw new ServiceException("文件格式不正确，请上传" + Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION) + "格式");
            }
            // 上传品牌图片
            R<SysFile> fileResult = remoteFileService.productUpload(file);
            if (StringUtils.isNull(fileResult) || StringUtils.isNull(fileResult.getData()))
            {
                throw new ServiceException("文件服务异常，请联系管理员");
            }
            String url = fileResult.getData().getUrl();
            return url;
        }
        throw new ServiceException("上传文件为空");
    }

    private QueryWrapper getQueryWrapper(ProductBrand brand) {
        QueryWrapper<ProductBrand> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(brand.getRow());
        if (StringUtils.isNotBlank(brand.getName())) {
            queryWrapper.lambda().like(ProductBrand::getName,brand.getName());
        }
        if (StringUtils.isNotBlank(brand.getCode())) {
            queryWrapper.lambda().like(ProductBrand::getCode,brand.getCode());
        }
        if (!Objects.equals(brand.getSort(),null)) {
            queryWrapper.lambda().eq(ProductBrand::getSort,brand.getSort());
        }
        if (StringUtils.isNotBlank(brand.getStatus())) {
            queryWrapper.lambda().eq(ProductBrand::getStatus,brand.getStatus());
        }
        if (StringUtils.isNotBlank(brand.getRecommend())) {
            queryWrapper.lambda().eq(ProductBrand::getRecommend,brand.getRecommend());
        }
        if (StringUtils.isNotBlank(brand.getType())) {
            queryWrapper.lambda().eq(ProductBrand::getType,brand.getType());
        }
        return queryWrapper;
    }

    private LambdaUpdateWrapper getUpdate(ProductBrand brand) {
        // 首先修改的东西一定要在权限列表中有，否则一概不处理
        List<String> list = new ArrayList<>();
        LambdaUpdateWrapper<ProductBrand> updateWrapper = new LambdaUpdateWrapper<>();
        if (StringUtils.isNotBlank(brand.getRow())) {
            list = Arrays.asList(brand.getRow() .split(","));
        } else {
            return updateWrapper;
        }
        // 没有id的也一概不处理
        if (!Objects.equals(brand.getId(),null)) {
            updateWrapper.eq(ProductBrand::getId,brand.getId());
        } else {
            return updateWrapper;
        }
        // 可以为空的字段
        if (list.contains("remark")) {
            updateWrapper.set(ProductBrand::getRemark,brand.getRemark());
        }
        if (list.contains("logo")) {
            updateWrapper.set(ProductBrand::getLogo,brand.getLogo());
        }
        if (list.contains("img")) {
            updateWrapper.set(ProductBrand::getImg,brand.getImg());
        }
        if (list.contains("desc")) {
            updateWrapper.set(ProductBrand::getDesc,brand.getDesc());
        }
        if (list.contains("web_url")) {
            updateWrapper.set(ProductBrand::getWebUrl,brand.getWebUrl());
        }
        // 不可以为空的字段
        if (list.contains("name") && !Objects.equals(brand.getName(),null)) {
            updateWrapper.set(ProductBrand::getName,brand.getName());
        }
        if (list.contains("sort") && !Objects.equals(brand.getSort(),null)) {
            updateWrapper.set(ProductBrand::getSort,brand.getSort());
        }
        if (list.contains("recommend") && StringUtils.isNotBlank(brand.getRecommend())) {
            updateWrapper.set(ProductBrand::getRecommend,brand.getRecommend());
        }
        if (list.contains("type") && StringUtils.isNotBlank(brand.getType())) {
            updateWrapper.set(ProductBrand::getType,brand.getType());
        }
        if (list.contains("status") && StringUtils.isNotBlank(brand.getStatus())) {
            updateWrapper.set(ProductBrand::getStatus,brand.getStatus());
        }
        if (list.contains("update_by") && StringUtils.isNotBlank(brand.getUpdateBy())) {
            updateWrapper.set(ProductBrand::getUpdateBy,brand.getUpdateBy());
        }
        if (list.contains("update_time") && Objects.equals(brand.getUpdateTime(),null)) {
            updateWrapper.set(ProductBrand::getUpdateTime,brand.getUpdateTime());
        }
        return updateWrapper;
    }

    /**
     * 导入用户数据
     *
     * @param brands 品牌数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importBrands(List<ProductBrand> brands, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(brands) || brands.size() == 0)
        {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (ProductBrand brand : brands)
        {
            try
            {
                // 验证是否存在这个数据
                Boolean unique = StringUtils.equals(UserConstants.UNIQUE,checkBrandKeyUnique(brand));
                if (unique)
                {
                    this.insertProductBrand(brand);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、品牌 " + brand.getName() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    this.updateProductBrand(brand);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、品牌 " + brand.getName() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、品牌 " + brand.getName() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、品牌 " + brand.getName() + " 导入失败：";
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

    /**
     * 新增或者修改前的准备
     * @param brand 数据
     * @param isInsert 是否新增
     * @return
     */
    private ProductBrand updateOrInsertRead(ProductBrand brand,Boolean isInsert) {
        if (isInsert) {
            brand.setCreateBy(SecurityUtils.getUsername());
            brand.setCreateTime(new Date());
        } else {
            brand.setUpdateBy(SecurityUtils.getUsername());
            brand.setUpdateTime(new Date());
        }
        return brand;
    }

    /**
     * 检查列明是否合格
     * @param productBrand
     * @return
     */
    private ProductBrand checkRows(ProductBrand productBrand) {
        if (StringUtils.isNotBlank(productBrand.getRow())) {
            List<String> rows = new ArrayList<>();
            for (String row:Arrays.asList(productBrand.getRow().split(","))) {
                row = "`" + row + "`";
                rows.add(row);
            }
            productBrand.setRow(StringUtils.join(rows,","));
        }
        return productBrand;
    }
}
