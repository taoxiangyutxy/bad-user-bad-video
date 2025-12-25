package com.ttt.one.admin.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Schema(description = "后台菜单表")
public class SysMenuVO {
    @Schema(description = "菜单ID")
    private Long menuId;;
    @Schema(description = "父菜单ID")
    private Long parentId;
    @NotEmpty(message = "菜单名称不能为空")
    @Schema(description = "菜单名称")
    private String menuName;
    @Schema(description = "菜单URL")
    private String path;
    @Schema(description = "授权标识")
    private String perms;
    @NotNull(message = "菜单类型不能为空")
    @Schema(description = "菜单类型 0-目录，1-菜单，2-按钮")
    private Integer type;
    @Schema(description = "图标")
    private String icon;
    @NotNull(message = "排序不能为空")
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
}
