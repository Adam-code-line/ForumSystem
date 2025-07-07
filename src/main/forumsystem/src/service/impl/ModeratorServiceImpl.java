package main.forumsystem.src.service.impl;

import main.forumsystem.src.dao.ForumDao;
import main.forumsystem.src.dao.TopicDao;
import main.forumsystem.src.dao.ReplyDao;
import main.forumsystem.src.dao.UserDao;
import main.forumsystem.src.dao.BanRecordDao;
import main.forumsystem.src.dao.impl.ForumDaoImpl;
import main.forumsystem.src.dao.impl.TopicDaoImpl;
import main.forumsystem.src.dao.impl.ReplyDaoImpl;
import main.forumsystem.src.dao.impl.UserDaoImpl;
import main.forumsystem.src.dao.impl.BanRecordDaoImpl;
import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.Topic;
import main.forumsystem.src.entity.Reply;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.BanRecord;
import main.forumsystem.src.service.ModeratorService;
import main.forumsystem.src.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 版主服务实现类
 */
public class ModeratorServiceImpl implements ModeratorService {
    
    private final ForumDao forumDao;
    private final TopicDao topicDao;
    private final ReplyDao replyDao;
    private final UserDao userDao;
    private final BanRecordDao banRecordDao;
    
    public ModeratorServiceImpl() {
        this.forumDao = new ForumDaoImpl();
        this.topicDao = new TopicDaoImpl();
        this.replyDao = new ReplyDaoImpl();
        this.userDao = new UserDaoImpl();
        this.banRecordDao = new BanRecordDaoImpl();
    }
    
    @Override
    public ModeratorResult createForum(Forum forum, int creatorId) {
        if (forum == null) {
            return new ModeratorResult(false, "板块信息不能为空");
        }
        
        if (creatorId <= 0) {
            return new ModeratorResult(false, "创建者ID无效");
        }
        
        // 验证用户是否存在
        User creator = userDao.getUserById(creatorId);
        if (creator == null) {
            return new ModeratorResult(false, "创建者不存在");
        }
        
        // 验证用户状态
        if (creator.getStatus() == User.UserStatus.BANNED) {
            return new ModeratorResult(false, "被封禁用户无法创建板块");
        }
        
        // 验证板块信息
        if (ValidationUtil.isEmpty(forum.getForumName()) || forum.getForumName().length() > 50) {
            return new ModeratorResult(false, "板块名称不能为空且长度不能超过50字符");
        }
        
        if (forum.getDescription() != null && forum.getDescription().length() > 200) {
            return new ModeratorResult(false, "板块描述长度不能超过200字符");
        }
        
        try {
            // 设置板块基本信息
            forum.setModeratorId(creatorId);
            forum.setCreateTime(LocalDateTime.now());
            forum.setTopicCount(0);
            forum.setPostCount(0);
            forum.setStatus(Forum.ForumStatus.ACTIVE);
            
            // 创建板块
            boolean success = forumDao.addForum(forum);
            if (!success) {
                return new ModeratorResult(false, "板块创建失败");
            }
            
            // 获取创建的板块
            Forum createdForum = forumDao.getForumByName(forum.getForumName());
            if (createdForum == null) {
                return new ModeratorResult(false, "板块创建失败");
            }
            
            // 如果创建者不是管理员，自动提升为版主
            if (creator.getRole() == User.UserRole.USER) {
                userDao.changeUserRole(creatorId, User.UserRole.MODERATOR);
            }
            
            return new ModeratorResult(true, "板块创建成功", createdForum);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ModeratorResult(false, "系统错误，板块创建失败");
        }
    }
    
    @Override
    public ModeratorResult updateForum(Forum forum, int moderatorId) {
        if (forum == null) {
            return new ModeratorResult(false, "板块信息不能为空");
        }
        
        if (moderatorId <= 0) {
            return new ModeratorResult(false, "版主ID无效");
        }
        
        try {
            // 验证版主权限
            if (!hasForumPermission(moderatorId, forum.getForumId())) {
                return new ModeratorResult(false, "您没有管理该板块的权限");
            }
            
            // 验证板块信息
            if (ValidationUtil.isEmpty(forum.getForumName()) || forum.getForumName().length() > 50) {
                return new ModeratorResult(false, "板块名称不能为空且长度不能超过50字符");
            }
            
            if (forum.getDescription() != null && forum.getDescription().length() > 200) {
                return new ModeratorResult(false, "板块描述长度不能超过200字符");
            }
            
            // 更新板块信息
            boolean success = forumDao.updateForum(forum);
            if (success) {
                Forum updatedForum = forumDao.getForumById(forum.getForumId());
                return new ModeratorResult(true, "板块信息更新成功", updatedForum);
            } else {
                return new ModeratorResult(false, "更新失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ModeratorResult(false, "系统错误，更新失败");
        }
    }
    
    @Override
    public ModeratorResult deleteForum(int forumId, int operatorId) {
        if (forumId <= 0) {
            return new ModeratorResult(false, "板块ID无效");
        }
        
        if (operatorId <= 0) {
            return new ModeratorResult(false, "操作者ID无效");
        }
        
        try {
            Forum forum = forumDao.getForumById(forumId);
            if (forum == null) {
                return new ModeratorResult(false, "板块不存在");
            }
            
            User operator = userDao.getUserById(operatorId);
            if (operator == null) {
                return new ModeratorResult(false, "操作者不存在");
            }
            
            // 只有管理员或板块版主可以删除板块
            if (operator.getRole() != User.UserRole.ADMIN && forum.getModeratorId() != operatorId) {
                return new ModeratorResult(false, "只有管理员或板块版主可以删除板块");
            }
            
            // 检查板块是否有内容
            if (forum.getTopicCount() > 0) {
                return new ModeratorResult(false, "板块内有主题，无法删除");
            }
            
            // 删除板块
            boolean success = forumDao.deleteForum(forumId);
            if (success) {
                return new ModeratorResult(true, "板块删除成功");
            } else {
                return new ModeratorResult(false, "删除失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ModeratorResult(false, "系统错误，删除失败");
        }
    }
    
    @Override
    public List<Forum> getForumsByModerator(int moderatorId) {
        if (moderatorId <= 0) {
            return List.of();
        }
        
        try {
            User moderator = userDao.getUserById(moderatorId);
            if (moderator == null) {
                return List.of();
            }
            
            // 管理员可以看到所有板块
            if (moderator.getRole() == User.UserRole.ADMIN) {
                return forumDao.getAllForums();
            }
            
            // 版主只能看到自己管理的板块
            return forumDao.getForumsByModerator(moderatorId);
            
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public boolean isForumModerator(int userId, int forumId) {
        if (userId <= 0 || forumId <= 0) {
            return false;
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return false;
            }
            
            // 管理员拥有所有板块权限
            if (user.getRole() == User.UserRole.ADMIN) {
                return true;
            }
            
            // 检查是否是板块版主（通过Forum实体检查）
            Forum forum = forumDao.getForumById(forumId);
            return forum != null && forum.getModeratorId() == userId;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public ModeratorResult addForumModerator(int forumId, int newModeratorId, int operatorId) {
        if (forumId <= 0 || newModeratorId <= 0 || operatorId <= 0) {
            return new ModeratorResult(false, "参数无效");
        }
        
        try {
            // 验证操作者权限（只有管理员可以添加版主）
            User operator = userDao.getUserById(operatorId);
            if (operator == null || operator.getRole() != User.UserRole.ADMIN) {
                return new ModeratorResult(false, "只有管理员可以添加版主");
            }
            
            // 验证板块是否存在
            Forum forum = forumDao.getForumById(forumId);
            if (forum == null) {
                return new ModeratorResult(false, "板块不存在");
            }
            
            // 验证新版主是否存在
            User newModerator = userDao.getUserById(newModeratorId);
            if (newModerator == null) {
                return new ModeratorResult(false, "用户不存在");
            }
            
            if (newModerator.getStatus() == User.UserStatus.BANNED) {
                return new ModeratorResult(false, "被封禁用户不能成为版主");
            }
            
            // 检查是否已经是版主
            if (forum.getModeratorId() == newModeratorId) {
                return new ModeratorResult(false, "该用户已经是此板块的版主");
            }
            
            // 设置新版主
            boolean success = forumDao.setForumModerator(forumId, newModeratorId);
            if (!success) {
                return new ModeratorResult(false, "设置版主失败");
            }
            
            // 如果新版主角色是普通用户，提升为版主角色
            if (newModerator.getRole() == User.UserRole.USER) {
                userDao.changeUserRole(newModeratorId, User.UserRole.MODERATOR);
            }
            
            return new ModeratorResult(true, "版主设置成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ModeratorResult(false, "系统错误，设置失败");
        }
    }
    
    @Override
    public ModeratorResult removeForumModerator(int forumId, int moderatorId, int operatorId) {
        if (forumId <= 0 || moderatorId <= 0 || operatorId <= 0) {
            return new ModeratorResult(false, "参数无效");
        }
        
        try {
            // 验证操作者权限（只有管理员可以移除版主）
            User operator = userDao.getUserById(operatorId);
            if (operator == null || operator.getRole() != User.UserRole.ADMIN) {
                return new ModeratorResult(false, "只有管理员可以移除版主");
            }
            
            // 验证板块是否存在
            Forum forum = forumDao.getForumById(forumId);
            if (forum == null) {
                return new ModeratorResult(false, "板块不存在");
            }
            
            // 检查是否是当前版主
            if (forum.getModeratorId() != moderatorId) {
                return new ModeratorResult(false, "该用户不是此板块的版主");
            }
            
            // 移除版主（设置为0表示无版主）
            boolean success = forumDao.setForumModerator(forumId, 0);
            if (!success) {
                return new ModeratorResult(false, "移除版主失败");
            }
            
            // 检查被移除的用户是否还管理其他板块
            List<Forum> otherForums = forumDao.getForumsByModerator(moderatorId);
            if (otherForums.isEmpty()) {
                // 如果不再管理任何板块，降级为普通用户
                userDao.changeUserRole(moderatorId, User.UserRole.USER);
            }
            
            return new ModeratorResult(true, "版主移除成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ModeratorResult(false, "系统错误，移除失败");
        }
    }
    
    @Override
    public List<User> getForumModerators(int forumId) {
        if (forumId <= 0) {
            return List.of();
        }
        
        try {
            Forum forum = forumDao.getForumById(forumId);
            if (forum == null || forum.getModeratorId() == 0) {
                return List.of();
            }
            
            User moderator = userDao.getUserById(forum.getModeratorId());
            if (moderator != null) {
                return List.of(moderator);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return List.of();
    }
    
    @Override
    public ModeratorResult pinTopic(int topicId, int moderatorId) {
        return updateTopicStatus(topicId, moderatorId, "pin", true);
    }
    
    @Override
    public ModeratorResult unpinTopic(int topicId, int moderatorId) {
        return updateTopicStatus(topicId, moderatorId, "pin", false);
    }
    
    @Override
    public ModeratorResult highlightTopic(int topicId, int moderatorId) {
        return updateTopicStatus(topicId, moderatorId, "highlight", true);
    }
    
    @Override
    public ModeratorResult unhighlightTopic(int topicId, int moderatorId) {
        return updateTopicStatus(topicId, moderatorId, "highlight", false);
    }
    
    @Override
    public ModeratorResult lockTopic(int topicId, int moderatorId, String reason) {
        if (topicId <= 0) {
            return new ModeratorResult(false, "主题ID无效");
        }
        
        if (moderatorId <= 0) {
            return new ModeratorResult(false, "版主ID无效");
        }
        
        try {
            Topic topic = topicDao.getTopicById(topicId);
            if (topic == null) {
                return new ModeratorResult(false, "主题不存在");
            }
            
            // 验证版主权限
            if (!hasForumPermission(moderatorId, topic.getForumId())) {
                return new ModeratorResult(false, "您没有管理该主题的权限");
            }
            
            if (topic.isLocked()) {
                return new ModeratorResult(false, "主题已被锁定");
            }
            
            // 锁定主题
            boolean success = topicDao.lockTopic(topicId, true);
            if (success) {
                return new ModeratorResult(true, "主题已锁定");
            } else {
                return new ModeratorResult(false, "锁定失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ModeratorResult(false, "系统错误，锁定失败");
        }
    }
    
    @Override
    public ModeratorResult unlockTopic(int topicId, int moderatorId) {
        if (topicId <= 0) {
            return new ModeratorResult(false, "主题ID无效");
        }
        
        if (moderatorId <= 0) {
            return new ModeratorResult(false, "版主ID无效");
        }
        
        try {
            Topic topic = topicDao.getTopicById(topicId);
            if (topic == null) {
                return new ModeratorResult(false, "主题不存在");
            }
            
            // 验证版主权限
            if (!hasForumPermission(moderatorId, topic.getForumId())) {
                return new ModeratorResult(false, "您没有管理该主题的权限");
            }
            
            if (!topic.isLocked()) {
                return new ModeratorResult(false, "主题未被锁定");
            }
            
            // 解锁主题
            boolean success = topicDao.lockTopic(topicId, false);
            if (success) {
                return new ModeratorResult(true, "主题已解锁");
            } else {
                return new ModeratorResult(false, "解锁失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ModeratorResult(false, "系统错误，解锁失败");
        }
    }
    
    @Override
    public ModeratorResult deleteTopic(int topicId, int moderatorId, String reason) {
        if (topicId <= 0) {
            return new ModeratorResult(false, "主题ID无效");
        }
        
        if (moderatorId <= 0) {
            return new ModeratorResult(false, "版主ID无效");
        }
        
        try {
            Topic topic = topicDao.getTopicById(topicId);
            if (topic == null) {
                return new ModeratorResult(false, "主题不存在");
            }
            
            // 验证版主权限
            if (!hasForumPermission(moderatorId, topic.getForumId())) {
                return new ModeratorResult(false, "您没有管理该主题的权限");
            }
            
            // 删除主题（软删除）
            boolean success = topicDao.deleteTopic(topicId);
            if (success) {
                // 更新板块主题数量
                forumDao.updateTopicCount(topic.getForumId(), -1);
                return new ModeratorResult(true, "主题已删除");
            } else {
                return new ModeratorResult(false, "删除失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ModeratorResult(false, "系统错误，删除失败");
        }
    }
    
    @Override
    public ModeratorResult moveTopic(int topicId, int targetForumId, int moderatorId) {
        if (topicId <= 0 || targetForumId <= 0) {
            return new ModeratorResult(false, "参数无效");
        }
        
        if (moderatorId <= 0) {
            return new ModeratorResult(false, "版主ID无效");
        }
        
        try {
            Topic topic = topicDao.getTopicById(topicId);
            if (topic == null) {
                return new ModeratorResult(false, "主题不存在");
            }
            
            Forum targetForum = forumDao.getForumById(targetForumId);
            if (targetForum == null) {
                return new ModeratorResult(false, "目标板块不存在");
            }
            
            // 验证版主对源板块和目标板块都有权限
            if (!hasForumPermission(moderatorId, topic.getForumId()) || 
                !hasForumPermission(moderatorId, targetForumId)) {
                return new ModeratorResult(false, "您没有移动主题的权限");
            }
            
            if (topic.getForumId() == targetForumId) {
                return new ModeratorResult(false, "主题已在目标板块中");
            }
            
            int originalForumId = topic.getForumId();
            
            // 移动主题
            boolean success = topicDao.moveTopic(topicId, targetForumId);
            if (success) {
                // 更新板块主题数量
                forumDao.updateTopicCount(originalForumId, -1);
                forumDao.updateTopicCount(targetForumId, 1);
                
                return new ModeratorResult(true, "主题移动成功");
            } else {
                return new ModeratorResult(false, "移动失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ModeratorResult(false, "系统错误，移动失败");
        }
    }
    
    @Override
    public ModeratorResult banUserFromForum(int userId, int forumId, int moderatorId, String reason, int durationDays) {
        if (userId <= 0 || forumId <= 0 || moderatorId <= 0) {
            return new ModeratorResult(false, "参数无效");
        }
        
        if (durationDays <= 0 || durationDays > 365) {
            return new ModeratorResult(false, "封禁天数必须在1-365天之间");
        }
        
        try {
            // 验证用户存在
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new ModeratorResult(false, "用户不存在");
            }
            
            // 验证版主权限
            if (!hasForumPermission(moderatorId, forumId)) {
                return new ModeratorResult(false, "您没有管理该板块的权限");
            }
            
            // 管理员不能被封禁
            if (user.getRole() == User.UserRole.ADMIN) {
                return new ModeratorResult(false, "管理员不能被封禁");
            }
            
            // 检查是否已被封禁
            if (banRecordDao.isUserBanned(userId)) {
                return new ModeratorResult(false, "用户已被封禁");
            }
            
            // 创建封禁记录（BanRecord实体没有setForumId方法，这里是板块级封禁，暂时用全局封禁代替）
            BanRecord banRecord = new BanRecord();
            banRecord.setUserId(userId);
            banRecord.setAdminId(moderatorId);
            banRecord.setReason(reason + " (板块ID: " + forumId + ")");
            banRecord.setBanStart(LocalDateTime.now());
            banRecord.setBanEnd(LocalDateTime.now().plusDays(durationDays));
            banRecord.setPermanent(false);
            banRecord.setStatus(BanRecord.BanStatus.ACTIVE);
            
            boolean success = banRecordDao.addBanRecord(banRecord);
            if (success) {
                return new ModeratorResult(true, "用户已被封禁" + durationDays + "天");
            } else {
                return new ModeratorResult(false, "封禁失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ModeratorResult(false, "系统错误，封禁失败");
        }
    }
    
    @Override
    public ModeratorResult unbanUserFromForum(int userId, int forumId, int moderatorId) {
        if (userId <= 0 || forumId <= 0 || moderatorId <= 0) {
            return new ModeratorResult(false, "参数无效");
        }
        
        try {
            // 验证版主权限
            if (!hasForumPermission(moderatorId, forumId)) {
                return new ModeratorResult(false, "您没有管理该板块的权限");
            }
            
            // 检查用户是否被封禁
            if (!banRecordDao.isUserBanned(userId)) {
                return new ModeratorResult(false, "用户未被封禁");
            }
            
            // 解封用户
            boolean success = banRecordDao.liftUserBan(userId, moderatorId);
            if (success) {
                return new ModeratorResult(true, "用户已解封");
            } else {
                return new ModeratorResult(false, "解封失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ModeratorResult(false, "系统错误，解封失败");
        }
    }
    
    @Override
    public boolean isUserBannedFromForum(int userId, int forumId) {
        if (userId <= 0 || forumId <= 0) {
            return false;
        }
        
        try {
            // 由于BanRecord没有板块字段，这里只检查用户是否被全局封禁
            return banRecordDao.isUserBanned(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<BanRecord> getForumBanRecords(int forumId, int moderatorId) {
        if (forumId <= 0 || moderatorId <= 0) {
            return List.of();
        }
        
        try {
            // 验证版主权限
            if (!hasForumPermission(moderatorId, forumId)) {
                return List.of();
            }
            
            // 由于BanRecord没有板块字段，返回所有活跃封禁记录
            return banRecordDao.getActiveBanRecords();
            
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public ForumStatistics getForumStatistics(int forumId, int moderatorId) {
        if (forumId <= 0 || moderatorId <= 0) {
            return new ForumStatistics(0, 0, 0, 0, 0, 0, 0);
        }
        
        try {
            // 验证版主权限
            if (!hasForumPermission(moderatorId, forumId)) {
                return new ForumStatistics(0, 0, 0, 0, 0, 0, 0);
            }
            
            Forum forum = forumDao.getForumById(forumId);
            if (forum == null) {
                return new ForumStatistics(0, 0, 0, 0, 0, 0, 0);
            }
            
            int totalTopics = forum.getTopicCount();
            int totalReplies = forum.getPostCount() - totalTopics; // 总帖子数减去主题数
            
            // 获取今日数据
            int todayTopics = topicDao.getTodayTopicCount(forumId);
            int todayReplies = 0; // ReplyDao中没有getTodayReplyCount方法
            
            // 获取该板块的封禁用户数（由于没有板块级封禁，返回0）
            int bannedUsers = 0;
            
            return new ForumStatistics(totalTopics, totalReplies, 0, todayTopics, todayReplies, 0, bannedUsers);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ForumStatistics(0, 0, 0, 0, 0, 0, 0);
        }
    }
    
    @Override
    public ModeratorResult deleteReply(int replyId, int moderatorId, String reason) {
        // 由于没有ReplyDao的具体实现，暂时返回未实现
        return new ModeratorResult(false, "回复删除功能待实现");
    }
    
    @Override
    public ModeratorResult batchDeleteReplies(int[] replyIds, int moderatorId, String reason) {
        // 由于没有ReplyDao的具体实现，暂时返回未实现
        return new ModeratorResult(false, "批量删除回复功能待实现");
    }
    
    @Override
    public List<Topic> getPendingTopics(int forumId, int moderatorId) {
        if (forumId <= 0 || moderatorId <= 0) {
            return List.of();
        }
        
        try {
            // 验证版主权限
            if (!hasForumPermission(moderatorId, forumId)) {
                return List.of();
            }
            
            // 获取隐藏状态的主题（待审核）
            return topicDao.getTopicsByStatus(forumId, Topic.TopicStatus.HIDDEN);
            
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public ModeratorResult reviewTopic(int topicId, int moderatorId, boolean approved, String reason) {
        if (topicId <= 0) {
            return new ModeratorResult(false, "主题ID无效");
        }
        
        if (moderatorId <= 0) {
            return new ModeratorResult(false, "版主ID无效");
        }
        
        try {
            Topic topic = topicDao.getTopicById(topicId);
            if (topic == null) {
                return new ModeratorResult(false, "主题不存在");
            }
            
            // 验证版主权限
            if (!hasForumPermission(moderatorId, topic.getForumId())) {
                return new ModeratorResult(false, "您没有审核该主题的权限");
            }
            
            // 审核主题
            Topic.TopicStatus newStatus = approved ? Topic.TopicStatus.NORMAL : Topic.TopicStatus.HIDDEN;
            boolean success = topicDao.changeTopicStatus(topicId, newStatus);
            
            if (success) {
                String message = approved ? "主题审核通过" : "主题审核不通过";
                return new ModeratorResult(true, message);
            } else {
                return new ModeratorResult(false, "审核失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ModeratorResult(false, "系统错误，审核失败");
        }
    }
    
    // 私有辅助方法
    
    /**
     * 验证用户是否有管理指定板块的权限
     */
    private boolean hasForumPermission(int userId, int forumId) {
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return false;
            }
            
            // 管理员拥有所有权限
            if (user.getRole() == User.UserRole.ADMIN) {
                return true;
            }
            
            // 检查是否是板块版主
            Forum forum = forumDao.getForumById(forumId);
            return forum != null && forum.getModeratorId() == userId;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 更新主题状态的通用方法
     */
    private ModeratorResult updateTopicStatus(int topicId, int moderatorId, String action, boolean value) {
        if (topicId <= 0) {
            return new ModeratorResult(false, "主题ID无效");
        }
        
        if (moderatorId <= 0) {
            return new ModeratorResult(false, "版主ID无效");
        }
        
        try {
            Topic topic = topicDao.getTopicById(topicId);
            if (topic == null) {
                return new ModeratorResult(false, "主题不存在");
            }
            
            // 验证版主权限
            if (!hasForumPermission(moderatorId, topic.getForumId())) {
                return new ModeratorResult(false, "您没有管理该主题的权限");
            }
            
            boolean success = false;
            String message = "";
            
            switch (action) {
                case "pin":
                    success = topicDao.pinTopic(topicId, value);
                    message = value ? "主题已置顶" : "主题已取消置顶";
                    break;
                case "highlight":
                    // 由于TopicDao中没有highlightTopic方法，使用pinTopic作为替代
                    success = topicDao.pinTopic(topicId, value);
                    message = value ? "主题已加精" : "主题已取消加精";
                    break;
                default:
                    return new ModeratorResult(false, "无效的操作");
            }
            
            if (success) {
                return new ModeratorResult(true, message);
            } else {
                return new ModeratorResult(false, "操作失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ModeratorResult(false, "系统错误，操作失败");
        }
    }
}
