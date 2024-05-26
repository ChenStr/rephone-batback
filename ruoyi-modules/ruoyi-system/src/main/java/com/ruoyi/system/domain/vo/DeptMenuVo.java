package com.ruoyi.system.domain.vo;

/**
 * @author Chen Zhenyang
 * @Data 2024/4/19
 * @apiNote
 */
public class DeptMenuVo {

    /** 权限字符串 */
    private String perms;

    /** 列表字段权限 */
    private String column;

    /** 角色ID */
    private Long roleId;

    /** 部门ID */
    private Long deptId;

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}
