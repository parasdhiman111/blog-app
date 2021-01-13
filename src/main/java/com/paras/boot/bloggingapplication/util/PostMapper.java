package com.paras.boot.bloggingapplication.util;

import com.paras.boot.bloggingapplication.models.Posts;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author 1460344
 */
public class PostMapper {

    public Map postDetailsToMap(Posts post) {
        Map map = new HashMap();
        map.put("post_id", post.getPostId().toString());
        map.put("title", post.getPostTitle());
        map.put("body", post.getPostBody());
        map.put("created_on",post.getCreatedOn());
        map.put("created_by", post.getPublishedBy().getUserName());
        map.put("last_updated", post.getUpdatedOn());
        return map;
    }
}
