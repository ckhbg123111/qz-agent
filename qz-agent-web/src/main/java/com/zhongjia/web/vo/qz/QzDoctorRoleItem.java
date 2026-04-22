package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "QzDoctorRoleItem", description = "医生角色条目")
public class QzDoctorRoleItem {

    @Schema(description = "医生工号")
    private String doctorId;

    @NotBlank(message = "医生姓名不能为空")
    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "角色，如 FIRST_ASSISTANT、SECOND_ASSISTANT")
    private String role;
}
