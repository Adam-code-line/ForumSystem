package main.forumsystem.src.controller.menu;

import main.forumsystem.src.service.AdminService;
import main.forumsystem.src.service.UserService;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.SensitiveWord;
import main.forumsystem.src.entity.BanRecord;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 管理员功能控制器
 */
public class AdminMenuController {
    
    private final AdminService adminService;
    private final UserService userService;
    private final Scanner scanner;
    
    public AdminMenuController(AdminService adminService, UserService userService, Scanner scanner) {
        this.adminService = adminService;
        this.userService = userService;
        this.scanner = scanner;
    }
    
    /**
     * 显示用户管理菜单
     */
    public void showUserManagement(User currentUser) {
        while (true) {
            System.out.println("\n=== 用户管理 ===");
            System.out.println("1. 查看用户列表");
            System.out.println("2. 搜索用户");
            System.out.println("3. 封禁用户");
            System.out.println("4. 解封用户");
            System.out.println("5. 提升权限");
            System.out.println("6. 查看封禁记录");
            System.out.println("0. 返回主菜单");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    viewUserList();
                    break;
                case 2:
                    searchUsers();
                    break;
                case 3:
                    banUser(currentUser);
                    break;
                case 4:
                    unbanUser();
                    break;
                case 5:
                    promoteUser();
                    break;
                case 6:
                    viewBanRecords();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选择！");
            }
        }
    }
    
    /**
     * 查看用户列表
     */
    private void viewUserList() {
        System.out.println("\n=== 用户列表 ===");
        System.out.print("请输入页码(默认1): ");
        String pageInput = scanner.nextLine();
        int page = 1;
        try {
            if (!pageInput.trim().isEmpty()) {
                page = Integer.parseInt(pageInput);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        
        List<User> users = userService.getUsersByPage(page, 10);
        if (users.isEmpty()) {
            System.out.println("没有找到用户！");
            return;
        }
        
        System.out.println("用户列表 (第" + page + "页):");
        System.out.printf("%-5s %-15s %-15s %-10s %-10s %-20s\n", 
                "序号", "用户名", "昵称", "角色", "状态", "注册时间");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            String nickname = user.getNickName() != null ? user.getNickName() : "未设置";
            System.out.printf("%-5d %-15s %-15s %-10s %-10s %-20s\n",
                    (page - 1) * 10 + i + 1,
                    user.getUsername(),
                    nickname,
                    getRoleText(user.getRole()),
                    getStatusText(user.getStatus()),
                    user.getRegisterTime() != null ? user.getRegisterTime().toString() : "未知");
        }
    }
    
    /**
     * 搜索用户
     */
    private void searchUsers() {
        System.out.println("\n=== 搜索用户 ===");
        System.out.print("请输入搜索关键词(用户名或昵称): ");
        String keyword = scanner.nextLine();
        
        if (keyword.trim().isEmpty()) {
            System.out.println("搜索关键词不能为空！");
            return;
        }
        
        List<User> users = userService.searchUsers(keyword);
        if (users.isEmpty()) {
            System.out.println("没有找到匹配的用户！");
            return;
        }
        
        System.out.println("搜索结果:");
        System.out.printf("%-5s %-15s %-15s %-10s %-10s\n", 
                "序号", "用户名", "昵称", "角色", "状态");
        System.out.println("---------------------------------------------------------------");
        
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            String nickname = user.getNickName() != null ? user.getNickName() : "未设置";
            System.out.printf("%-5d %-15s %-15s %-10s %-10s\n",
                    i + 1,
                    user.getUsername(),
                    nickname,
                    getRoleText(user.getRole()),
                    getStatusText(user.getStatus()));
        }
    }
    
    /**
     * 封禁用户
     */
    private void banUser(User currentUser) {
        System.out.println("\n=== 封禁用户 ===");
        System.out.print("请输入要封禁的用户名: ");
        String username = scanner.nextLine();
        
        if (username.trim().isEmpty()) {
            System.out.println("用户名不能为空！");
            return;
        }
        
        User user = userService.getUserByUsername(username);
        if (user == null) {
            System.out.println("用户不存在！");
            return;
        }
        
        if (user.getUserId() == currentUser.getUserId()) {
            System.out.println("不能封禁自己！");
            return;
        }
        
        if (user.getRole() == User.UserRole.ADMIN) {
            System.out.println("不能封禁管理员！");
            return;
        }
        
        System.out.print("请输入封禁原因: ");
        String reason = scanner.nextLine();
        
        System.out.print("请输入封禁时长(小时，0为永久): ");
        long durationHours = 0;
        try {
            String durationInput = scanner.nextLine();
            if (!durationInput.trim().isEmpty()) {
                durationHours = Long.parseLong(durationInput);
            }
        } catch (NumberFormatException e) {
            System.out.println("时长格式错误，默认为永久封禁");
        }
        
        // 先封禁用户
        UserService.UserResult banResult = userService.banUser(user.getUserId());
        if (banResult.isSuccess()) {
            // 创建封禁记录
            boolean recordCreated = adminService.createBanRecord(
                user.getUserId(), 
                currentUser.getUserId(), 
                reason, 
                durationHours
            );
            
            if (recordCreated) {
                System.out.println("用户封禁成功！");
            } else {
                System.out.println("用户已封禁，但封禁记录创建失败！");
            }
        } else {
            System.out.println("封禁失败: " + banResult.getMessage());
        }
    }
    
    /**
     * 解封用户
     */
    private void unbanUser() {
        System.out.println("\n=== 解封用户 ===");
        System.out.print("请输入要解封的用户名: ");
        String username = scanner.nextLine();
        
        if (username.trim().isEmpty()) {
            System.out.println("用户名不能为空！");
            return;
        }
        
        User user = userService.getUserByUsername(username);
        if (user == null) {
            System.out.println("用户不存在！");
            return;
        }
        
        UserService.UserResult result = userService.unbanUser(user.getUserId());
        if (result.isSuccess()) {
            System.out.println("用户解封成功！");
        } else {
            System.out.println("解封失败: " + result.getMessage());
        }
    }
    
    /**
     * 提升权限
     */
    private void promoteUser() {
        System.out.println("\n=== 提升权限 ===");
        System.out.print("请输入要提升的用户名: ");
        String username = scanner.nextLine();
        
        if (username.trim().isEmpty()) {
            System.out.println("用户名不能为空！");
            return;
        }
        
        User user = userService.getUserByUsername(username);
        if (user == null) {
            System.out.println("用户不存在！");
            return;
        }
        
        System.out.println("当前用户角色: " + getRoleText(user.getRole()));
        System.out.println("1. 提升为版主");
        System.out.println("2. 提升为管理员");
        System.out.println("0. 取消");
        System.out.print("请选择操作: ");
        
        int choice = getIntInput();
        UserService.UserResult result = null;
        
        switch (choice) {
            case 1:
                result = userService.promoteToModerator(user.getUserId());
                break;
            case 2:
                result = userService.promoteToAdmin(user.getUserId());
                break;
            case 0:
                return;
            default:
                System.out.println("无效选择！");
                return;
        }
        
        if (result != null) {
            if (result.isSuccess()) {
                System.out.println("权限提升成功！");
            } else {
                System.out.println("提升失败: " + result.getMessage());
            }
        }
    }
    
    /**
     * 查看封禁记录
     */
    private void viewBanRecords() {
        System.out.println("\n=== 封禁记录 ===");
        System.out.println("1. 查看所有封禁记录");
        System.out.println("2. 查看指定用户封禁记录");
        System.out.println("3. 清理过期封禁记录"); // 新增选项
        System.out.println("0. 返回");
        System.out.print("请选择操作: ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1:
                adminService.cleanExpiredData();
                viewAllBanRecords();
                break;
            case 2:
                viewUserBanRecords();
                break;
            case 3:
                manualCleanExpiredBans(); // 手动清理
            case 0:
                return;
            default:
                System.out.println("无效选择！");
        }
    }
    
    /**
     * 查看所有封禁记录
     */
    private void viewAllBanRecords() {
        List<BanRecord> banRecords = adminService.getAllBanRecords();
        
        if (banRecords.isEmpty()) {
            System.out.println("暂无封禁记录！");
            return;
        }
        
        System.out.println("\n所有封禁记录:");
        // 修改表头显示用户名
        System.out.printf("%-15s %-15s %-15s %-20s %-20s %-10s\n", 
                "被封用户", "封禁原因", "执行管理员", "开始时间", "结束时间", "状态");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (BanRecord record : banRecords) {
            String endTime = record.isPermanent() ? "永久" : 
                           (record.getBanEnd() != null ? record.getBanEnd().toString() : "未知");
            String adminName = getUserNameById(record.getAdminId());
            // 获取被封用户名
            String userName = getUserNameById(record.getUserId());
            
            // 显示用户名而不是用户ID
            System.out.printf("%-15s %-15s %-15s %-20s %-20s %-10s\n",
                    userName, // 显示用户名
                    record.getReason().length() > 12 ? record.getReason().substring(0, 12) + "..." : record.getReason(),
                    adminName,
                    record.getBanStart() != null ? record.getBanStart().toString() : "未知",
                    endTime,
                    getBanStatusText(record.getStatus()));
        }
    }
    
    /**
     * 查看指定用户封禁记录
     */
    private void viewUserBanRecords() {
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine();
        
        if (username.trim().isEmpty()) {
            System.out.println("用户名不能为空！");
            return;
        }
        
        User user = userService.getUserByUsername(username);
        if (user == null) {
            System.out.println("用户不存在！");
            return;
        }
        
        List<BanRecord> banRecords = adminService.getUserBanRecords(user.getUserId());
        
        if (banRecords.isEmpty()) {
            System.out.println("该用户无封禁记录！");
            return;
        }
        
        System.out.println("\n用户 " + username + " 的封禁记录:");
        System.out.printf("%-15s %-15s %-20s %-20s %-10s\n", 
                "封禁原因", "执行管理员", "开始时间", "结束时间", "状态");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (BanRecord record : banRecords) {
            String endTime = record.isPermanent() ? "永久" : 
                           (record.getBanEnd() != null ? record.getBanEnd().toString() : "未知");
            String adminName = getUserNameById(record.getAdminId());
            
            System.out.printf("%-15s %-15s %-20s %-20s %-10s\n",
                    record.getReason(),
                    adminName,
                    record.getBanStart() != null ? record.getBanStart().toString() : "未知",
                    endTime,
                    getBanStatusText(record.getStatus()));
        }
    }

    /**
     * 手动清理过期封禁记录
     */
    private void manualCleanExpiredBans() {
        System.out.println("\n=== 清理过期封禁记录 ===");
        System.out.print("确认清理所有过期的封禁记录？(y/n): ");
        String confirm = scanner.nextLine();

        if ("y".equalsIgnoreCase(confirm)) {
            // 修复：直接调用 processExpiredBans 方法
            int cleanedCount = adminService.processExpiredBans();
            System.out.println("清理完成，共处理了 " + cleanedCount + " 条过期封禁记录");
        }
    }
    
    /**
     * 内容审核
     */
    public void showContentModeration(User currentUser) {
        while (true) {
            System.out.println("\n=== 内容审核 ===");
            System.out.println("1. 删除主题");
            System.out.println("2. 删除回复");
            System.out.println("3. 删除板块");
            System.out.println("4. 批量删除主题");
            System.out.println("5. 批量删除回复");
            System.out.println("0. 返回主菜单");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    deleteTopicByAdmin();
                    break;
                case 2:
                    deleteReplyByAdmin();
                    break;
                case 3:
                    deleteForumByAdmin();
                    break;
                case 4:
                    batchDeleteTopics();
                    break;
                case 5:
                    batchDeleteReplies();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选择！");
            }
        }
    }
    
    /**
     * 删除主题
     */
    private void deleteTopicByAdmin() {
        System.out.println("\n=== 删除主题 ===");
        System.out.print("请输入要删除的主题ID: ");
        int topicId = getIntInput();
        
        if (topicId <= 0) {
            System.out.println("主题ID无效！");
            return;
        }
        
        System.out.print("确认删除主题？(y/n): ");
        String confirm = scanner.nextLine();
        if ("y".equalsIgnoreCase(confirm)) {
            AdminService.AdminResult result = adminService.deleteTopic(topicId);
            if (result.isSuccess()) {
                System.out.println("主题删除成功！");
            } else {
                System.out.println("删除失败: " + result.getMessage());
            }
        }
    }
    
    /**
     * 删除回复
     */
    private void deleteReplyByAdmin() {
        System.out.println("\n=== 删除回复 ===");
        System.out.print("请输入要删除的回复ID: ");
        int replyId = getIntInput();
        
        if (replyId <= 0) {
            System.out.println("回复ID无效！");
            return;
        }
        
        System.out.print("确认删除回复？(y/n): ");
        String confirm = scanner.nextLine();
        if ("y".equalsIgnoreCase(confirm)) {
            AdminService.AdminResult result = adminService.deleteReply(replyId);
            if (result.isSuccess()) {
                System.out.println("回复删除成功！");
            } else {
                System.out.println("删除失败: " + result.getMessage());
            }
        }
    }
    
    /**
     * 删除板块
     */
    private void deleteForumByAdmin() {
        System.out.println("\n=== 删除板块 ===");
        System.out.print("请输入要删除的板块ID: ");
        int forumId = getIntInput();
        
        if (forumId <= 0) {
            System.out.println("板块ID无效！");
            return;
        }
        
        System.out.print("确认删除板块？(y/n): ");
        String confirm = scanner.nextLine();
        if ("y".equalsIgnoreCase(confirm)) {
            AdminService.AdminResult result = adminService.deleteForum(forumId);
            if (result.isSuccess()) {
                System.out.println("板块删除成功！");
            } else {
                System.out.println("删除失败: " + result.getMessage());
            }
        }
    }
    
    /**
     * 批量删除主题
     */
    private void batchDeleteTopics() {
        System.out.println("\n=== 批量删除主题 ===");
        System.out.print("请输入要删除的主题ID（用逗号分隔）: ");
        String input = scanner.nextLine();
        
        if (input.trim().isEmpty()) {
            System.out.println("输入不能为空！");
            return;
        }
        
        try {
            String[] idStrings = input.split(",");
            int[] topicIds = new int[idStrings.length];
            
            for (int i = 0; i < idStrings.length; i++) {
                topicIds[i] = Integer.parseInt(idStrings[i].trim());
            }
            
            System.out.print("确认批量删除 " + topicIds.length + " 个主题？(y/n): ");
            String confirm = scanner.nextLine();
            if ("y".equalsIgnoreCase(confirm)) {
                int deletedCount = adminService.batchDeleteTopics(topicIds);
                System.out.println("批量删除完成，成功删除 " + deletedCount + " 个主题");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("ID格式错误！");
        }
    }
    
    /**
     * 批量删除回复
     */
    private void batchDeleteReplies() {
        System.out.println("\n=== 批量删除回复 ===");
        System.out.print("请输入要删除的回复ID（用逗号分隔）: ");
        String input = scanner.nextLine();
        
        if (input.trim().isEmpty()) {
            System.out.println("输入不能为空！");
            return;
        }
        
        try {
            String[] idStrings = input.split(",");
            int[] replyIds = new int[idStrings.length];
            
            for (int i = 0; i < idStrings.length; i++) {
                replyIds[i] = Integer.parseInt(idStrings[i].trim());
            }
            
            System.out.print("确认批量删除 " + replyIds.length + " 个回复？(y/n): ");
            String confirm = scanner.nextLine();
            if ("y".equalsIgnoreCase(confirm)) {
                int deletedCount = adminService.batchDeleteReplies(replyIds);
                System.out.println("批量删除完成，成功删除 " + deletedCount + " 个回复");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("ID格式错误！");
        }
    }
    
    /**
     * 敏感词管理
     */
    public void showSensitiveWordManagement(User currentUser) {
        while (true) {
            System.out.println("\n=== 敏感词管理 ===");
            System.out.println("1. 查看敏感词列表");
            System.out.println("2. 添加敏感词");
            System.out.println("3. 删除敏感词");
            System.out.println("4. 检测文本");
            System.out.println("0. 返回主菜单");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    viewSensitiveWords();
                    break;
                case 2:
                    addSensitiveWord();
                    break;
                case 3:
                    deleteSensitiveWord();
                    break;
                case 4:
                    testSensitiveWord();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选择！");
            }
        }
    }
    
    /**
     * 查看敏感词列表
     */
    private void viewSensitiveWords() {
        System.out.println("\n=== 敏感词列表 ===");
        
        List<SensitiveWord> sensitiveWords = adminService.getAllSensitiveWords();
        if (sensitiveWords.isEmpty()) {
            System.out.println("暂无敏感词！");
            return;
        }
        
        System.out.printf("%-10s %-20s %-20s\n", "ID", "敏感词", "创建时间");
        System.out.println("--------------------------------------------------");
        
        for (SensitiveWord word : sensitiveWords) {
            System.out.printf("%-10d %-20s %-20s\n",
                    word.getWordId(),
                    word.getWord(),
                    word.getCreateTime() != null ? word.getCreateTime().toString() : "未知");
        }
    }
    
    /**
     * 添加敏感词
     */
    private void addSensitiveWord() {
        System.out.println("\n=== 添加敏感词 ===");
        System.out.print("请输入敏感词: ");
        String word = scanner.nextLine();
        
        if (word.trim().isEmpty()) {
            System.out.println("敏感词不能为空！");
            return;
        }
        
        boolean success = adminService.addSensitiveWord(word.trim());
        if (success) {
            System.out.println("敏感词添加成功！");
        } else {
            System.out.println("添加失败，可能该敏感词已存在！");
        }
    }
    
    /**
     * 删除敏感词
     */
    private void deleteSensitiveWord() {
        System.out.println("\n=== 删除敏感词 ===");
        System.out.print("请输入要删除的敏感词ID: ");
        int wordId = getIntInput();
        
        if (wordId <= 0) {
            System.out.println("敏感词ID无效！");
            return;
        }
        
        System.out.print("确认删除敏感词？(y/n): ");
        String confirm = scanner.nextLine();
        if ("y".equalsIgnoreCase(confirm)) {
            boolean success = adminService.deleteSensitiveWord(wordId);
            if (success) {
                System.out.println("敏感词删除成功！");
            } else {
                System.out.println("删除失败！");
            }
        }
    }
    
    /**
     * 检测敏感词
     */
    private void testSensitiveWord() {
        System.out.println("\n=== 敏感词检测 ===");
        System.out.print("请输入要检测的文本: ");
        String text = scanner.nextLine();
        
        if (text.trim().isEmpty()) {
            System.out.println("文本不能为空！");
            return;
        }
        
        boolean containsSensitive = adminService.containsSensitiveWord(text);
        if (containsSensitive) {
            System.out.println("检测结果: 文本包含敏感词！");
        } else {
            System.out.println("检测结果: 文本未包含敏感词。");
        }
    }
    
    /**
     * 系统统计
     */
    public void showSystemStatistics(User currentUser) {
        System.out.println("\n=== 系统统计 ===");
        
        Map<String, Object> stats = adminService.getSystemStatistics();
        
        System.out.println("用户统计:");
        System.out.println("  总用户数: " + stats.getOrDefault("totalUsers", 0));
        System.out.println("  活跃用户: " + stats.getOrDefault("activeUsers", 0));
        System.out.println("  今日新增: " + stats.getOrDefault("todayNewUsers", 0));
        
        System.out.println("\n板块统计:");
        System.out.println("  总板块数: " + stats.getOrDefault("totalForums", 0));
        System.out.println("  活跃板块: " + stats.getOrDefault("activeForums", 0));
        
        System.out.println("\n内容统计:");
        System.out.println("  总主题数: " + stats.getOrDefault("totalTopics", 0));
        System.out.println("  今日主题: " + stats.getOrDefault("todayTopics", 0));
        
        System.out.println("\n系统管理:");
        System.out.println("  当前封禁: " + stats.getOrDefault("activeBans", 0));
        System.out.println("  敏感词数: " + stats.getOrDefault("totalSensitiveWords", 0));
        
        System.out.println("\n详细统计:");
        System.out.println("1. 用户详细统计");
        System.out.println("2. 板块详细统计");
        System.out.println("3. 内容详细统计");
        System.out.println("4. 数据清理");
        System.out.println("0. 返回");
        System.out.print("请选择操作: ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1:
                showDetailedUserStatistics();
                break;
            case 2:
                showDetailedForumStatistics();
                break;
            case 3:
                showDetailedContentStatistics();
                break;
            case 4:
                showDataCleaningMenu();
                break;
            case 0:
                return;
            default:
                System.out.println("无效选择！");
        }
    }
    
    /**
     * 显示详细用户统计
     */
    private void showDetailedUserStatistics() {
        System.out.println("\n=== 详细用户统计 ===");
        
        Map<String, Object> stats = adminService.getUserStatistics();
        
        System.out.println("用户总数统计:");
        System.out.println("  总用户数: " + stats.getOrDefault("totalUsers", 0));
        System.out.println("  活跃用户: " + stats.getOrDefault("activeUsers", 0));
        System.out.println("  今日新增: " + stats.getOrDefault("todayNewUsers", 0));
        
        System.out.println("\n按角色统计:");
        System.out.println("  管理员: " + stats.getOrDefault("admins", 0));
        System.out.println("  版主: " + stats.getOrDefault("moderators", 0));
        System.out.println("  普通用户: " + stats.getOrDefault("users", 0));
        
        System.out.println("\n按状态统计:");
        System.out.println("  活跃用户: " + stats.getOrDefault("activeUsersByStatus", 0));
        System.out.println("  封禁用户: " + stats.getOrDefault("bannedUsers", 0));
        
        System.out.println("\n按任意键返回...");
        scanner.nextLine();
    }
    
    /**
     * 显示详细板块统计
     */
    private void showDetailedForumStatistics() {
        System.out.println("\n=== 详细板块统计 ===");
        
        Map<String, Object> stats = adminService.getForumStatistics();
        
        System.out.println("板块统计:");
        System.out.println("  总板块数: " + stats.getOrDefault("totalForums", 0));
        System.out.println("  活跃板块: " + stats.getOrDefault("activeForums", 0));
        System.out.println("  正常板块: " + stats.getOrDefault("activeForumsByStatus", 0));
        
        System.out.println("\n按任意键返回...");
        scanner.nextLine();
    }
    
    /**
     * 显示详细内容统计
     */
    private void showDetailedContentStatistics() {
        System.out.println("\n=== 详细内容统计 ===");
        
        Map<String, Object> stats = adminService.getContentStatistics();
        
        System.out.println("主题统计:");
        System.out.println("  总主题数: " + stats.getOrDefault("totalTopics", 0));
        System.out.println("  今日主题: " + stats.getOrDefault("todayTopics", 0));
        
        System.out.println("\n按状态统计:");
        System.out.println("  正常主题: " + stats.getOrDefault("normalTopics", 0));
        System.out.println("  隐藏主题: " + stats.getOrDefault("hiddenTopics", 0));
        System.out.println("  删除主题: " + stats.getOrDefault("deletedTopics", 0));
        
        System.out.println("\n按任意键返回...");
        scanner.nextLine();
    }
    
    /**
     * 数据清理菜单
     */
    private void showDataCleaningMenu() {
        while (true) {
            System.out.println("\n=== 数据清理 ===");
            System.out.println("1. 清理过期数据");
            System.out.println("2. 清理非活跃用户");
            System.out.println("3. 清理空板块");
            System.out.println("0. 返回");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    cleanExpiredData();
                    break;
                case 2:
                    cleanInactiveUsers();
                    break;
                case 3:
                    cleanEmptyForums();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选择！");
            }
        }
    }
    
    /**
     * 清理过期数据
     */
    private void cleanExpiredData() {
        System.out.println("\n=== 清理过期数据 ===");
        System.out.print("确认清理过期数据？(y/n): ");
        String confirm = scanner.nextLine();
        
        if ("y".equalsIgnoreCase(confirm)) {
            AdminService.AdminResult result = adminService.cleanExpiredData();
            if (result.isSuccess()) {
                System.out.println("清理成功: " + result.getMessage());
            } else {
                System.out.println("清理失败: " + result.getMessage());
            }
        }
    }
    
    /**
     * 清理非活跃用户
     */
    private void cleanInactiveUsers() {
        System.out.println("\n=== 清理非活跃用户 ===");
        System.out.print("请输入非活跃天数阈值（默认90天）: ");
        String input = scanner.nextLine();
        
        int inactiveDays = 90;
        try {
            if (!input.trim().isEmpty()) {
                inactiveDays = Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            System.out.println("输入格式错误，使用默认值90天");
        }
        
        System.out.print("确认清理超过 " + inactiveDays + " 天未登录的用户？(y/n): ");
        String confirm = scanner.nextLine();
        
        if ("y".equalsIgnoreCase(confirm)) {
            AdminService.AdminResult result = adminService.cleanInactiveUsers(inactiveDays);
            if (result.isSuccess()) {
                System.out.println("清理成功: " + result.getMessage());
            } else {
                System.out.println("清理失败: " + result.getMessage());
            }
        }
    }
    
    /**
     * 清理空板块
     */
    private void cleanEmptyForums() {
        System.out.println("\n=== 清理空板块 ===");
        System.out.print("确认清理没有任何内容的空板块？(y/n): ");
        String confirm = scanner.nextLine();
        
        if ("y".equalsIgnoreCase(confirm)) {
            AdminService.AdminResult result = adminService.cleanEmptyForums();
            if (result.isSuccess()) {
                System.out.println("清理成功: " + result.getMessage());
            } else {
                System.out.println("清理失败: " + result.getMessage());
            }
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
    
    private String getStatusText(User.UserStatus status) {
        return switch (status) {
            case ACTIVE -> "活跃";
            case BANNED -> "封禁";
            case INACTIVE -> "非活跃";
        };
    }
    
    private String getBanStatusText(BanRecord.BanStatus status) {
        return switch (status) {
            case ACTIVE -> "生效中";
            case LIFTED -> "已解除";
        };
    }
    
    private String getUserNameById(int userId) {
        User user = userService.getUserById(userId);
        return user != null ? user.getUsername() : "未知用户";
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
