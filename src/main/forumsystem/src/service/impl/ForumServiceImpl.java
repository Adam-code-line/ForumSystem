package main.forumsystem.src.service.impl;

import main.forumsystem.src.service.ForumService;
import main.forumsystem.src.dao.UserDao;
import main.forumsystem.src.dao.ForumDao;
import main.forumsystem.src.dao.TopicDao;
import main.forumsystem.src.dao.ReplyDao;
import main.forumsystem.src.dao.impl.UserDaoImpl;
import main.forumsystem.src.dao.impl.ForumDaoImpl;
import main.forumsystem.src.dao.impl.TopicDaoImpl;
import main.forumsystem.src.dao.impl.ReplyDaoImpl;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.Topic;
import main.forumsystem.src.entity.Reply;
import main.forumsystem.src.factory.UserFactory;
import main.forumsystem.src.factory.UserOperationFactory;
import main.forumsystem.src.factory.impl.UserFactoryImpl;
import main.forumsystem.src.service.UserBlockService;
import main.forumsystem.src.service.impl.UserBlockServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * 板块服务实现类
 * 使用工厂模式创建对象，专注于业务逻辑和数据库操作
 */
public class ForumServiceImpl implements ForumService {
    
    private final UserDao userDao;
    private final ForumDao forumDao;
    private final TopicDao topicDao;
    private final ReplyDao replyDao;
    private final UserFactory userFactory;
    private final UserBlockService userBlockService;
    
    public ForumServiceImpl() {
        this.userDao = new UserDaoImpl();
        this.forumDao = new ForumDaoImpl();
        this.topicDao = new TopicDaoImpl();
        this.replyDao = new ReplyDaoImpl();
        this.userFactory = new UserFactoryImpl();
        this.userBlockService = new UserBlockServiceImpl();
    }
    
    // ==================== 板块管理 ====================
    
    @Override
    public ForumResult createForum(int userId, String forumName, String description) {
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new ForumResult(false, "用户不存在");
            }
            
            // 获取用户操作工厂
            UserOperationFactory operationFactory = userFactory.getOperationFactory(user);
            
            if (!operationFactory.canCreateForum(user)) {
                return new ForumResult(false, "您没有创建板块的权限");
            }
            
            // 使用工厂创建板块对象
            Forum forum = operationFactory.createForum(forumName, description, user);
            
            // 保存板块到数据库
            boolean success = forumDao.addForum(forum);
            if (success) {
                // 如果是普通用户创建板块，提升为版主
                if (user.getRole() == User.UserRole.USER) {
                    userDao.changeUserRole(userId, User.UserRole.MODERATOR);
                }
                return new ForumResult(true, "板块创建成功");
            } else {
                return new ForumResult(false, "创建失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ForumResult(false, "系统错误，创建失败");
        }
    }
    
    @Override
    public Forum getForumById(int forumId) {
        if (forumId <= 0) {
            return null;
        }
        
        try {
            return forumDao.getForumById(forumId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<Forum> getAllForums() {
        try {
            return forumDao.getAllForums();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public ForumResult updateForum(int forumId, String forumName, String description) {
        if (forumId <= 0) {
            return new ForumResult(false, "板块ID无效");
        }
        
        try {
            Forum forum = forumDao.getForumById(forumId);
            if (forum == null) {
                return new ForumResult(false, "板块不存在");
            }
            
            forum.setForumName(forumName);
            forum.setDescription(description);
            
            boolean success = forumDao.updateForum(forum);
            if (success) {
                return new ForumResult(true, "板块更新成功");
            } else {
                return new ForumResult(false, "更新失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ForumResult(false, "系统错误，更新失败");
        }
    }
    
    // ==================== 主题管理 ====================
    
    @Override
    public ForumResult createTopic(int userId, String title, String content, int forumId) {
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new ForumResult(false, "用户不存在");
            }
            
            // 检查是否被拉黑
            if (!userBlockService.canUserPostInForum(userId, forumId)) {
                return new ForumResult(false, "您被该板块版主拉黑，无法发表主题");
            }
            
            // 获取用户操作工厂
            UserOperationFactory operationFactory = userFactory.getOperationFactory(user);
            
            if (!operationFactory.canCreateTopic(user, forumId)) {
                return new ForumResult(false, "您没有在该板块发帖的权限");
            }
            
            // 使用工厂创建主题对象
            Topic topic = operationFactory.createTopic(title, content, forumId, user);
            
            // 保存主题到数据库
            boolean success = topicDao.addTopic(topic);
            if (success) {
                // 更新统计数据
                userDao.updatePostCount(userId, 1);
                forumDao.updateTopicCount(forumId, 1);
                return new ForumResult(true, "主题发布成功");
            } else {
                return new ForumResult(false, "发布失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ForumResult(false, "系统错误，发布失败");
        }
    }
    
    @Override
    public Topic getTopicById(int topicId) {
        if (topicId <= 0) {
            return null;
        }
        
        try {
            return topicDao.getTopicById(topicId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<Topic> getTopicsByForum(int forumId, int page, int size) {
        if (forumId <= 0 || page <= 0 || size <= 0) {
            return new ArrayList<>();
        }
        
        try {
            // 修复：使用实际存在的方法，添加排序参数
            return topicDao.getTopicsByPage(forumId, page, size, "last_reply_time", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public ForumResult updateTopic(int topicId, String title, String content) {
        if (topicId <= 0) {
            return new ForumResult(false, "主题ID无效");
        }
        
        try {
            Topic topic = topicDao.getTopicById(topicId);
            if (topic == null) {
                return new ForumResult(false, "主题不存在");
            }
            
            topic.setTitle(title);
            topic.setContent(content);
            
            boolean success = topicDao.updateTopic(topic);
            if (success) {
                return new ForumResult(true, "主题更新成功");
            } else {
                return new ForumResult(false, "更新失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ForumResult(false, "系统错误，更新失败");
        }
    }
    
    @Override
    public boolean pinTopic(int topicId, boolean isPinned) {
        if (topicId <= 0) {
            return false;
        }
        
        try {
            return topicDao.pinTopic(topicId, isPinned);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean lockTopic(int topicId, boolean isLocked) {
        if (topicId <= 0) {
            return false;
        }
        
        try {
            return topicDao.lockTopic(topicId, isLocked);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean increaseTopicViewCount(int topicId) {
        if (topicId <= 0) {
            return false;
        }
        
        try {
            // 修复：使用实际存在的方法名
            return topicDao.incrementViewCount(topicId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== 回复管理 ====================
    
    @Override
    public ForumResult createReply(int userId, String content, int topicId) {
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new ForumResult(false, "用户不存在");
            }
            
            // 检查是否被拉黑
            if (!userBlockService.canUserReplyToTopic(userId, topicId)) {
                return new ForumResult(false, "您被主题作者或板块版主拉黑，无法回复");
            }
            
            // 获取用户操作工厂
            UserOperationFactory operationFactory = userFactory.getOperationFactory(user);
            
            if (!operationFactory.canReply(user, topicId)) {
                return new ForumResult(false, "您没有回复该主题的权限");
            }
            
            // 使用工厂创建回复对象
            Reply reply = operationFactory.createReply(content, topicId, user);
            
            // 保存回复到数据库
            boolean success = replyDao.addReply(reply);
            if (success) {
                // 更新统计数据
                userDao.updatePostCount(userId, 1);
                topicDao.updateReplyCount(topicId, 1);
                topicDao.updateLastReplyInfo(topicId, userId);
                
                // 更新板块统计
                Topic topic = topicDao.getTopicById(topicId);
                if (topic != null) {
                    forumDao.updatePostCount(topic.getForumId(), 1);
                }
                
                return new ForumResult(true, "回复发表成功");
            } else {
                return new ForumResult(false, "回复失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ForumResult(false, "系统错误，回复失败");
        }
    }
    
    @Override
    public Reply getReplyById(int replyId) {
        if (replyId <= 0) {
            return null;
        }
        
        try {
            return replyDao.getReplyById(replyId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<Reply> getRepliesByTopic(int topicId, int page, int size) {
        if (topicId <= 0 || page <= 0 || size <= 0) {
            return new ArrayList<>();
        }
        
        try {
            // 修复：使用DAO接口中实际存在的方法
            return replyDao.getRepliesByTopicId(topicId, page, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public ForumResult updateReply(int replyId, String content) {
        if (replyId <= 0) {
            return new ForumResult(false, "回复ID无效");
        }
        
        try {
            Reply reply = replyDao.getReplyById(replyId);
            if (reply == null) {
                return new ForumResult(false, "回复不存在");
            }
            
            reply.setContent(content);
            
            boolean success = replyDao.updateReply(reply);
            if (success) {
                return new ForumResult(true, "回复更新成功");
            } else {
                return new ForumResult(false, "更新失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ForumResult(false, "系统错误，更新失败");
        }
    }
    
    // ==================== 统计信息 ====================
    
    @Override
    public ForumStatistics getForumStatistics(int forumId) {
        if (forumId <= 0) {
            return new ForumStatistics(0, 0, 0, 0);
        }
        
        try {
            // 使用现有的DAO方法
            int topicCount = topicDao.getTopicCount(forumId);
            int postCount = replyDao.getReplyCount(forumId); // 修复：使用实际存在的方法
            int todayTopicCount = topicDao.getTodayTopicCount(forumId);
            int todayPostCount = replyDao.getTodayReplyCount(forumId); // 修复：使用实际存在的方法
            
            return new ForumStatistics(topicCount, postCount, todayTopicCount, todayPostCount);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ForumStatistics(0, 0, 0, 0);
        }
    }
    
    @Override
    public UserPostStatistics getUserPostStatistics(int userId) {
        if (userId <= 0) {
            return new UserPostStatistics(0, 0, 0, 0);
        }
        
        try {
            // 使用现有的DAO方法
            int totalTopics = topicDao.getUserTopicCount(userId);
            int totalReplies = replyDao.getUserReplyCount(userId);
            
            // 由于DAO中没有专门的按用户统计今日数据的方法，我们返回0
            // 或者可以通过其他方式实现，比如获取用户今日发布的所有内容后计算
            int todayTopics = 0; 
            int todayReplies = 0; 
            
            return new UserPostStatistics(totalTopics, totalReplies, todayTopics, todayReplies);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new UserPostStatistics(0, 0, 0, 0);
        }
    }

    // ==================== 搜索功能 ====================
    
    @Override
    public List<Topic> searchTopics(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty() || page <= 0 || size <= 0) {
            return new ArrayList<>();
        }
        
        try {
            // 修复：使用实际存在的searchTopics方法，传入正确的参数
            List<Topic> allTopics = topicDao.searchTopics(keyword.trim(), 0); // 0表示全站搜索
            
            // 手动分页
            int startIndex = (page - 1) * size;
            int endIndex = Math.min(startIndex + size, allTopics.size());
            
            if (startIndex >= allTopics.size()) {
                return new ArrayList<>();
            }
            
            return allTopics.subList(startIndex, endIndex);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Forum> searchForums(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            // 修复：使用现有的getAllForums方法，然后手动过滤
            List<Forum> allForums = forumDao.getAllForums();
            List<Forum> result = new ArrayList<>();
            
            String searchKeyword = keyword.trim().toLowerCase();
            for (Forum forum : allForums) {
                if (forum.getForumName().toLowerCase().contains(searchKeyword) || 
                    forum.getDescription().toLowerCase().contains(searchKeyword)) {
                    result.add(forum);
                }
            }
            
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ==================== 权限检查 ====================
    
    @Override
    public boolean canUserPerformAction(int userId, String action, int targetId) {
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return false;
            }
            
            UserOperationFactory operationFactory = userFactory.getOperationFactory(user);
            
            switch (action.toLowerCase()) {
                case "create_forum":
                    return operationFactory.canCreateForum(user);
                case "create_topic":
                    return operationFactory.canCreateTopic(user, targetId);
                case "reply":
                    return operationFactory.canReply(user, targetId);
                case "manage_forum":
                    return operationFactory.canManageForum(user, targetId);
                case "manage_topic":
                    return operationFactory.canManageTopic(user, targetId);
                default:
                    return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
