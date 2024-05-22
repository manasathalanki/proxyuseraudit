package com.bh.cp.proxy.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;
import com.bh.cp.proxy.handler.JsonResponseHandler;

@Component
public class TypeDescriptionResponseHandler<T> extends JsonResponseHandler<T> {
	@SuppressWarnings("unchecked")
	public TypeDescriptionResponseHandler() {
		super((T) new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object parse(Map<String, Object> request) {
		HashMap<String, Object> response = (HashMap<String, Object>) getT();
		HashMap<String, Object> newResponse;

		if (!(response.containsKey(ProxyConstants.DATA))) {
			JSONObject nullObject = new JSONObject();
			nullObject.put(ProxyConstants.DATA, "No data found");
			return nullObject;
		}
		
		Map<String,Object> map=new HashMap<>();
		List<Map<String,Object>> list=new ArrayList<>();
		map.put(ProxyConstants.ID,ProxyConstants.HIGH_VALUE);
		map.put(ProxyConstants.DESCRIPTION,ProxyConstants.HIGH_DESCRIPTION);
		list.add(map);
		map=new HashMap<>();
		map.put(ProxyConstants.ID,ProxyConstants.MEDIUM_VALUE);
		map.put(ProxyConstants.DESCRIPTION,ProxyConstants.MEDIUM_DESCRIPTION);
		list.add(map);
		map=new HashMap<>();
		map.put(ProxyConstants.ID, ProxyConstants.LOW_VALUE);
		map.put(ProxyConstants.DESCRIPTION,ProxyConstants.LOW_DESCRIPTION);
		list.add(map);
		newResponse= (HashMap<String, Object>) response.get(ProxyConstants.DATA);
		newResponse.put(ProxyConstants.CUSTOMER_PRIORITIES, list);
		List<String> iterationList=new ArrayList<>();
		iterationList.add(ProxyConstants.NOTIFICATION);
		iterationList.add(ProxyConstants.TREND);
		iterationList.add(ProxyConstants.ATTACHMENT);
		List<String> commentList = new ArrayList<>();
		commentList.add(ProxyConstants.CASEUPD);
		commentList.add(ProxyConstants.FBKFROMCST);
		commentList.add(ProxyConstants.FBKFROMBH);
		
		Map<String,Object> commentType;
		Map<String,Object> newCommentType;
		List<Map<String,Object>> commentTypeList;
		List<Map<String,Object>> newCommentTypeList=new ArrayList<>();
		commentType=(Map<String, Object>) response.get(ProxyConstants.DATA);
		commentTypeList=(List<Map<String, Object>>) commentType.get(ProxyConstants.COMMENT_TYPE);

		int k=0;
		for (int i = 0; i < commentTypeList.size(); i++) {
			newCommentType=new HashMap<>();
			map=new HashMap<>();
			if(commentList.contains(commentTypeList.get(i).get(ProxyConstants.ID)))
			{
				newCommentType.put(ProxyConstants.COMMENT_TYPES,ProxyConstants.UPDATE);
			}
			else
			{
				newCommentType.put(ProxyConstants.COMMENT_TYPES,commentTypeList.get(i).get(ProxyConstants.COMMENT_TYPES));
			}
			newCommentType.put(ProxyConstants.ID, commentTypeList.get(i).get(ProxyConstants.ID)!=null?commentTypeList.get(i).get(ProxyConstants.ID):"");
			newCommentType.put(ProxyConstants.DESCRIPTION, commentTypeList.get(i).get(ProxyConstants.DESCRIPTION)!=null?commentTypeList.get(i).get(ProxyConstants.DESCRIPTION):"");
			newCommentType.put(ProxyConstants.FOR_CUSTOMER, commentTypeList.get(i).get(ProxyConstants.FOR_CUSTOMER)!=null?commentTypeList.get(i).get(ProxyConstants.FOR_CUSTOMER):"");
			if(k<3)
			{
			map.put(ProxyConstants.ID,iterationList.get(k));
			map.put(ProxyConstants.COMMENT_TYPES,iterationList.get(k));
			map.put(ProxyConstants.DESCRIPTION,"");
			map.put(ProxyConstants.FOR_CUSTOMER,ProxyConstants.EXTERNAL);
			newCommentTypeList.add(map);
			k++;
		    }
			newCommentTypeList.add(newCommentType);
		}
		newResponse.put(ProxyConstants.COMMENT_TYPE,newCommentTypeList);
		return new JSONObject().put(ProxyConstants.DATA,newResponse);
	}
}
