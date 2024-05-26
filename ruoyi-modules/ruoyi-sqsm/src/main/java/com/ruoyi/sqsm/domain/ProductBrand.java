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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author Chen Zhenyang
 * @Data 2024/4/17
 * @apiNote 商品品牌 数据模型
 */
@Data
@TableName("t_rephone_product_brand")
public class ProductBrand extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Excel(name = "品牌id", cellType = ColumnType.NUMERIC)
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 品牌名称 */
    @Excel(name = "品牌名称", cellType = ColumnType.STRING)
    @NotBlank(message = "品牌名称不能为空")
    @Size(min = 0, max = 32, message = "品牌名称长度不能超过64个字符")
    private String name;

    /** 品牌编码 */
    @Excel(name = "品牌编码", cellType = ColumnType.STRING)
    @NotBlank(message = "品牌编码不能为空")
    @Size(min = 0, max = 64, message = "品牌编码长度不能超过64个字符")
    private String code;

    /** 品牌logo图片地址 */
    private String logo;

    /** 品牌产品图片地址 */
    private String img;

    /** 品牌描述 */
    @Excel(name = "品牌描述", cellType = ColumnType.STRING)
    @TableField( value = "`desc`")
    private String desc;

    /** 品牌网址 */
    @Excel(name = "品牌网址", cellType = ColumnType.STRING)
    private String webUrl;

    /** 品牌排序 */
    private Integer sort;

    /** 状态（0正常 1停用） */
    @Excel(name = "品类状态", cellType = ColumnType.STRING)
    private String status;

    /** 是否推荐 */
    @Excel(name = "是否推荐", cellType = ColumnType.STRING)
    private String recommend;

    /** 品牌类型 */
    @Excel(name = "品牌类型", cellType = ColumnType.STRING)
    private String type;

}
