package com.sy.model.weixin;

import lombok.Data;

@Data
public class ImageMessage extends TextMessage {
    private com.sy.model.weixin.Image Image;
}
