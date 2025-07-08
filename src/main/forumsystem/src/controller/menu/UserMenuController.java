package main.forumsystem.src.controller.menu;

import main.forumsystem.src.service.UserService;
import main.forumsystem.src.controller.AuthController;
import main.forumsystem.src.entity.User;
import java.util.Scanner;

/**
 * 用户个人中心控制器
 */
public class UserMenuController {
    
    private final UserService userService;
    private final AuthController authController;
    private final Scanner scanner;
    private final UserBlockMenuController userBlockMenuController;
    
    public UserMenuController(UserService userService, AuthController authController, Scanner scanner) {
        this.userService = userService;
        this.authController = authController;
        this.scanner = scanner;
        this.userBlockMenuController = new UserBlockMenuController(userService, scanner);
    }
    
    /**
     * 显示个人中心
     */
    public void showPersonalCenter(User currentUser) {
        while (true) {
            System.out.println("\n=== 个人中心 ===");
            System.out.println("用户名: " + currentUser.getUsername());
            System.out.println("昵称: " + (currentUser.getNickName() != null ? currentUser.getNickName() : "未设置"));
            System.out.println("角色: " + getUserRoleText(currentUser));
            System.out.println("发帖数: " + currentUser.getPostCount());
            System.out.println("声誉: " + currentUser.getReputation());
            
            System.out.println("\n操作选项:");
            System.out.println("1. 查看个人信息");
            System.out.println("2. 修改昵称");
            System.out.println("3. 修改密码");
            System.out.println("4. 查看我的主题");
            System.out.println("5. 查看我的回复");
            System.out.println("6. 拉黑管理");  // 新增菜单项
            System.out.println("0. 返回主菜单");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    viewPersonalInfo(currentUser);
                    break;
                case 2:
                    updateNickName(currentUser);
                    break;
                case 3:
                    changePassword(currentUser);
                    break;
                case 4:
                    viewMyTopics(currentUser);
                    break;
                case 5:
                    viewMyReplies(currentUser);
                    break;
                case 6:
                    userBlockMenuController.showBlockManagement(currentUser);  // 调用拉黑管理
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选择！");
            }
        }
    }
    
    /**
     * 修改昵称
     */
    private void updateNickName(User currentUser) {
        System.out.println("\n=== 修改昵称 ===");
        System.out.println("当前昵称: " + (currentUser.getNickName() != null ? currentUser.getNickName() : "未设置"));
        System.out.print("请输入新昵称: ");
        String newNickname = scanner.nextLine();
        
        if (newNickname.trim().isEmpty()) {
            System.out.println("昵称不能为空！");
            return;
        }
        
        UserService.UserResult result = userService.updateUserNickName(currentUser.getUserId(), newNickname);
        if (result.isSuccess()) {
            currentUser.setNickName(newNickname);
            System.out.println("昵称修改成功！");
        } else {
            System.out.println("修改失败: " + result.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    public void changePassword(User currentUser) {
        System.out.println("\n=== 修改密码 ===");
        System.out.print("请输入当前密码: ");
        String oldPassword = scanner.nextLine();
        System.out.print("请输入新密码: ");
        String newPassword = scanner.nextLine();
        System.out.print("请确认新密码: ");
        String confirmPassword = scanner.nextLine();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("两次输入的密码不一致！");
            return;
        }
        
        if (newPassword.length() < 6) {
            System.out.println("密码长度不能少于6位！");
            return;
        }
        
        boolean success = authController.changePassword(currentUser.getUserId(), oldPassword, newPassword);
        if (success) {
            System.out.println("密码修改成功！");
        } else {
            System.out.println("密码修改失败，请检查当前密码是否正确！");
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private User createUpdatedUser(User currentUser) {
        User updatedUser = new User();
        updatedUser.setUserId(currentUser.getUserId());
        updatedUser.setUsername(currentUser.getUsername());
        updatedUser.setPassword(currentUser.getPassword());
        updatedUser.setEmail(currentUser.getEmail());
        updatedUser.setNickName(currentUser.getNickName());
        updatedUser.setAvatar(currentUser.getAvatar());
        updatedUser.setRole(currentUser.getRole());
        updatedUser.setStatus(currentUser.getStatus());
        updatedUser.setPostCount(currentUser.getPostCount());
        updatedUser.setReputation(currentUser.getReputation());
        return updatedUser;
    }
    
    private String getUserRoleText(User currentUser) {
        return switch (currentUser.getRole()) {
            case USER -> "普通用户";
            case MODERATOR -> "版主";
            case ADMIN -> "管理员";
        };
    }
    
    private int getIntInput() {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void viewPersonalInfo(User currentUser) {
        System.out.println("\n=== 个人信息 ===");
        System.out.println("用户名: " + currentUser.getUsername());
        System.out.println("昵称: " + (currentUser.getNickName() != null ? currentUser.getNickName() : "未设置"));
        System.out.println("邮箱: " + currentUser.getEmail());
        System.out.println("角色: " + getUserRoleText(currentUser));
        System.out.println("注册时间: " + currentUser.getRegisterTime());
        System.out.println("最后登录: " + currentUser.getLastLogin());
        System.out.println("声誉值: " + currentUser.getReputation());
        System.out.println("发帖数: " + currentUser.getPostCount());
        
        System.out.println("\n按任意键返回...");
        scanner.nextLine();
    }
    
    private void viewMyTopics(User currentUser) {
        System.out.println("\n=== 我的主题 ===");
        // TODO: 实现查看用户主题的逻辑
        System.out.println("功能尚未实现，敬请期待！");
        
        System.out.println("\n按任意键返回...");
        scanner.nextLine();
    }
    
    private void viewMyReplies(User currentUser) {
        System.out.println("\n=== 我的回复 ===");
        // TODO: 实现查看用户回复的逻辑
        System.out.println("功能尚未实现，敬请期待！");
        
        System.out.println("\n按任意键返回...");
        scanner.nextLine();
    }
}
