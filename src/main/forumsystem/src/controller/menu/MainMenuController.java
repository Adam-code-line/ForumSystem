package main.forumsystem.src.controller.menu;

import main.forumsystem.src.entity.User;
import java.util.Scanner;

/**
 * 主菜单控制器
 */
public class MainMenuController {
    
    private final Scanner scanner;
    
    public MainMenuController(Scanner scanner) {
        this.scanner = scanner;
    }
    
    /**
     * 显示主菜单
     * @param currentUser 当前登录用户
     * @return 用户选择的菜单项
     */
    public int showMainMenu(User currentUser) {
        System.out.println("\n=== 论坛主菜单 ===");
        String displayName = currentUser.getNickName() != null ? 
                           currentUser.getNickName() : currentUser.getUsername();
        System.out.println("当前用户: " + displayName + " (" + getUserRoleText(currentUser) + ")");
        
        System.out.println("\n【板块功能】");
        System.out.println("1. 查看所有板块");
        System.out.println("2. 创建新板块");
        System.out.println("3. 进入板块");
        
        System.out.println("\n【内容管理】");
        System.out.println("4. 发布主题");
        System.out.println("5. 搜索主题");
        System.out.println("6. 我的发帖");
        
        if (currentUser.getRole() == User.UserRole.MODERATOR || 
            currentUser.getRole() == User.UserRole.ADMIN) {
            System.out.println("\n【版主功能】");
            System.out.println("7. 版主管理");
        }
        
        if (currentUser.getRole() == User.UserRole.ADMIN) {
            System.out.println("\n【管理员功能】");
            System.out.println("8. 用户管理");
            System.out.println("9. 内容审核");
            System.out.println("10. 敏感词管理");
            System.out.println("11. 系统统计");
        }
        
        System.out.println("\n【个人中心】");
        System.out.println("12. 个人信息");
        System.out.println("13. 修改密码");
        System.out.println("0. 退出登录");
        
        System.out.print("请选择操作: ");
        
        return getIntInput();
    }
    
    /**
     * 获取用户角色文本
     */
    private String getUserRoleText(User user) {
        return switch (user.getRole()) {
            case USER -> "普通用户";
            case MODERATOR -> "版主";
            case ADMIN -> "管理员";
        };
    }
    
    /**
     * 获取整数输入
     */
    private int getIntInput() {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
