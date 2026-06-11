package com.hrms.hrms.aspect;

import com.hrms.hrms.annotation.Log;
import com.hrms.hrms.annotation.RequireRole;
import com.hrms.hrms.common.UserContext;
import com.hrms.hrms.entity.OperationLog;
import com.hrms.hrms.repository.OperationLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect     // 声明这是一个切面类
@Component  // 交给 Spring 管理
public class LogAndAuthAspect {

    @Autowired
    private OperationLogRepository logRepository;

    // 拦截所有带 @Log 或 @RequireRole 注解的方法
    @Around("@annotation(com.hrms.hrms.annotation.Log) || " +
            "@annotation(com.hrms.hrms.annotation.RequireRole)")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {

        // ── 1. 用反射获取当前执行的方法和注解 ──────────────────
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RequireRole requireRole = method.getAnnotation(RequireRole.class);
        Log logAnnotation       = method.getAnnotation(Log.class);

        // ── 2. 权限校验（如果有 @RequireRole 注解）─────────────
        if (requireRole != null) {
            String currentRole = UserContext.getRole();
            String[] allowedRoles = requireRole.value();

            // 用反射获取允许的角色列表，判断当前用户是否有权限
            boolean hasPermission = Arrays.asList(allowedRoles)
                    .contains(currentRole);

            if (!hasPermission) {
                // 无权限，记录日志并拒绝
                saveLog(method.getName(), "权限不足，拒绝访问", false);
                throw new RuntimeException("权限不足，无法执行此操作");
            }
        }

        // ── 3. 执行真正的业务方法（动态代理的核心）────────────
        boolean success = true;
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            success = false;
            // 记录失败日志后继续抛出异常
            if (logAnnotation != null) {
                saveLog(logAnnotation.action(), "执行失败: " + e.getMessage(), false);
            }
            throw e;
        }

        // ── 4. 记录操作日志（如果有 @Log 注解）────────────────
        if (logAnnotation != null && success) {
            // 用反射获取方法参数，拼接到日志描述
            String[] paramNames = signature.getParameterNames();
            Object[] paramValues = joinPoint.getArgs();
            StringBuilder desc = new StringBuilder();
            for (int i = 0; i < paramNames.length; i++) {
                if (i > 0) desc.append(", ");
                desc.append(paramNames[i]).append("=").append(paramValues[i]);
            }
            saveLog(logAnnotation.action(), desc.toString(), true);
        }

        return result;
    }

    // 保存日志到数据库
    private void saveLog(String action, String targetDesc, boolean success) {
        try {
            OperationLog log = new OperationLog();
            log.setUsername(UserContext.getUsername() != null
                    ? UserContext.getUsername() : "未知用户");
            log.setRole(UserContext.getRole() != null
                    ? UserContext.getRole() : "未知角色");
            log.setAction(action);
            log.setTargetDesc(targetDesc != null && targetDesc.length() > 195
                    ? targetDesc.substring(0, 195) + "..."
                    : targetDesc);
            log.setSuccess(success);
            log.setCreateTime(LocalDateTime.now());

            // 获取请求 IP
            try {
                ServletRequestAttributes attrs =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    HttpServletRequest request = attrs.getRequest();
                    log.setIp(request.getRemoteAddr());
                }
            } catch (Exception ignored) {}

            logRepository.save(log);
        } catch (Exception e) {
            // 日志保存失败不影响主业务
            System.err.println("日志保存失败: " + e.getMessage());
        }
    }
}