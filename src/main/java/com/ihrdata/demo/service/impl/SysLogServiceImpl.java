package com.ihrdata.demo.service.impl;

import com.ihrdata.demo.dao.SysLogMapper;
import com.ihrdata.demo.model.SysLog;
import com.ihrdata.demo.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("sysLogService")
public class SysLogServiceImpl implements SysLogService {
    @Autowired
    private SysLogMapper sysLogMapper;

    @Transactional
    @Override
    public void insertSysLog(SysLog sysLog) {
        sysLogMapper.insertSelective(sysLog);
    }

}
