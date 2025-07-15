package main.forumsystem.src.controller.menu;

import main.forumsystem.src.service.ForumService;
import main.forumsystem.src.controller.ForumController;
import main.forumsystem.src.entity.*;
import main.forumsystem.src.service.UserBlockService;
import main.forumsystem.src.service.impl.UserBlockServiceImpl;
import java.util.List;
import java.util.Scanner;
import main.forumsystem.src.service.UserService;
import main.forumsystem.src.service.impl.UserServiceImpl;

/**
 * 论坛功能控制器
 */
public class ForumMenuController {
    
    private final ForumService forumService;
    private final ForumController forumController;
    private final Scanner scanner;
    private final UserBlockService userBlockService;
    
    public ForumMenuController(ForumService forumService, ForumController forumController, Scanner scanner) {
        this.forumService = forumService;
        this.forumController = forumController;
        this.scanner = scanner;
        this.userBlockService = new UserBlockServiceImpl();
    }
    
    /**
     * 查看所有板块
     */
    public void viewAllForums() {
        System.out.println("\n=== 所有板块 ===");
        List<Forum> forums = forumService.getAllForums();
        
        if (forums.isEmpty()) {
            System.out.println("暂无板块，您可以创建第一个板块！");
            return;
        }
        
        for (int i = 0; i < forums.size(); i++) {
            Forum forum = forums.get(i);
            System.out.printf("%d. %s - %s\n", i + 1, forum.getForumName(), forum.getDescription());
            System.out.printf("   版主: %s | 主题数: %d | 帖子数: %d\n",
                    getModeratorName(forum.getModeratorId()),
                    forum.getTopicCount(),
                    forum.getPostCount());
        }
    }
    
    /**
     * 创建板块
     */
    public void createForum(User currentUser) {
        System.out.println("\n=== 创建板块 ===");
        System.out.print("请输入板块名称: ");
        String forumName = scanner.nextLine();
        System.out.print("请输入板块描述: ");
        String description = scanner.nextLine();
        
        ForumService.ForumResult result = forumController.createForum(currentUser.getUserId(), forumName, description);
        if (result.isSuccess()) {
            System.out.println("板块创建成功！" + result.getMessage());
            if (currentUser.getRole() == User.UserRole.USER) {
                System.out.println("恭喜！您已自动升级为版主！");
            }
        } else {
            System.out.println("创建失败: " + result.getMessage());
        }
    }
    
    /**
     * 进入板块
     */
    public void enterForum(User currentUser) {
        viewAllForums();
        System.out.print("\n请选择要进入的板块编号: ");
        int choice = getIntInput();
        
        List<Forum> forums = forumService.getAllForums();
        if (choice > 0 && choice <= forums.size()) {
            Forum selectedForum = forums.get(choice - 1);
            showForumContent(selectedForum, currentUser);
        } else {
            System.out.println("无效的板块编号！");
        }
    }
    
    /**
     * 显示板块内容
     */
    private void showForumContent(Forum forum, User currentUser) {
        while (true) {
            System.out.println("\n=== " + forum.getForumName() + " ===");
            System.out.println(forum.getDescription());
            
            // 显示主题列表
            List<Topic> topics = forumService.getTopicsByForum(forum.getForumId(), 1, 10);
            
            // 过滤被拉黑用户的主题
            List<Topic> filteredTopics = userBlockService.filterBlockedContent(currentUser.getUserId(), topics);
            
            if (filteredTopics.isEmpty()) {
                System.out.println("该板块暂无主题，快来发布第一个主题吧！");
            } else {
                System.out.println("\n主题列表:");
                for (int i = 0; i < filteredTopics.size(); i++) {
                    Topic topic = filteredTopics.get(i);
                    String pinStatus = topic.isPinned() ? "[置顶]" : "";
                    String lockStatus = topic.isLocked() ? "[锁定]" : "";
                    System.out.printf("%d. %s%s%s (作者: %s, 回复: %d, 浏览: %d)\n",
                            i + 1, pinStatus, lockStatus, topic.getTitle(),
                            getUserName(topic.getUserId()),
                            topic.getReplyCount(),
                            topic.getViewCount());
                }
            }
            
            System.out.println("\n操作选项:");
            System.out.println("1. 发布新主题");
            System.out.println("2. 查看主题详情");
            if (canManageForum(currentUser, forum.getForumId())) {
                System.out.println("3. 板块管理");
            }
            System.out.println("0. 返回主菜单");
            System.out.print("请选择操作: ");
            
            int choice = getIntInput();
            switch (choice) {
                case 1:
                    createTopicInForum(forum.getForumId(), currentUser);
                    break;
                case 2:
                    viewTopicDetail(filteredTopics, currentUser);
                    break;
                case 3:
                    if (canManageForum(currentUser, forum.getForumId())) {
                        System.out.println("板块管理功能待完善...");
                    } else {
                        System.out.println("权限不足！");
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效选择！");
            }
        }
    }
    
    /**
     * 创建主题
     */
    public void createTopic(User currentUser) {
        System.out.println("\n=== 发表主题 ===");
        
        // 先选择板块
        System.out.print("请输入板块ID: ");
        int forumId = getIntInput();
        
        if (forumId <= 0) {
            System.out.println("无效的板块ID！");
            return;
        }
        
        // 检查是否被拉黑
        if (!userBlockService.canUserPostInForum(currentUser.getUserId(), forumId)) {
            System.out.println("您被该板块版主拉黑，无法发表主题！");
            return;
        }
        
        System.out.print("请输入主题标题: ");
        String title = scanner.nextLine();
        
        if (title.trim().isEmpty()) {
            System.out.println("标题不能为空！");
            return;
        }
        
        System.out.print("请输入主题内容: ");
        String content = scanner.nextLine();
        
        if (content.trim().isEmpty()) {
            System.out.println("内容不能为空！");
            return;
        }
        
        // 创建主题
        ForumService.ForumResult result = forumService.createTopic(
                currentUser.getUserId(), title.trim(), content.trim(), forumId
        );
        
        if (result.isSuccess()) {
            System.out.println("主题发表成功！");
        } else {
            System.out.println("发表失败: " + result.getMessage());
        }
    }
    
    /**
     * 在指定板块创建主题
     */
    private void createTopicInForum(int forumId, User currentUser) {
        System.out.println("\n=== 发表主题 ===");
        
        // 检查是否被版主拉黑
        if (!userBlockService.canUserPostInForum(currentUser.getUserId(), forumId)) {
            System.out.println("您被该板块版主拉黑，无法发表主题！");
            return;
        }
        
        System.out.print("请输入主题标题: ");
        String title = scanner.nextLine();
        
        if (title.trim().isEmpty()) {
            System.out.println("标题不能为空！");
            return;
        }
        
        System.out.print("请输入主题内容: ");
        String content = scanner.nextLine();
        
        if (content.trim().isEmpty()) {
            System.out.println("内容不能为空！");
            return;
        }
        
        // 创建主题 - 修复：直接使用 forumService 而不是 forumController
        ForumService.ForumResult result = forumService.createTopic(
                currentUser.getUserId(), title.trim(), content.trim(), forumId
        );
        
        if (result.isSuccess()) {
            System.out.println("主题发表成功！");
        } else {
            System.out.println("发表失败: " + result.getMessage());
        }
    }
    
    /**
     * 回复主题
     */
    public void replyToTopic(User currentUser) {
        System.out.println("\n=== 回复主题 ===");
        System.out.print("请输入主题ID: ");
        int topicId = getIntInput();
        
        if (topicId <= 0) {
            System.out.println("无效的主题ID！");
            return;
        }
        
        // 检查是否被拉黑
        if (!userBlockService.canUserReplyToTopic(currentUser.getUserId(), topicId)) {
            System.out.println("您被主题作者或板块版主拉黑，无法回复！");
            return;
        }
        
        System.out.print("请输入回复内容: ");
        String content = scanner.nextLine();
        
        if (content.trim().isEmpty()) {
            System.out.println("回复内容不能为空！");
            return;
        }
        
        // 发表回复 - 修复参数顺序
        ForumService.ForumResult result = forumService.createReply(
                currentUser.getUserId(), content.trim(), topicId
        );
        
        if (result.isSuccess()) {
            System.out.println("回复发表成功！");
        } else {
            System.out.println("回复失败: " + result.getMessage());
        }
    }
    
    /**
     * 查看主题列表（过滤被拉黑用户的内容）
     */
    public void viewTopics(User currentUser, int forumId) {
        System.out.println("\n=== 主题列表 ===");

        UserService userService = new UserServiceImpl();
        
        // 获取主题列表
        List<Topic> topics = forumService.getTopicsByForum(forumId, 1, 20);
        
        // 过滤被拉黑用户的内容
        List<Topic> filteredTopics = userBlockService.filterBlockedContent(currentUser.getUserId(), topics);
        
        if (filteredTopics.isEmpty()) {
            System.out.println("该板块暂无主题！");
            return;
        }
        
        System.out.printf("%-5s %-30s %-15s %-10s %-10s\n", 
                "名称", "标题", "作者", "回复数", "浏览数");
        System.out.println("------------------------------------------------------------------------");
        
        for (Topic topic : filteredTopics) {
            System.out.printf("%-5d %-30s %-15s %-10d %-10d\n",
                    topic.getTopicId(),
                    topic.getTitle().length() > 25 ? topic.getTitle().substring(0, 25) + "..." : topic.getTitle(),
                    userService.getUserName(topic.getUserId()),
                    topic.getReplyCount(),
                    topic.getViewCount());
        }
    }
    
    /**
     * 搜索主题
     */
    public void searchTopics(User currentUser) {
        System.out.println("\n=== 搜索主题 ===");
        System.out.print("请输入搜索关键词: ");
        String keyword = scanner.nextLine();
        
        List<Topic> topics = forumService.searchTopics(keyword, 1, 20);
        if (topics.isEmpty()) {
            System.out.println("没有找到相关主题！");
        } else {
            System.out.println("搜索结果:");
            for (int i = 0; i < topics.size(); i++) {
                Topic topic = topics.get(i);
                System.out.printf("%d. %s (作者: %s, 板块: %s)\n",
                        i + 1, topic.getTitle(),
                        getUserName(topic.getUserId()),
                        getForumName(topic.getForumId()));
            }
            
            System.out.print("请选择要查看的主题编号(0返回): ");
            int choice = getIntInput();
            if (choice > 0 && choice <= topics.size()) {
                showTopicDetail(topics.get(choice - 1), currentUser);
            }
        }
    }
    
    /**
     * 查看我的发帖
     */
    public void viewMyPosts(User currentUser) {
        System.out.println("\n=== 我的发帖 ===");
        
        ForumService.UserPostStatistics stats = forumService.getUserPostStatistics(currentUser.getUserId());
        System.out.printf("总主题数: %d | 总回复数: %d | 今日主题: %d | 今日回复: %d\n",
                stats.getTotalTopics(), stats.getTotalReplies(),
                stats.getTodayTopics(), stats.getTodayReplies());
        
        System.out.println("详细帖子列表功能待完善...");
    }
    
    // ==================== 辅助方法 ====================
    
    private void viewTopicDetail(List<Topic> topics, User currentUser) {
        if (topics.isEmpty()) {
            System.out.println("没有可查看的主题！");
            return;
        }
        
        System.out.print("请选择要查看的主题编号: ");
        int choice = getIntInput();
        
        if (choice > 0 && choice <= topics.size()) {
            Topic selectedTopic = topics.get(choice - 1);
            showTopicDetail(selectedTopic, currentUser);
        } else {
            System.out.println("无效的主题编号！");
        }
    }
    
    private void showTopicDetail(Topic topic, User currentUser) {
        // 增加浏览量
        forumService.increaseTopicViewCount(topic.getTopicId());
        
        System.out.println("\n=== " + topic.getTitle() + " ===");
        System.out.println("作者: " + getUserName(topic.getUserId()));
        System.out.println("发布时间: " + topic.getCreateTime());
        System.out.println("浏览量: " + (topic.getViewCount() + 1));
        System.out.println("\n内容:");
        System.out.println(topic.getContent());
        
        // 显示回复列表
        List<Reply> replies = forumService.getRepliesByTopic(topic.getTopicId(), 1, 10);
        
        // 过滤被拉黑用户的回复
        List<Reply> filteredReplies = userBlockService.filterBlockedContent(currentUser.getUserId(), replies);
        
        if (!filteredReplies.isEmpty()) {
            System.out.println("\n=== 回复列表 ===");
            for (int i = 0; i < filteredReplies.size(); i++) {
                Reply reply = filteredReplies.get(i);
                System.out.printf("%d楼 %s (%s):\n%s\n\n",
                        i + 2, getUserName(reply.getUserId()),
                        reply.getCreateTime(), reply.getContent());
            }
        }
        
        System.out.println("操作选项:");
        if (!topic.isLocked()) {
            System.out.println("1. 发表回复");
        }
        System.out.println("0. 返回");
        System.out.print("请选择操作: ");
        
        int choice = getIntInput();
        if (choice == 1 && !topic.isLocked()) {
            createReply(topic.getTopicId(), currentUser);
        }
    }
    
    private void createReply(int topicId, User currentUser) {
        System.out.println("\n=== 发表回复 ===");
        
        // 检查是否被拉黑
        if (!userBlockService.canUserReplyToTopic(currentUser.getUserId(), topicId)) {
            System.out.println("您被主题作者或板块版主拉黑，无法回复！");
            return;
        }
        
        System.out.print("请输入回复内容: ");
        String content = scanner.nextLine();
        
        // 修复：使用 forumService 而不是 forumController，并修复参数顺序
        ForumService.ForumResult result = forumService.createReply(
                currentUser.getUserId(), content, topicId
        );
        
        if (result.isSuccess()) {
            System.out.println("回复发表成功！");
        } else {
            System.out.println("回复失败: " + result.getMessage());
        }
    }
    
    private String getUserName(int userId) {
        // 这里应该调用userService获取用户信息，简化处理
        UserService userService = new UserServiceImpl();
        return userService.getUserName(userId);
    }
    
    private String getModeratorName(int moderatorId) {
        if (moderatorId <= 0) return "无";
        return "版主" + moderatorId;
    }
    
    private String getForumName(int forumId) {
        Forum forum = forumService.getForumById(forumId);
        return forum != null ? forum.getForumName() : "未知板块";
    }
    
    private boolean canManageForum(User user, int forumId) {
        return user.getRole() == User.UserRole.ADMIN || 
               forumService.canUserPerformAction(user.getUserId(), "manageForum", forumId);
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
