package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "QzHpLinkVO", description = "幽门螺杆菌落地页链接返回")
public class QzHpLinkVO {

    @Schema(description = "落地页链接")
    private String link;

    public static QzHpLinkVO of(String link) {
        QzHpLinkVO vo = new QzHpLinkVO();
        vo.setLink(link);
        return vo;
    }
}
