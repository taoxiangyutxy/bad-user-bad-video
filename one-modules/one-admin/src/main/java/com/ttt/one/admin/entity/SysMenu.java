package com.ttt.one.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "后台菜单表")
public class SysMenu {
    @Schema(description = "菜单ID")
    @TableId
    private Long menuId;;
    @Schema(description = "父菜单ID")
    private Long parentId;
    @Schema(description = "菜单名称")
    private String menuName;
    @Schema(description = "菜单URL")
    private String path;
    @Schema(description = "授权标识")
    private String perms;
    @Schema(description = "菜单类型 0-目录，1-菜单，2-按钮")
    private Integer type;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "排序")
    private Integer sortOrder;
    @Schema(description = "组件")
    private String component;
    @Schema(description = "是否可见 0-不可见，1-可见")
    private Integer isVisible;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "权限标识")
    private String label;
    @Schema(description = "子菜单")
    @TableField(exist = false)
    private List<SysMenu> children;;
}
