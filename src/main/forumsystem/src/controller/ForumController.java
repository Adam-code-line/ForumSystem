package main.forumsystem.src.controller;

import main.forumsystem.src.service.ForumService;
import main.forumsystem.src.service.LoginService;
import main.forumsystem.src.service.impl.ForumServiceImpl;
import main.forumsystem.src.service.impl.LoginServiceImpl;
import main.forumsystem.src.entity.User;

/**
 * 板块控制器
 * 处理板块、主题、回复相关功能
 */
public class ForumController {
    
    private final ForumService forumService;
    private final LoginService loginService;
    
    public ForumController() {
        this.forumService = new ForumServiceImpl();
        this.loginService = new LoginServiceImpl();
    }
    
    /**
     * 创建板块
     */
    public ForumService.ForumResult createForum(int userId, String forumName, String description) {
        // 验证用户是否被封禁
        if (loginService.isUserBanned(userId)) {
            return new ForumService.ForumResult(false, "您已被封禁，无法创建板块");
        }
        
        if (forumName == null || forumName.trim().isEmpty()) {
            return new ForumService.ForumResult(false, "板块名称不能为空");
        }
        
        if (description == null || description.trim().isEmpty()) {
            return new ForumService.ForumResult(false, "板块描述不能为空");
        }
        
        return forumService.createForum(userId, forumName.trim(), description.trim());
    }
    
    /**
     * 创建主题
     */
    public ForumService.ForumResult createTopic(int userId, String title, String content, int forumId) {
        // 验证用户是否被封禁
        if (loginService.isUserBanned(userId)) {
            return new ForumService.ForumResult(false, "您已被封禁，无法发布主题");
        }
        
        if (title == null || title.trim().isEmpty()) {
            return new ForumService.ForumResult(false, "主题标题不能为空");
        }
        
        if (content == null || content.trim().isEmpty()) {
            return new ForumService.ForumResult(false, "主题内容不能为空");
        }
        
        if (forumId <= 0) {
            return new ForumService.ForumResult(false, "板块ID无效");
        }
        
        // 调用服务层方法（包含敏感词过滤）
        ForumService.ForumResult result = forumService.createTopic(userId, title.trim(), content.trim(), forumId);
        
        // 记录敏感词过滤日志
        if (result.isSuccess() && result.getMessage().contains("敏感词")) {
            System.out.println("用户ID: " + userId + " 发布主题时触发敏感词过滤");
        }
        
        return result;
    }
    
    /**
     * 创建回复
     */
    public ForumService.ForumResult createReply(int userId, String content, int topicId) {
        // 验证用户是否被封禁
        if (loginService.isUserBanned(userId)) {
            return new ForumService.ForumResult(false, "您已被封禁，无法发表回复");
        }
        
        if (content == null || content.trim().isEmpty()) {
            return new ForumService.ForumResult(false, "回复内容不能为空");
        }
        
        if (topicId <= 0) {
            return new ForumService.ForumResult(false, "主题ID无效");
        }
        
        // 调用服务层方法（包含敏感词过滤）
        ForumService.ForumResult result = forumService.createReply(userId, content.trim(), topicId);
        
        // 记录敏感词过滤日志
        if (result.isSuccess() && result.getMessage().contains("敏感词")) {
            System.out.println("用户ID: " + userId + " 发表回复时触发敏感词过滤");
        }
        
        return result;
    }
    
    /**
     * 检查用户是否可以执行某个操作
     */
    public boolean canUserPerformAction(int userId, String action, int targetId) {
        // 验证用户是否被封禁
        if (loginService.isUserBanned(userId)) {
            return false;
        }
        
        if (action == null || action.trim().isEmpty()) {
            return false;
        }
        
        return forumService.canUserPerformAction(userId, action.trim(), targetId);
    }
}
