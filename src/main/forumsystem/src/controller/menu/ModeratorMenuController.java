package main.forumsystem.src.controller.menu;

import main.forumsystem.src.service.ForumService;
import main.forumsystem.src.service.ModeratorService;
import main.forumsystem.src.entity.*;
import java.util.List;
import java.util.Scanner;
import main.forumsystem.src.service.impl.UserServiceImpl;
import main.forumsystem.src.service.UserService;


/**
 * 版主功能控制器
 */
public class ModeratorMenuController {
    
    private final ForumService forumService;
    private final ModeratorService moderatorService;
    private final Scanner scanner;
    
    public ModeratorMenuController(ForumService forumService, ModeratorService moderatorService, Scanner scanner) {
        this.forumService = forumService;
        this.moderatorService = moderatorService;
        this.scanner = scanner;
    }
    
    /**
     * 显示版主管理菜单
     */
    public void showModeratorMenu(User currentUser) {
        while (true) {
            System.out.println("\n=== 版主管理 ===");
            System.out.println("1. 查看我管理的板块");
            System.out.println("2. 板块内容审核");
            System.out.println("3. 主题管理");
            System.out.println("4. 用户管理");
            System.out.println("5. 统计信息");
            System.out.println("0. 返回主菜单");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    viewMyManagedForums(currentUser);
                    break;
                case 2:
                    moderateForumContent(currentUser);
                    break;
                case 3:
                    manageTopics(currentUser);
                    break;
                case 4:
                    moderateUsers(currentUser);
                    break;
                case 5:
                    viewModeratorStatistics(currentUser);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选择！");
            }
        }
    }
    
    /**
     * 查看我管理的板块
     */
    private void viewMyManagedForums(User currentUser) {
        System.out.println("\n=== 我管理的板块 ===");
        
        List<Forum> myForums = moderatorService.getForumsByModerator(currentUser.getUserId());
        
        if (myForums.isEmpty()) {
            System.out.println("您暂未管理任何板块！");
            return;
        }
        
        System.out.printf("%-5s %-20s %-30s %-10s %-10s\n", 
                "序号", "板块名称", "描述", "主题数", "帖子数");
        System.out.println("------------------------------------------------------------------------");
        
        for (int i = 0; i < myForums.size(); i++) {
            Forum forum = myForums.get(i);
            System.out.printf("%-5d %-20s %-30s %-10d %-10d\n",
                    i + 1,
                    forum.getForumName(),
                    forum.getDescription(),
                    forum.getTopicCount(),
                    forum.getPostCount());
        }
        
        System.out.println("\n操作选项:");
        System.out.println("1. 进入板块管理");
        System.out.println("0. 返回");
        System.out.print("请选择操作: ");
        
        int choice = getIntInput();
        if (choice == 1) {
            System.out.print("请选择要管理的板块编号: ");
            int forumChoice = getIntInput();
            if (forumChoice > 0 && forumChoice <= myForums.size()) {
                Forum selectedForum = myForums.get(forumChoice - 1);
                manageSpecificForum(selectedForum, currentUser);
            } else {
                System.out.println("无效的板块编号！");
            }
        }
    }
    
    /**
     * 管理特定板块
     */
    private void manageSpecificForum(Forum forum, User currentUser) {
        while (true) {
            System.out.println("\n=== 管理板块: " + forum.getForumName() + " ===");
            System.out.println("1. 查看板块主题");
            System.out.println("2. 置顶主题");
            System.out.println("3. 取消置顶主题");
            System.out.println("4. 锁定主题");
            System.out.println("5. 解锁主题");
            System.out.println("6. 删除主题");
            System.out.println("7. 移动主题");
            System.out.println("8. 审核主题");
            System.out.println("9. 板块设置");
            System.out.println("0. 返回");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    viewForumTopics(forum);
                    break;
                case 2:
                    pinTopic(forum, currentUser);
                    break;
                case 3:
                    unpinTopic(forum, currentUser);
                    break;
                case 4:
                    lockTopic(forum, currentUser);
                    break;
                case 5:
                    unlockTopic(forum, currentUser);
                    break;
                case 6:
                    deleteTopic(forum, currentUser);
                    break;
                case 7:
                    moveTopic(forum, currentUser);
                    break;
                case 8:
                    reviewTopic(forum, currentUser);
                    break;
                case 9:
                    forumSettings(forum, currentUser);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选择！");
            }
        }
    }
    
    /**
     * 查看板块主题
     */
    private void viewForumTopics(Forum forum) {
        System.out.println("\n=== " + forum.getForumName() + " 主题列表 ===");
        
        List<Topic> topics = forumService.getTopicsByForum(forum.getForumId(), 1, 20);
        UserService userService = new UserServiceImpl();

        if (topics.isEmpty()) {
            System.out.println("该板块暂无主题！");
            return;
        }
        
        System.out.printf("%-5s %-30s %-15s %-10s %-10s %-10s\n", 
                "ID", "标题", "作者", "回复数", "浏览数", "状态");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (Topic topic : topics) {
            String status = "";
            if (topic.isPinned()) status += "[置顶]";
            if (topic.isLocked()) status += "[锁定]";
            if (status.isEmpty()) status = "正常";
            
            System.out.printf("%-5d %-30s %-15s %-10d %-10d %-10s\n",
                    userService.getUserName(topic.getUserId()),
                    topic.getTitle().length() > 25 ? topic.getTitle().substring(0, 25) + "..." : topic.getTitle(),
                    userService.getUserName(topic.getUserId()),
                    topic.getReplyCount(),
                    topic.getViewCount(),
                    status);
        }
    }
    
    /**
     * 置顶主题
     */
    private void pinTopic(Forum forum, User currentUser) {
        System.out.println("\n=== 置顶主题 ===");
        System.out.print("请输入要置顶的主题ID: ");
        int topicId = getIntInput();
        
        if (topicId <= 0) {
            System.out.println("无效的主题ID！");
            return;
        }
        
        ModeratorService.ModeratorResult result = moderatorService.pinTopic(topicId, currentUser.getUserId());
        if (result.isSuccess()) {
            System.out.println("主题置顶成功！");
        } else {
            System.out.println("置顶失败: " + result.getMessage());
        }
    }
    
    /**
     * 取消置顶主题
     */
    private void unpinTopic(Forum forum, User currentUser) {
        System.out.println("\n=== 取消置顶主题 ===");
        System.out.print("请输入要取消置顶的主题ID: ");
        int topicId = getIntInput();
        
        if (topicId <= 0) {
            System.out.println("无效的主题ID！");
            return;
        }
        
        ModeratorService.ModeratorResult result = moderatorService.unpinTopic(topicId, currentUser.getUserId());
        if (result.isSuccess()) {
            System.out.println("取消置顶成功！");
        } else {
            System.out.println("操作失败: " + result.getMessage());
        }
    }
    
    /**
     * 锁定主题
     */
    private void lockTopic(Forum forum, User currentUser) {
        System.out.println("\n=== 锁定主题 ===");
        System.out.print("请输入要锁定的主题ID: ");
        int topicId = getIntInput();
        
        if (topicId <= 0) {
            System.out.println("无效的主题ID！");
            return;
        }
        
        System.out.print("请输入锁定原因: ");
        String reason = scanner.nextLine();
        
        ModeratorService.ModeratorResult result = moderatorService.lockTopic(topicId, currentUser.getUserId(), reason);
        if (result.isSuccess()) {
            System.out.println("主题锁定成功！");
        } else {
            System.out.println("锁定失败: " + result.getMessage());
        }
    }
    
    /**
     * 解锁主题
     */
    private void unlockTopic(Forum forum, User currentUser) {
        System.out.println("\n=== 解锁主题 ===");
        System.out.print("请输入要解锁的主题ID: ");
        int topicId = getIntInput();
        
        if (topicId <= 0) {
            System.out.println("无效的主题ID！");
            return;
        }
        
        ModeratorService.ModeratorResult result = moderatorService.unlockTopic(topicId, currentUser.getUserId());
        if (result.isSuccess()) {
            System.out.println("主题解锁成功！");
        } else {
            System.out.println("解锁失败: " + result.getMessage());
        }
    }
    
    /**
     * 删除主题
     */
    private void deleteTopic(Forum forum, User currentUser) {
        System.out.println("\n=== 删除主题 ===");
        System.out.print("请输入要删除的主题ID: ");
        int topicId = getIntInput();
        
        if (topicId <= 0) {
            System.out.println("无效的主题ID！");
            return;
        }
        
        System.out.print("请输入删除原因: ");
        String reason = scanner.nextLine();
        
        System.out.print("确认删除主题？(y/n): ");
        String confirm = scanner.nextLine();
        if ("y".equalsIgnoreCase(confirm)) {
            ModeratorService.ModeratorResult result = moderatorService.deleteTopic(topicId, currentUser.getUserId(), reason);
            if (result.isSuccess()) {
                System.out.println("主题删除成功！");
            } else {
                System.out.println("删除失败: " + result.getMessage());
            }
        }
    }
    
    /**
     * 移动主题
     */
    private void moveTopic(Forum forum, User currentUser) {
        System.out.println("\n=== 移动主题 ===");
        System.out.print("请输入要移动的主题ID: ");
        int topicId = getIntInput();
        
        if (topicId <= 0) {
            System.out.println("无效的主题ID！");
            return;
        }
        
        System.out.print("请输入目标板块ID: ");
        int targetForumId = getIntInput();
        
        if (targetForumId <= 0) {
            System.out.println("无效的板块ID！");
            return;
        }
        
        ModeratorService.ModeratorResult result = moderatorService.moveTopic(topicId, targetForumId, currentUser.getUserId());
        if (result.isSuccess()) {
            System.out.println("主题移动成功！");
        } else {
            System.out.println("移动失败: " + result.getMessage());
        }
    }
    
    /**
     * 审核主题
     */
    private void reviewTopic(Forum forum, User currentUser) {
        System.out.println("\n=== 审核主题 ===");
        
        // 获取待审核主题
        List<Topic> pendingTopics = moderatorService.getPendingTopics(forum.getForumId(), currentUser.getUserId());

        UserService userService = new UserServiceImpl();
        
        if (pendingTopics.isEmpty()) {
            System.out.println("没有待审核的主题！");
            return;
        }
        
        System.out.println("待审核主题列表:");
        System.out.printf("%-5s %-30s %-15s\n", "ID", "标题", "作者");
        System.out.println("-------------------------------------------------------");
        
        for (Topic topic : pendingTopics) {
            System.out.printf("%-5d %-30s %-15s\n",
                    userService.getUserName(topic.getUserId()),
                    topic.getTitle().length() > 25 ? topic.getTitle().substring(0, 25) + "..." : topic.getTitle(),
                    userService.getUserName(topic.getUserId()));
        }
        
        System.out.print("请输入要审核的主题ID: ");
        int topicId = getIntInput();
        
        if (topicId <= 0) {
            System.out.println("无效的主题ID！");
            return;
        }
        
        System.out.println("1. 通过审核");
        System.out.println("2. 拒绝审核");
        System.out.print("请选择: ");
        int choice = getIntInput();
        
        boolean approved = choice == 1;
        
        System.out.print("请输入审核意见: ");
        String reason = scanner.nextLine();
        
        ModeratorService.ModeratorResult result = moderatorService.reviewTopic(topicId, currentUser.getUserId(), approved, reason);
        if (result.isSuccess()) {
            System.out.println("审核完成！");
        } else {
            System.out.println("审核失败: " + result.getMessage());
        }
    }
    
    /**
     * 板块设置
     */
    private void forumSettings(Forum forum, User currentUser) {
        System.out.println("\n=== 板块设置 ===");
        System.out.println("当前板块信息:");
        System.out.println("  名称: " + forum.getForumName());
        System.out.println("  描述: " + forum.getDescription());
        
        System.out.println("\n操作选项:");
        System.out.println("1. 修改板块信息");
        System.out.println("2. 板块统计");
        System.out.println("3. 板块封禁管理");
        System.out.println("0. 返回");
        System.out.print("请选择操作: ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1:
                updateForumInfo(forum, currentUser);
                break;
            case 2:
                showForumStatistics(forum, currentUser);
                break;
            case 3:
                manageForumBans(forum, currentUser);
                break;
            case 0:
                return;
            default:
                System.out.println("无效选择！");
        }
    }
    
    /**
     * 修改板块信息
     */
    private void updateForumInfo(Forum forum, User currentUser) {
        System.out.println("\n=== 修改板块信息 ===");
        System.out.println("当前名称: " + forum.getForumName());
        System.out.print("请输入新名称(留空不修改): ");
        String newName = scanner.nextLine();
        
        System.out.println("当前描述: " + forum.getDescription());
        System.out.print("请输入新描述(留空不修改): ");
        String newDescription = scanner.nextLine();
        
        // 创建更新的板块对象
        Forum updatedForum = new Forum();
        updatedForum.setForumId(forum.getForumId());
        updatedForum.setForumName(newName.trim().isEmpty() ? forum.getForumName() : newName);
        updatedForum.setDescription(newDescription.trim().isEmpty() ? forum.getDescription() : newDescription);
        updatedForum.setModeratorId(forum.getModeratorId());
        updatedForum.setCreateTime(forum.getCreateTime());
        updatedForum.setTopicCount(forum.getTopicCount());
        updatedForum.setPostCount(forum.getPostCount());
        updatedForum.setStatus(forum.getStatus());
        
        ModeratorService.ModeratorResult result = moderatorService.updateForum(updatedForum, currentUser.getUserId());
        if (result.isSuccess()) {
            System.out.println("板块信息更新成功！");
            // 更新本地forum对象
            forum.setForumName(updatedForum.getForumName());
            forum.setDescription(updatedForum.getDescription());
        } else {
            System.out.println("更新失败: " + result.getMessage());
        }
    }
    
    /**
     * 显示板块统计
     */
    private void showForumStatistics(Forum forum, User currentUser) {
        System.out.println("\n=== 板块统计信息 ===");
        
        ModeratorService.ForumStatistics stats = moderatorService.getForumStatistics(forum.getForumId(), currentUser.getUserId());
        
        System.out.println("基础统计:");
        System.out.println("  总主题数: " + stats.getTotalTopics());
        System.out.println("  总回复数: " + stats.getTotalReplies());
        System.out.println("  总用户数: " + stats.getTotalUsers());
        
        System.out.println("\n今日统计:");
        System.out.println("  今日主题: " + stats.getTodayTopics());
        System.out.println("  今日回复: " + stats.getTodayReplies());
        
        System.out.println("\n管理统计:");
        System.out.println("  待审核主题: " + stats.getPendingTopics());
        System.out.println("  封禁用户: " + stats.getBannedUsers());
        
        System.out.println("\n按任意键返回...");
        scanner.nextLine();
    }
    
    /**
     * 管理板块封禁
     */
    private void manageForumBans(Forum forum, User currentUser) {
        while (true) {
            System.out.println("\n=== 板块封禁管理 ===");
            System.out.println("1. 查看封禁记录");
            System.out.println("2. 封禁用户");
            System.out.println("3. 解封用户");
            System.out.println("0. 返回");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    viewForumBanRecords(forum, currentUser);
                    break;
                case 2:
                    banUserFromForum(forum, currentUser);
                    break;
                case 3:
                    unbanUserFromForum(forum, currentUser);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选择！");
            }
        }
    }
    
    /**
     * 查看板块封禁记录
     */
    private void viewForumBanRecords(Forum forum, User currentUser) {
        System.out.println("\n=== 板块封禁记录 ===");
        
        List<BanRecord> banRecords = moderatorService.getForumBanRecords(forum.getForumId(), currentUser.getUserId());
        
        if (banRecords.isEmpty()) {
            System.out.println("暂无封禁记录！");
            return;
        }
        
        System.out.printf("%-10s %-15s %-20s %-20s %-10s\n", 
                "用户ID", "封禁原因", "开始时间", "结束时间", "状态");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (BanRecord record : banRecords) {
            String endTime = record.isPermanent() ? "永久" : 
                           (record.getBanEnd() != null ? record.getBanEnd().toString() : "未知");
            
            System.out.printf("%-10d %-15s %-20s %-20s %-10s\n",
                    record.getUserId(),
                    record.getReason().length() > 12 ? record.getReason().substring(0, 12) + "..." : record.getReason(),
                    record.getBanStart() != null ? record.getBanStart().toString() : "未知",
                    endTime,
                    record.getStatus() == BanRecord.BanStatus.ACTIVE ? "生效中" : "已解除");
        }
    }
    
    /**
     * 在板块内封禁用户
     */
    private void banUserFromForum(Forum forum, User currentUser) {
        System.out.println("\n=== 封禁用户 ===");
        System.out.print("请输入要封禁的用户ID: ");
        int userId = getIntInput();
        
        if (userId <= 0) {
            System.out.println("无效的用户ID！");
            return;
        }
        
        System.out.print("请输入封禁原因: ");
        String reason = scanner.nextLine();
        
        System.out.print("请输入封禁天数(1-365): ");
        int duration = getIntInput();
        
        if (duration <= 0 || duration > 365) {
            System.out.println("封禁天数必须在1-365天之间！");
            return;
        }
        
        ModeratorService.ModeratorResult result = moderatorService.banUserFromForum(
                userId, forum.getForumId(), currentUser.getUserId(), reason, duration);
        
        if (result.isSuccess()) {
            System.out.println("用户封禁成功！");
        } else {
            System.out.println("封禁失败: " + result.getMessage());
        }
    }
    
    /**
     * 在板块内解封用户
     */
    private void unbanUserFromForum(Forum forum, User currentUser) {
        System.out.println("\n=== 解封用户 ===");
        System.out.print("请输入要解封的用户ID: ");
        int userId = getIntInput();
        
        if (userId <= 0) {
            System.out.println("无效的用户ID！");
            return;
        }
        
        ModeratorService.ModeratorResult result = moderatorService.unbanUserFromForum(
                userId, forum.getForumId(), currentUser.getUserId());
        
        if (result.isSuccess()) {
            System.out.println("用户解封成功！");
        } else {
            System.out.println("解封失败: " + result.getMessage());
        }
    }
    
    /**
     * 版主统计信息
     */
    private void viewModeratorStatistics(User currentUser) {
        System.out.println("\n=== 版主统计信息 ===");
        
        List<Forum> myForums = moderatorService.getForumsByModerator(currentUser.getUserId());
        
        System.out.println("管理统计:");
        System.out.println("  管理板块数: " + myForums.size());
        
        if (!myForums.isEmpty()) {
            int totalTopics = myForums.stream()
                    .mapToInt(Forum::getTopicCount)
                    .sum();
            
            int totalPosts = myForums.stream()
                    .mapToInt(Forum::getPostCount)
                    .sum();
            
            System.out.println("  管理主题数: " + totalTopics);
            System.out.println("  管理帖子数: " + totalPosts);
        }
        
        System.out.println("\n个人统计:");
        System.out.println("  个人发帖: " + currentUser.getPostCount());
        System.out.println("  声誉积分: " + currentUser.getReputation());
        
        System.out.println("\n按任意键返回...");
        scanner.nextLine();
    }
    
    // ==================== 占位方法实现 ====================
    
    private void moderateForumContent(User currentUser) {
        while (true) {
            System.out.println("\n=== 板块内容审核 ===");
            System.out.println("1. 查看待审核主题");
            System.out.println("2. 批量审核主题");
            System.out.println("3. 删除回复");
            System.out.println("0. 返回");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    viewAllPendingTopics(currentUser);
                    break;
                case 2:
                    System.out.println("批量审核功能待完善...");
                    break;
                case 3:
                    deleteReply(currentUser);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选择！");
            }
        }
    }
    
    private void viewAllPendingTopics(User currentUser) {
        System.out.println("\n=== 所有待审核主题 ===");
        
        List<Forum> myForums = moderatorService.getForumsByModerator(currentUser.getUserId());

        UserService userService = new UserServiceImpl();
        boolean hasPendingTopics = false;
        
        for (Forum forum : myForums) {
            List<Topic> pendingTopics = moderatorService.getPendingTopics(forum.getForumId(), currentUser.getUserId());
            if (!pendingTopics.isEmpty()) {
                hasPendingTopics = true;
                System.out.println("\n板块: " + forum.getForumName());
                System.out.printf("%-5s %-30s %-15s\n", "ID", "标题", "作者");
                System.out.println("-------------------------------------------------------");
                
                for (Topic topic : pendingTopics) {
                    System.out.printf("%-5d %-30s %-15s\n",
                            topic.getTopicId(),
                            topic.getTitle().length() > 25 ? topic.getTitle().substring(0, 25) + "..." : topic.getTitle(),
                            userService.getUserName(topic.getUserId()));
                }
            }
        }
        
        if (!hasPendingTopics) {
            System.out.println("没有待审核的主题！");
        }
        
        System.out.println("\n按任意键返回...");
        scanner.nextLine();
    }
    
    private void deleteReply(User currentUser) {
        System.out.println("\n=== 删除回复 ===");
        System.out.print("请输入要删除的回复ID: ");
        int replyId = getIntInput();
        
        if (replyId <= 0) {
            System.out.println("无效的回复ID！");
            return;
        }
        
        System.out.print("请输入删除原因: ");
        String reason = scanner.nextLine();
        
        System.out.print("确认删除回复？(y/n): ");
        String confirm = scanner.nextLine();
        if ("y".equalsIgnoreCase(confirm)) {
            ModeratorService.ModeratorResult result = moderatorService.deleteReply(replyId, currentUser.getUserId(), reason);
            if (result.isSuccess()) {
                System.out.println("回复删除成功！");
            } else {
                System.out.println("删除失败: " + result.getMessage());
            }
        }
    }
    
    private void manageTopics(User currentUser) {
        System.out.println("\n=== 主题管理 ===");
        System.out.println("1. 批量置顶主题");
        System.out.println("2. 批量锁定主题");
        System.out.println("3. 批量移动主题");
        System.out.println("0. 返回");
        System.out.print("请选择操作: ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1:
            case 2:
            case 3:
                System.out.println("批量主题管理功能待完善...");
                break;
            case 0:
                return;
            default:
                System.out.println("无效选择！");
        }
    }
    
    private void moderateUsers(User currentUser) {
        System.out.println("\n=== 用户管理 ===");
        System.out.println("1. 查看板块用户列表");
        System.out.println("2. 批量封禁管理");
        System.out.println("0. 返回");
        System.out.print("请选择操作: ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1:
            case 2:
                System.out.println("用户管理功能待完善...");
                break;
            case 0:
                return;
            default:
                System.out.println("无效选择！");
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private int getIntInput() {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
