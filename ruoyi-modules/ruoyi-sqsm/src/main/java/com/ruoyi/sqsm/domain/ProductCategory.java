package com.ruoyi.sqsm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.annotation.Excel.ColumnType;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.math3.stat.descriptive.summary.Product;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品分类表 t_rehone_product_category
 * 
 * @author Chen Zhenyang
 */
@Data
@TableName("t_rehone_product_category")
public class ProductCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Excel(name = "品类编号", cellType = ColumnType.NUMERIC)
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 父级id */
    @Excel(name = "品类父级编号", cellType = ColumnType.NUMERIC)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentsId;

    /** 分类名称 */
    @Excel(name = "品类名称", cellType = ColumnType.STRING)
    @NotBlank(message = "分类名称不能为空")
    @Size(min = 0, max = 64, message = "分类名称长度不能超过64个字符")
    private String name;

    /** 分类编码 */
    @Excel(name = "品类编码", cellType = ColumnType.STRING)
    @NotBlank(message = "分类编码不能为空")
    @Size(min = 0, max = 64, message = "分类编码长度不能超过64个字符")
    private String code;

    /** 分类排序默认0 */
    private Integer sort;

    /** 状态（0正常 1停用） */
    @Excel(name = "品类排序", cellType = ColumnType.STRING)
    private String status;

    /** 事业部费用 */
    @Excel(name = "事业部费用", cellType = ColumnType.STRING)
    private BigDecimal cost;

    /** 祖籍列表 */
    private String ancestors;

    /** 子品类 */
    @TableField(exist = false)
    private List<ProductCategory> children = new ArrayList<ProductCategory>();

    /** 商品数量 */
    @TableField(exist = false)
    private Integer productSum;

}
