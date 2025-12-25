package com.ttt.one.admin.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Schema(description = "后台角色")
@Data
public class SysRoleVO {
    @Schema(description = "角色ID")
    private Long roleId;
    @NotEmpty
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "角色描述")
    private String description;
    @Schema(description = "创建者ID")
    private Long createUserId;;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "角色编码")
    private String roleCode;
}
