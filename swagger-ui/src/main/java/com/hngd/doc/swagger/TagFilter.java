package com.hngd.doc.swagger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.CollectionUtils;

import io.swagger.v3.core.filter.AbstractSpecFilter;
import io.swagger.v3.core.model.ApiDescription;
import io.swagger.v3.oas.models.Operation;

public class TagFilter extends AbstractSpecFilter{

	private String tag;
	public TagFilter(String tag) {
		this.tag=tag;
	}
    @Override
    public Optional<Operation> filterOperation(Operation operation, ApiDescription api, Map<String, List<String>> params, Map<String, String> cookies, Map<String, List<String>> headers) {
    	List<String> tags=operation.getTags();
    	if(CollectionUtils.isEmpty(tags)) {
    		return Optional.empty();
    	}else if(isValidTag(tags,tag)) {
    		return Optional.of(operation);
    	}
    	return Optional.empty();
    }
    
    public static boolean isValidTag(List<String> tags,String target) {
	    return tags.stream()
		    .filter(tag->tag.contains(target))
            .findFirst()
            .isPresent();
	}
}
