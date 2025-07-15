package main.forumsystem.src.service.impl;

import main.forumsystem.src.dao.UserDao;
import main.forumsystem.src.dao.ForumDao;
import main.forumsystem.src.dao.TopicDao;
import main.forumsystem.src.dao.ReplyDao;
import main.forumsystem.src.dao.BanRecordDao;
import main.forumsystem.src.dao.SensitiveWordDao;
import main.forumsystem.src.dao.impl.UserDaoImpl;
import main.forumsystem.src.dao.impl.ForumDaoImpl;
import main.forumsystem.src.dao.impl.TopicDaoImpl;
import main.forumsystem.src.dao.impl.ReplyDaoImpl;
import main.forumsystem.src.dao.impl.BanRecordDaoImpl;
import main.forumsystem.src.dao.impl.SensitiveWordDaoImpl;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.Topic;
import main.forumsystem.src.entity.Reply;
import main.forumsystem.src.entity.BanRecord;
import main.forumsystem.src.entity.SensitiveWord;
import main.forumsystem.src.service.AdminService;
import main.forumsystem.src.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 管理员服务实现类
 * 专注于系统级管理功能的业务逻辑
 */
public class AdminServiceImpl implements AdminService {
    
    private final UserDao userDao;
    private final ForumDao forumDao;
    private final TopicDao topicDao;
    private final ReplyDao replyDao;
    private final BanRecordDao banRecordDao;
    private final SensitiveWordDao sensitiveWordDao;
    
    public AdminServiceImpl() {
        this.userDao = new UserDaoImpl();
        this.forumDao = new ForumDaoImpl();
        this.topicDao = new TopicDaoImpl();
        this.replyDao = new ReplyDaoImpl();
        this.banRecordDao = new BanRecordDaoImpl();
        this.sensitiveWordDao = new SensitiveWordDaoImpl();
    }
    
    // ==================== 系统统计信息 ====================
    
    @Override
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 用户统计
            stats.put("totalUsers", userDao.getUserCount());
            stats.put("activeUsers", userDao.getActiveUserCount());
            stats.put("todayNewUsers", userDao.getTodayRegisterCount());
            
            // 板块统计
            stats.put("totalForums", forumDao.getForumCount());
            stats.put("activeForums", forumDao.getActiveForumCount());
            
            // 内容统计
            stats.put("totalTopics", topicDao.getTopicCount(0));
            stats.put("todayTopics", topicDao.getTodayTopicCount(0));
            
            // 封禁统计
            List<BanRecord> activeBans = banRecordDao.getActiveBanRecords();
            stats.put("activeBans", activeBans.size());
            
            // 敏感词统计
            List<SensitiveWord> sensitiveWords = sensitiveWordDao.getAllSensitiveWords();
            stats.put("totalSensitiveWords", sensitiveWords.size());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            stats.put("totalUsers", userDao.getUserCount());
            stats.put("activeUsers", userDao.getActiveUserCount());
            stats.put("todayNewUsers", userDao.getTodayRegisterCount());
            
            // 按角色统计
            List<User> admins = userDao.getUsersByRole(User.UserRole.ADMIN);
            List<User> moderators = userDao.getUsersByRole(User.UserRole.MODERATOR);
            List<User> users = userDao.getUsersByRole(User.UserRole.USER);
            
            stats.put("admins", admins.size());
            stats.put("moderators", moderators.size());
            stats.put("users", users.size());
            
            // 按状态统计
            List<User> activeUsers = userDao.getUsersByStatus(User.UserStatus.ACTIVE);
            List<User> bannedUsers = userDao.getUsersByStatus(User.UserStatus.BANNED);
            
            stats.put("activeUsersByStatus", activeUsers.size());
            stats.put("bannedUsers", bannedUsers.size());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getForumStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            stats.put("totalForums", forumDao.getForumCount());
            stats.put("activeForums", forumDao.getActiveForumCount());
            
            // 按状态统计
            List<Forum> activeForums = forumDao.getForumsByStatus(Forum.ForumStatus.ACTIVE);
            stats.put("activeForumsByStatus", activeForums.size());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getContentStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            stats.put("totalTopics", topicDao.getTopicCount(0));
            stats.put("todayTopics", topicDao.getTodayTopicCount(0));
            
            // 按状态统计
            List<Topic> normalTopics = topicDao.getTopicsByStatus(0, Topic.TopicStatus.NORMAL);
            List<Topic> hiddenTopics = topicDao.getTopicsByStatus(0, Topic.TopicStatus.HIDDEN);
            List<Topic> deletedTopics = topicDao.getTopicsByStatus(0, Topic.TopicStatus.DELETED);
            
            stats.put("normalTopics", normalTopics.size());
            stats.put("hiddenTopics", hiddenTopics.size());
            stats.put("deletedTopics", deletedTopics.size());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    // ==================== 内容管理 ====================
    
    @Override
    public AdminResult deleteForum(int forumId) {
        if (forumId <= 0) {
            return new AdminResult(false, "板块ID无效");
        }
        
        try {
            Forum forum = forumDao.getForumById(forumId);
            if (forum == null) {
                return new AdminResult(false, "板块不存在");
            }
            
            // 检查板块是否有内容
            if (forum.getTopicCount() > 0) {
                return new AdminResult(false, "板块内有主题，无法删除");
            }
            
            boolean success = forumDao.deleteForum(forumId);
            if (success) {
                return new AdminResult(true, "板块删除成功");
            } else {
                return new AdminResult(false, "删除失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，删除失败");
        }
    }
    
    @Override
    public AdminResult deleteTopic(int topicId) {
        if (topicId <= 0) {
            return new AdminResult(false, "主题ID无效");
        }
        
        try {
            Topic topic = topicDao.getTopicById(topicId);
            if (topic == null) {
                return new AdminResult(false, "主题不存在");
            }
            
            boolean success = topicDao.deleteTopic(topicId);
            if (success) {
                // 更新板块主题数量
                forumDao.updateTopicCount(topic.getForumId(), -1);
                return new AdminResult(true, "主题删除成功");
            } else {
                return new AdminResult(false, "删除失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，删除失败");
        }
    }
    
    @Override
    public AdminResult deleteReply(int replyId) {
        if (replyId <= 0) {
            return new AdminResult(false, "回复ID无效");
        }
        
        try {
            Reply reply = replyDao.getReplyById(replyId);
            if (reply == null) {
                return new AdminResult(false, "回复不存在");
            }
            
            boolean success = replyDao.deleteReply(replyId);
            if (success) {
                // 更新主题回复数量
                topicDao.updateReplyCount(reply.getTopicId(), -1);
                
                // 更新板块帖子数量
                Topic topic = topicDao.getTopicById(reply.getTopicId());
                if (topic != null) {
                    forumDao.updatePostCount(topic.getForumId(), -1);
                }
                
                return new AdminResult(true, "回复删除成功");
            } else {
                return new AdminResult(false, "删除失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，删除失败");
        }
    }
    
    @Override
    public int batchDeleteTopics(int[] topicIds) {
        if (topicIds == null || topicIds.length == 0) {
            return 0;
        }
        
        try {
            return topicDao.batchDeleteTopics(topicIds);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public int batchDeleteReplies(int[] replyIds) {
        if (replyIds == null || replyIds.length == 0) {
            return 0;
        }
        
        try {
            return replyDao.batchDeleteReplies(replyIds);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    // ==================== 敏感词管理 ====================
    
    @Override
    public boolean addSensitiveWord(String word) {
        if (ValidationUtil.isEmpty(word)) {
            return false;
        }
        
        try {
            // 检查敏感词是否已存在
            if (sensitiveWordDao.existsSensitiveWord(word)) {
                return false;
            }
            
            SensitiveWord sensitiveWord = new SensitiveWord();
            sensitiveWord.setWord(word);
            sensitiveWord.setCreateTime(LocalDateTime.now());
            
            return sensitiveWordDao.addSensitiveWord(sensitiveWord);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteSensitiveWord(int wordId) {
        if (wordId <= 0) {
            return false;
        }
        
        try {
            return sensitiveWordDao.deleteSensitiveWord(wordId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<SensitiveWord> getAllSensitiveWords() {
        try {
            return sensitiveWordDao.getAllSensitiveWords();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean containsSensitiveWord(String text) {
        if (ValidationUtil.isEmpty(text)) {
            return false;
        }
        
        try {
            List<SensitiveWord> sensitiveWords = sensitiveWordDao.getAllSensitiveWords();
            for (SensitiveWord word : sensitiveWords) {
                if (text.toLowerCase().contains(word.getWord().toLowerCase())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== 封禁记录管理 ====================
    
    @Override
    public boolean createBanRecord(int userId, int adminId, String reason, long durationHours) {
        if (userId <= 0 || adminId <= 0 || ValidationUtil.isEmpty(reason)) {
            return false;
        }
        
        try {
            BanRecord banRecord = new BanRecord();
            banRecord.setUserId(userId);
            banRecord.setAdminId(adminId);
            banRecord.setReason(reason);
            banRecord.setBanStart(LocalDateTime.now());
            banRecord.setStatus(BanRecord.BanStatus.ACTIVE);
            
            if (durationHours > 0) {
                banRecord.setBanEnd(LocalDateTime.now().plusHours(durationHours));
                banRecord.setPermanent(false);
            } else {
                banRecord.setPermanent(true);
            }
            
            return banRecordDao.addBanRecord(banRecord);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<BanRecord> getUserBanRecords(int userId) {
        if (userId <= 0) {
            return new ArrayList<>();
        }
        
        try {
            return banRecordDao.getBanRecordsByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<BanRecord> getAllBanRecords() {
        try {
            return banRecordDao.getActiveBanRecords();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // ==================== 系统设置 ====================
    
    @Override
    public boolean updateSystemSetting(String key, String value) {
        if (ValidationUtil.isEmpty(key) || value == null) {
            return false;
        }
        
        try {
            // 这里需要实现系统设置的存储逻辑
            // 可以存储在数据库的settings表中
            return true; // 暂时返回true
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public String getSystemSetting(String key) {
        if (ValidationUtil.isEmpty(key)) {
            return null;
        }
        
        try {
            // 这里需要实现系统设置的读取逻辑
            return null; // 暂时返回null
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Map<String, String> getAllSystemSettings() {
        try {
            // 这里需要实现获取所有系统设置的逻辑
            return new HashMap<>(); // 暂时返回空Map
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    // ==================== 数据清理 ====================
    
    @Override
    public AdminResult cleanExpiredData() {
        try {
            int cleanedCount = 0;
            
            // 清理过期的封禁记录
            List<BanRecord> expiredBans = banRecordDao.getExpiredBanRecords();
            for (BanRecord ban : expiredBans) {
                if (banRecordDao.liftUserBan(ban.getUserId(), 0)) {
                    cleanedCount++;
                }
            }
            
            return new AdminResult(true, "清理完成，处理了" + cleanedCount + "条过期数据");
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "清理失败，系统错误");
        }
    }
    
    @Override
    public AdminResult cleanInactiveUsers(int inactiveDays) {
        if (inactiveDays <= 0) {
            return new AdminResult(false, "非活跃天数必须大于0");
        }
        
        try {
            // 修复：使用现有的方法获取非活跃用户
            // 由于UserDao中没有getInactiveUsers方法，我们先获取所有用户然后筛选
            List<User> allUsers = userDao.getAllUsers();
            int cleanedCount = 0;
            
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(inactiveDays);
            
            for (User user : allUsers) {
                // 检查用户最后登录时间是否超过指定天数
                if (user.getRole() == User.UserRole.USER && 
                    user.getLastLogin() != null && 
                    user.getLastLogin().isBefore(cutoffDate)) {
                    
                    if (userDao.deleteUser(user.getUserId())) {
                        cleanedCount++;
                    }
                }
            }
            
            return new AdminResult(true, "清理完成，删除了" + cleanedCount + "个非活跃用户");
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "清理失败，系统错误");
        }
    }
    
    @Override
    public AdminResult cleanEmptyForums() {
        try {
            List<Forum> allForums = forumDao.getAllForums();
            int cleanedCount = 0;
            
            for (Forum forum : allForums) {
                if (forum.getTopicCount() == 0 && forum.getPostCount() == 0) {
                    if (forumDao.deleteForum(forum.getForumId())) {
                        cleanedCount++;
                    }
                }
            }
            
            return new AdminResult(true, "清理完成，删除了" + cleanedCount + "个空板块");
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "清理失败，系统错误");
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
    public List<Topic> getAllTopics() {
        try {
            return topicDao.getAllTopics();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Topic> getTopicsByForum(int forumId) {
        try {
            return topicDao.getTopicsByForum(forumId); 
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Reply> getAllReplies() {
        try {
            return replyDao.getAllReplies();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Reply> getRepliesByTopic(int topicId) {
        try {
            return replyDao.getRepliesByTopicId(topicId); // 使用现有的方法
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Topic getTopicById(int topicId) {
        try {
            return topicDao.getTopicById(topicId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Forum getForumById(int forumId) {
        try {
            return forumDao.getForumById(forumId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Reply getReplyById(int replyId) {
        try {
            return replyDao.getReplyById(replyId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int batchDeleteForums(int[] forumIds) {
        try {
            return forumDao.batchDeleteForums(forumIds);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int processExpiredBans() {
        try {
            return banRecordDao.processExpiredBans();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
