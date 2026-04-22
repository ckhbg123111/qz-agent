package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "QzComplicationItem", description = "并发症条目")
public class QzComplicationItem {

    @Schema(description = "并发症编码")
    private String complicationCode;

    @NotBlank(message = "并发症名称不能为空")
    @Schema(description = "并发症名称")
    private String complicationName;

    @Schema(description = "严重程度")
    private String severity;

    @Schema(description = "处理措施")
    private String handling;
}
