package com.ihrdata.demo.common.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ihrdata.demo.common.annotation.SysLogAnnotation;
import com.ihrdata.demo.common.shiro.dto.ShiroUser;
import com.ihrdata.demo.model.SysLog;
import com.ihrdata.demo.service.SysLogService;
import com.ihrdata.wtool.common.utils.HttpContextUtil;
import com.ihrdata.wtool.common.utils.IpUtil;
import com.ihrdata.wtool.common.utils.JsonUtil;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统日志切面类
 *
 * @author 王未之
 * @date 2019/11/21
 */
@Component
@Aspect
public class SysLogAspect {
    @Autowired
    private SysLogService sysLogService;

    @Pointcut("@annotation(com.ihrdata.demo.common.annotation.SysLogAnnotation)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long time = System.currentTimeMillis() - beginTime;

        recordLog(joinPoint, time);

        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, long time) throws JsonProcessingException {
        ShiroUser shiroUser;
        try {
            shiroUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
        } catch (Exception e) {
            return;
        }

        SysLog sysLog = new SysLog();
        SysLogAnnotation sysLogAnnotation =
            ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(SysLogAnnotation.class);

        long userId = shiroUser.getId();

        if (null != null) {
            sysLog.setUserId(userId);
        }

        if (sysLogAnnotation != null) {
            sysLog.setUserOperation(sysLogAnnotation.value());
            sysLog.setOperationType(sysLogAnnotation.type());
        }
        sysLog.setReqClazz(joinPoint.getTarget().getClass().getName());
        sysLog.setReqMethod(joinPoint.getSignature().getName());
        if (sysLogAnnotation.flag()) {
            String[] args = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
            sysLog.setReqParamsName(JsonUtil.castToString(args));
            sysLog.setReqParamsValue(JsonUtil.castToString(joinPoint.getArgs()));
        }
        sysLog.setReqIp(IpUtil.getIpAddress(HttpContextUtil.getHttpServletRequest()));
        sysLog.setCostTime(time);

        sysLogService.insertSysLog(sysLog);
    }
}
