package com.sy.model.weixin;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**图文信息的Article
 * @Author ZhaoShuHao
 * @Date 2023/8/22 17:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@XStreamAlias("item")
public class Article {
    @XStreamAlias("Title")
    private String title;
    @XStreamAlias("Description")
    private String description;
    @XStreamAlias("PicUrl")
    private String picUrl;
    @XStreamAlias("Url")
    private String url;
}
