package com.bh.cp.proxy.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.bh.cp.proxy.constants.ProxyConstants;

@Component
public class OktaUserDetails {
	Map<String, List<String>> userMap;
	@SuppressWarnings("unchecked")
	public Map<String, List<String>> mappingUserData(List<Map<String, Object>> allOktaUserResponse) {
		     List<String> list;
		     userMap = new HashMap<>();
			for (int i = 0; i < allOktaUserResponse.size(); i++) {
				list = new ArrayList<>();
				Map<String,String> profile;
				Map<String,String> auth;
				profile=allOktaUserResponse.get(i).get(ProxyConstants.PROFILE)!=null ? (Map<String, String>) allOktaUserResponse.get(i).get(ProxyConstants.PROFILE):new HashMap<>();
				auth=allOktaUserResponse.get(i).get(ProxyConstants.AUTH)!=null ? (Map<String, String>) allOktaUserResponse.get(i).get(ProxyConstants.AUTH):new HashMap<>();
				String status = allOktaUserResponse.get(i).get(ProxyConstants.STATUS.toLowerCase())!=null ? allOktaUserResponse.get(i).get(ProxyConstants.STATUS.toLowerCase()).toString():"";
				if(!auth.isEmpty())
				{
				  callAuth(list,auth);
				}
				else
				{
					callProfile(list,profile,status);
				}
			}
			return userMap;
		}

		private void callProfile(List<String> list,Map<String, String> profile,String status) {
			String name;
			if(status.equalsIgnoreCase(ProxyConstants.ACTIVE))
			{
			String firstName = profile.get("firstName") != null
					? profile.get("firstName"): "";
			String lastName = profile.get("lastName") != null
					? profile.get("lastName"): "";
			name = firstName + " " + lastName;
			list.add(name);
			}
			else
			{
				list.add(ProxyConstants.INACTIVE_USER);
			}
			String presentMail = profile.get(ProxyConstants.EMAIL).toLowerCase();
			String[] checkInactiveBHMail=presentMail.split("@");
			String afterAt=checkInactiveBHMail[1];
			if(ProxyConstants.AFTER_AT.equalsIgnoreCase(afterAt))
			{
				list.add(ProxyConstants.INTERNAL_USER);
			}
			else
			{
			list.add(ProxyConstants.EXTERNAL_USER);
			}
			String samAccName = profile.get(ProxyConstants.SAM_ACC_NAME)!=null ? profile.get(ProxyConstants.SAM_ACC_NAME) :"";
			list.add(samAccName);
			userMap.put(profile.get(ProxyConstants.EMAIL).toLowerCase(), list);
		}

		private void callAuth(List<String> list,Map<String, String> auth) {
			String name;
			String firstName = auth.get(ProxyConstants.NAME) != null
					? auth.get(ProxyConstants.NAME): "";
			String lastName = auth.get(ProxyConstants.SURNAME) != null
					? auth.get(ProxyConstants.SURNAME): "";
			name = firstName + " " + lastName;
			list.add(name);
			list.add(auth.get(ProxyConstants.TITLE) != null ? auth.get(ProxyConstants.TITLE): "");
			String samAccName = auth.get(ProxyConstants.SAM_ACC_NAME)!=null ? auth.get(ProxyConstants.SAM_ACC_NAME) :"";
			list.add(samAccName);
			userMap.put(auth.get(ProxyConstants.EMAIL).toLowerCase(), list);
		}
}
