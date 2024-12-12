package com.websales.admin.paging;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PagingAndSortingArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		
		return parameter.getParameterAnnotation(PagingAndSortingParam.class) != null;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer model,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		
		PagingAndSortingParam annotation = parameter.getParameterAnnotation(PagingAndSortingParam.class);
		String sortDir = webRequest.getParameter("sortDir");
		String sortField = webRequest.getParameter("sortField");
		String keyword = webRequest.getParameter("keyword");
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("sortField", sortField);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("moduleURL", annotation.moduleURL());
		
		return new PagingAndSortingHelper(model, annotation.listName(), sortField, sortDir, keyword);
	}
	
	
}
