package com.ihrdata.demo.service;

import com.ihrdata.demo.model.SysLog;

public interface SysLogService {
    /**
     * 添加日志
     *
     * @param sysLog
     */
    void insertSysLog(SysLog sysLog);
}
