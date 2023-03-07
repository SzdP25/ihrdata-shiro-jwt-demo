package com.ihrdata.demo.dao;

import com.ihrdata.demo.model.SysLog;
import com.ihrdata.demo.mybatis.TkMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface SysLogMapper extends TkMapper<SysLog> {
}