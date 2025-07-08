package main.forumsystem.src.controller;

import main.forumsystem.src.service.ForumService;
import main.forumsystem.src.service.AdminService;
import main.forumsystem.src.service.UserService;
import main.forumsystem.src.service.LoginService;
import main.forumsystem.src.service.ModeratorService;
import main.forumsystem.src.service.UserBlockService;
import main.forumsystem.src.service.impl.*;
import main.forumsystem.src.entity.*;
import main.forumsystem.src.controller.menu.*;

import java.util.Scanner;

/**
 * 主控制器 - 负责系统流程控制和用户界面导航
 * 实现论坛的版主管理、版块管理、内容管理等核心功能
 */
public class MainController {
    
    // 各种控制器和服务
    private final AuthController authController;
    private final ForumController forumController;
    private final UserController userController;
    private final AdminController adminController;
    
    private final ForumService forumService;
    private final AdminService adminService;
    private final UserService userService;
    private final LoginService loginService;
    private final ModeratorService moderatorService;
    private final UserBlockService userBlockService;
    
    // 功能模块控制器
    private final LoginMenuController loginMenuController;
    private final MainMenuController mainMenuController;
    private final ForumMenuController forumMenuController;
    private final AdminMenuController adminMenuController;
    private final ModeratorMenuController moderatorMenuController;
    private final UserMenuController userMenuController;
    
    // 当前登录用户
    private User currentUser;
    private Scanner scanner;
    
    public MainController() {
        // 初始化控制器
        this.authController = new AuthController();
        this.forumController = new ForumController();
        this.userController = new UserController();
        this.adminController = new AdminController();
        
        // 初始化服务
        this.forumService = new ForumServiceImpl();
        this.adminService = new AdminServiceImpl();
        this.userService = new UserServiceImpl();
        this.loginService = new LoginServiceImpl();
        this.moderatorService = new ModeratorServiceImpl();
        this.userBlockService = new UserBlockServiceImpl();
        
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
        
        // 初始化功能模块控制器
        this.loginMenuController = new LoginMenuController(authController, scanner);
        this.mainMenuController = new MainMenuController(scanner);
        this.forumMenuController = new ForumMenuController(forumService, forumController, scanner);
        this.adminMenuController = new AdminMenuController(adminService, userService, scanner);
        this.moderatorMenuController = new ModeratorMenuController(forumService, moderatorService, scanner);
        this.userMenuController = new UserMenuController(userService, authController, scanner);
    }
    
    /**
     * 系统主入口
     */
    public void start() {
        System.out.println("=== 欢迎使用论坛系统 ===");
        
        while (true) {
            if (currentUser == null) {
                // 登录流程
                User loginUser = loginMenuController.showLoginMenu();
                if (loginUser != null) {
                    currentUser = loginUser;
                    String displayName = currentUser.getNickName() != null ? 
                                       currentUser.getNickName() : currentUser.getUsername();
                    System.out.println("登录成功！欢迎 " + displayName);
                }
            } else {
                // 主菜单流程
                int choice = mainMenuController.showMainMenu(currentUser);
                handleMainMenuChoice(choice);
            }
        }
    }
    
    /**
     * 处理主菜单选择
     */
    private void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1: // 查看所有板块
                forumMenuController.viewAllForums();
                break;
            case 2: // 创建新板块
                forumMenuController.createForum(currentUser);
                // 刷新用户信息（可能升级为版主）
                currentUser = userService.getUserById(currentUser.getUserId());
                break;
            case 3: // 进入板块
                forumMenuController.enterForum(currentUser);
                break;
            case 4: // 发布主题
                forumMenuController.createTopic(currentUser);
                break;
            case 5: // 搜索主题
                forumMenuController.searchTopics(currentUser);
                break;
            case 6: // 我的发帖
                forumMenuController.viewMyPosts(currentUser);
                break;
            case 7: // 版主管理
                if (currentUser.getRole() == User.UserRole.MODERATOR || 
                    currentUser.getRole() == User.UserRole.ADMIN) {
                    moderatorMenuController.showModeratorMenu(currentUser);
                } else {
                    System.out.println("权限不足！");
                }
                break;
            case 8: // 用户管理
                if (currentUser.getRole() == User.UserRole.ADMIN) {
                    adminMenuController.showUserManagement(currentUser);
                } else {
                    System.out.println("权限不足！");
                }
                break;
            case 9: // 内容审核
                if (currentUser.getRole() == User.UserRole.ADMIN) {
                    adminMenuController.showContentModeration(currentUser);
                } else {
                    System.out.println("权限不足！");
                }
                break;
            case 10: // 敏感词管理
                if (currentUser.getRole() == User.UserRole.ADMIN) {
                    adminMenuController.showSensitiveWordManagement(currentUser);
                } else {
                    System.out.println("权限不足！");
                }
                break;
            case 11: // 系统统计
                if (currentUser.getRole() == User.UserRole.ADMIN) {
                    adminMenuController.showSystemStatistics(currentUser);
                } else {
                    System.out.println("权限不足！");
                }
                break;
            case 12: // 个人信息
                userMenuController.showPersonalCenter(currentUser);
                // 刷新用户信息（可能修改了个人资料）
                currentUser = userService.getUserById(currentUser.getUserId());
                break;
            case 13: // 修改密码
                userMenuController.changePassword(currentUser);
                break;
            case 0: // 退出登录
                logout();
                break;
            default:
                System.out.println("无效选择，请重新输入！");
        }
    }
    
    /**
     * 退出登录
     */
    private void logout() {
        if (currentUser != null) {
            authController.logout(currentUser.getUserId());
            currentUser = null;
            System.out.println("已安全退出登录！");
        }
    }
}
