package main.forumsystem.src.controller.menu;

import main.forumsystem.src.controller.AuthController;
import main.forumsystem.src.service.LoginService;
import main.forumsystem.src.entity.User;
import java.util.Scanner;

/**
 * 登录菜单控制器
 */
public class LoginMenuController {
    
    private final AuthController authController;
    private final Scanner scanner;
    
    public LoginMenuController(AuthController authController, Scanner scanner) {
        this.authController = authController;
        this.scanner = scanner;
    }
    
    /**
     * 显示登录菜单
     * @return 登录成功的用户对象，失败返回null
     */
    public User showLoginMenu() {
        System.out.println("\n=== 登录菜单 ===");
        System.out.println("1. 普通用户登录");
        System.out.println("2. 管理员登录");
        System.out.println("3. 用户注册");
        System.out.println("4. 管理员注册");
        System.out.println("0. 退出系统");
        System.out.print("请选择操作: ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1:
                return userLogin();
            case 2:
                return adminLogin();
            case 3:
                userRegister();
                return null;
            case 4:
                adminRegister();
                return null;
            case 0:
                System.out.println("感谢使用！");
                System.exit(0);
                break;
            default:
                System.out.println("无效选择，请重新输入！");
                return null;
        }
        return null;
    }
    
    /**
     * 普通用户登录
     */
    private User userLogin() {
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine();
        System.out.print("请输入密码: ");
        String password = scanner.nextLine();
        
        LoginService.LoginResult result = authController.login(username, password);
        if (result.isSuccess()) {
            return result.getUser();
        } else {
            System.out.println("登录失败: " + result.getMessage());
            return null;
        }
    }
    
    /**
     * 管理员登录
     */
    private User adminLogin() {
        System.out.print("请输入管理员用户名: ");
        String username = scanner.nextLine();
        System.out.print("请输入密码: ");
        String password = scanner.nextLine();
        
        LoginService.LoginResult result = authController.login(username, password);
        if (result.isSuccess() && result.getUser().getRole() == User.UserRole.ADMIN) {
            return result.getUser();
        } else {
            System.out.println("管理员登录失败: " + (result.isSuccess() ? "权限不足" : result.getMessage()));
            return null;
        }
    }
    
    /**
     * 用户注册
     */
    private void userRegister() {
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine();
        System.out.print("请输入密码: ");
        String password = scanner.nextLine();
        System.out.print("请输入邮箱: ");
        String email = scanner.nextLine();
        
        LoginService.LoginResult result = authController.register(username, password, email);
        if (result.isSuccess()) {
            System.out.println("注册成功！请登录使用。");
        } else {
            System.out.println("注册失败: " + result.getMessage());
        }
    }
    
    /**
     * 管理员注册
     */
    private void adminRegister() {
        System.out.print("请输入管理员用户名: ");
        String username = scanner.nextLine();
        System.out.print("请输入密码: ");
        String password = scanner.nextLine();
        System.out.print("请输入邮箱: ");
        String email = scanner.nextLine();
        System.out.print("请输入管理员密钥: ");
        String adminKey = scanner.nextLine();
        
        LoginService.LoginResult result = authController.registerAdmin(username, password, email, adminKey);
        if (result.isSuccess()) {
            System.out.println("管理员注册成功！请登录使用。");
        } else {
            System.out.println("注册失败: " + result.getMessage());
        }
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
