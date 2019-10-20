package com.taotao.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.mapper.TestMapper;
import com.taotao.service.TestService;

/**
 * @author kunlingou
 * @date 2019/10/20
 */
@Service
public class TestServiceImpl implements TestService{
	@Autowired
	private TestMapper mapper;
	@Override
	public String queryNow() {
		return mapper.queryNow();
	}
}
