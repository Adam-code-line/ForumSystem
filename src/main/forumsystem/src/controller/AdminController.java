package main.forumsystem.src.controller;

import main.forumsystem.src.service.AdminService;
import main.forumsystem.src.service.LoginService;
import main.forumsystem.src.service.impl.AdminServiceImpl;
import main.forumsystem.src.service.impl.LoginServiceImpl;
import main.forumsystem.src.entity.User;

/**
 * 管理员控制器
 * 处理系统管理功能，需要管理员权限
 */
public class AdminController {
    
    private final AdminService adminService;
    private final LoginService loginService;
    
    public AdminController() {
        this.adminService = new AdminServiceImpl();
        this.loginService = new LoginServiceImpl();
    }
    
    /**
     * 验证管理员权限的辅助方法
     */
    private boolean verifyAdminPermission(int operatorId) {
        return loginService.hasPermission(operatorId, User.UserRole.ADMIN);
    }
    
    /**
     * 获取系统统计信息
     */
    public java.util.Map<String, Object> getSystemStatistics(int operatorId) {
        if (!verifyAdminPermission(operatorId)) {
            return java.util.Map.of("error", "权限不足");
        }
        
        return adminService.getSystemStatistics();
    }
    
    /**
     * 获取用户统计信息
     */
    public java.util.Map<String, Object> getUserStatistics(int operatorId) {
        if (!verifyAdminPermission(operatorId)) {
            return java.util.Map.of("error", "权限不足");
        }
        
        return adminService.getUserStatistics();
    }
    
    // 其他管理员功能方法...
}
