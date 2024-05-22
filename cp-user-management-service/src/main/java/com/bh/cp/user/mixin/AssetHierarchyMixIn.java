package com.bh.cp.user.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = "fields")
public interface AssetHierarchyMixIn {

}
