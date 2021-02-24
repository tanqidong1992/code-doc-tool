package com.hngd.parser.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.hngd.parser.javadoc.BlockTag;

import lombok.Data;
import lombok.experimental.Accessors;
/**
 * 注释修饰目标
 * @author tqd
 *
 */
@Data
@Accessors(chain = true)
public class CommentDecoratedTarget {

    /**
     * 被修饰者名称
     */
    private String name;
    /**
     * 注释
     */
    private String comment;
    /**
     * Java Doc注释,Block Tags拓展信息
     */
    private Map<String,BlockTag> extensions=new HashMap<>();
     
    public void addExtension(String key,BlockTag extension) {
        if(key==null || extension==null) {
            return;
        }
        extensions.put(key, extension);
    }
    
    public <T extends BlockTag> List<T> getExtensions(Class<T> type) {
        return extensions.values()
          .stream()
          .filter(type::isInstance)
          .map(type::cast)
          .collect(Collectors.toList());
    }
    
    public <T extends BlockTag> Optional<T> findAnyExtension(Class<T> type) {
        List<T> extensions=getExtensions(type);
        if(CollectionUtils.isEmpty(extensions)) {
            return Optional.empty();
        }else {
            return Optional.of(extensions.get(0));
        }
    }
       
}
