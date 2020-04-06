package com.sy.service.impl;


import com.sy.mapper.FunctionMapper;
import com.sy.model.Authority;
import com.sy.model.Function;
import com.sy.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class FunctionServiceImpl implements FunctionService {
    @Autowired
    private FunctionMapper functionMapper;

    @Override
    public List<Function> getSubFunctionList(Function function) throws Exception {
        return functionMapper.getSubFunctionList(function);
    }

    @Override
    public List<Function> getSubFuncList(Function function) throws Exception {
        return functionMapper.getSubFuncList(function);
    }

    @Override
    public List<Function> getMainFunctionList(Authority authority) throws Exception {
        return functionMapper.getMainFunctionList(authority);
    }

    @Override
    public Function getFunctionById(Function function) throws Exception {
        return functionMapper.getFunctionById(function);
    }

    @Override
    public List<Function> getFunctionListByIn(String sqlInString) throws Exception {
        return functionMapper.getFunctionListByIn(sqlInString);
    }

    @Override
    public List<Function> getFunctionListByRoId(Authority authority) throws Exception {
        return functionMapper.getFunctionListByRoId(authority);
    }
}
