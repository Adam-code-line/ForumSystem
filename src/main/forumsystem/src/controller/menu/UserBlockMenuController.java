package main.forumsystem.src.controller.menu;

import main.forumsystem.src.service.UserBlockService;
import main.forumsystem.src.service.UserService;
import main.forumsystem.src.service.impl.UserBlockServiceImpl;
import main.forumsystem.src.entity.User;

import java.util.List;
import java.util.Scanner;

/**
 * 用户拉黑管理控制器
 */
public class UserBlockMenuController {
    
    private final UserBlockService userBlockService;
    private final UserService userService;
    private final Scanner scanner;
    
    public UserBlockMenuController(UserService userService, Scanner scanner) {
        this.userBlockService = new UserBlockServiceImpl();
        this.userService = userService;
        this.scanner = scanner;
    }
    
    /**
     * 显示拉黑管理菜单
     */
    public void showBlockManagement(User currentUser) {
        while (true) {
            System.out.println("\n=== 拉黑管理 ===");
            System.out.println("1. 拉黑用户");
            System.out.println("2. 取消拉黑");
            System.out.println("3. 查看拉黑列表");
            System.out.println("4. 查看谁拉黑了我");
            System.out.println("5. 检查拉黑状态");
            System.out.println("0. 返回");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    blockUser(currentUser);
                    break;
                case 2:
                    unblockUser(currentUser);
                    break;
                case 3:
                    viewBlockedUsers(currentUser);
                    break;
                case 4:
                    viewBlockedByUsers(currentUser);
                    break;
                case 5:
                    checkBlockStatus(currentUser);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选择！");
            }
        }
    }
    
    /**
     * 拉黑用户
     */
    private void blockUser(User currentUser) {
        System.out.println("\n=== 拉黑用户 ===");
        System.out.print("请输入要拉黑的用户名: ");
        String username = scanner.nextLine();
        
        if (username.trim().isEmpty()) {
            System.out.println("用户名不能为空！");
            return;
        }
        
        User targetUser = userService.getUserByUsername(username.trim());
        if (targetUser == null) {
            System.out.println("用户不存在！");
            return;
        }
        
        if (targetUser.getUserId() == currentUser.getUserId()) {
            System.out.println("不能拉黑自己！");
            return;
        }
        
        System.out.print("请输入拉黑原因（可选）: ");
        String reason = scanner.nextLine();
        
        UserBlockService.BlockResult result = userBlockService.blockUser(
                currentUser.getUserId(), 
                targetUser.getUserId(), 
                reason.trim().isEmpty() ? null : reason.trim()
        );
        
        if (result.isSuccess()) {
            System.out.println("拉黑成功！用户 " + username + " 已被拉黑。");
        } else {
            System.out.println("拉黑失败: " + result.getMessage());
        }
    }
    
    /**
     * 取消拉黑
     */
    private void unblockUser(User currentUser) {
        System.out.println("\n=== 取消拉黑 ===");
        
        // 先显示当前拉黑列表
        List<User> blockedUsers = userBlockService.getBlockedUsers(currentUser.getUserId());
        if (blockedUsers.isEmpty()) {
            System.out.println("您没有拉黑任何用户！");
            return;
        }
        
        System.out.println("当前拉黑的用户:");
        System.out.printf("%-5s %-15s %-15s\n", "序号", "用户名", "昵称");
        System.out.println("----------------------------------------");
        
        for (int i = 0; i < blockedUsers.size(); i++) {
            User user = blockedUsers.get(i);
            String nickname = user.getNickName() != null ? user.getNickName() : "未设置";
            System.out.printf("%-5d %-15s %-15s\n", i + 1, user.getUsername(), nickname);
        }
        
        System.out.print("请输入要取消拉黑的用户名: ");
        String username = scanner.nextLine();
        
        if (username.trim().isEmpty()) {
            System.out.println("用户名不能为空！");
            return;
        }
        
        User targetUser = userService.getUserByUsername(username.trim());
        if (targetUser == null) {
            System.out.println("用户不存在！");
            return;
        }
        
        UserBlockService.BlockResult result = userBlockService.unblockUser(
                currentUser.getUserId(), 
                targetUser.getUserId()
        );
        
        if (result.isSuccess()) {
            System.out.println("取消拉黑成功！用户 " + username + " 已被移出拉黑列表。");
        } else {
            System.out.println("取消拉黑失败: " + result.getMessage());
        }
    }
    
    /**
     * 查看拉黑列表
     */
    private void viewBlockedUsers(User currentUser) {
        System.out.println("\n=== 我的拉黑列表 ===");
        
        List<User> blockedUsers = userBlockService.getBlockedUsers(currentUser.getUserId());
        if (blockedUsers.isEmpty()) {
            System.out.println("您没有拉黑任何用户！");
            return;
        }
        
        System.out.printf("%-5s %-15s %-15s %-10s %-20s\n", 
                "序号", "用户名", "昵称", "角色", "注册时间");
        System.out.println("------------------------------------------------------------------------");
        
        for (int i = 0; i < blockedUsers.size(); i++) {
            User user = blockedUsers.get(i);
            String nickname = user.getNickName() != null ? user.getNickName() : "未设置";
            String role = getRoleText(user.getRole());
            String registerTime = user.getRegisterTime() != null ? 
                                user.getRegisterTime().toString().substring(0, 16) : "未知";
            
            System.out.printf("%-5d %-15s %-15s %-10s %-20s\n",
                    i + 1, user.getUsername(), nickname, role, registerTime);
        }
        
        System.out.println("\n总计拉黑用户: " + blockedUsers.size() + " 人");
    }
    
    /**
     * 查看谁拉黑了我
     */
    private void viewBlockedByUsers(User currentUser) {
        System.out.println("\n=== 拉黑了我的用户 ===");
        
        List<User> blockedByUsers = userBlockService.getBlockedByUsers(currentUser.getUserId());
        if (blockedByUsers.isEmpty()) {
            System.out.println("没有用户拉黑您！");
            return;
        }
        
        System.out.printf("%-5s %-15s %-15s %-10s\n", 
                "序号", "用户名", "昵称", "角色");
        System.out.println("--------------------------------------------------");
        
        for (int i = 0; i < blockedByUsers.size(); i++) {
            User user = blockedByUsers.get(i);
            String nickname = user.getNickName() != null ? user.getNickName() : "未设置";
            String role = getRoleText(user.getRole());
            
            System.out.printf("%-5d %-15s %-15s %-10s\n",
                    i + 1, user.getUsername(), nickname, role);
        }
        
        System.out.println("\n总计: " + blockedByUsers.size() + " 人拉黑了您");
    }
    
    /**
     * 检查拉黑状态
     */
    private void checkBlockStatus(User currentUser) {
        System.out.println("\n=== 检查拉黑状态 ===");
        System.out.print("请输入要检查的用户名: ");
        String username = scanner.nextLine();
        
        if (username.trim().isEmpty()) {
            System.out.println("用户名不能为空！");
            return;
        }
        
        User targetUser = userService.getUserByUsername(username.trim());
        if (targetUser == null) {
            System.out.println("用户不存在！");
            return;
        }
        
        if (targetUser.getUserId() == currentUser.getUserId()) {
            System.out.println("这就是您自己！");
            return;
        }
        
        boolean iBlocked = userBlockService.isUserBlocked(currentUser.getUserId(), targetUser.getUserId());
        boolean blockedMe = userBlockService.isUserBlocked(targetUser.getUserId(), currentUser.getUserId());
        boolean mutual = userBlockService.isMutualBlock(currentUser.getUserId(), targetUser.getUserId());
        
        System.out.println("\n拉黑状态检查结果:");
        System.out.println("  您是否拉黑了 " + username + ": " + (iBlocked ? "是" : "否"));
        System.out.println("  " + username + " 是否拉黑了您: " + (blockedMe ? "是" : "否"));
        System.out.println("  是否相互拉黑: " + (mutual ? "是" : "否"));
        
        if (mutual) {
            System.out.println("\n提示: 你们相互拉黑，无法看到对方的任何内容。");
        } else if (iBlocked) {
            System.out.println("\n提示: 您拉黑了该用户，将看不到其发布的内容。");
        } else if (blockedMe) {
            System.out.println("\n提示: 该用户拉黑了您，您无法在其管理的板块发言。");
        } else {
            System.out.println("\n提示: 你们之间没有拉黑关系，可以正常互动。");
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private String getRoleText(User.UserRole role) {
        return switch (role) {
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
}
