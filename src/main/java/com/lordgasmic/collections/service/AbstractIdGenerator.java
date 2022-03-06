package com.lordgasmic.collections.service;

import com.lordgasmic.collections.GenericService;
import lombok.Data;

@Data
public abstract class AbstractIdGenerator implements GenericService {
    private String mItemDescriptor;
    private String mCurrentId;
    private String mPrefix;
    private String mSuffix;
}
