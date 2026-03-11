package com.zhongjia.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "qz.hp")
public class QzHpProperties {

    private boolean maskReturnLink = false;

    public boolean isMaskReturnLink() {
        return maskReturnLink;
    }

    public void setMaskReturnLink(boolean maskReturnLink) {
        this.maskReturnLink = maskReturnLink;
    }
}
