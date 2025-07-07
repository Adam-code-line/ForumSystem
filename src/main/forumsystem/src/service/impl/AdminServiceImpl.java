package main.forumsystem.src.service.impl;

import main.forumsystem.src.dao.UserDao;
import main.forumsystem.src.dao.ForumDao;
import main.forumsystem.src.dao.TopicDao;
import main.forumsystem.src.dao.BanRecordDao;
import main.forumsystem.src.dao.SensitiveWordDao;
import main.forumsystem.src.dao.impl.UserDaoImpl;
import main.forumsystem.src.dao.impl.ForumDaoImpl;
import main.forumsystem.src.dao.impl.TopicDaoImpl;
import main.forumsystem.src.dao.impl.BanRecordDaoImpl;
import main.forumsystem.src.dao.impl.SensitiveWordDaoImpl;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.Topic;
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
 */
public class AdminServiceImpl implements AdminService {
    
    private final UserDao userDao;
    private final ForumDao forumDao;
    private final TopicDao topicDao;
    private final BanRecordDao banRecordDao;
    private final SensitiveWordDao sensitiveWordDao;
    
    public AdminServiceImpl() {
        this.userDao = new UserDaoImpl();
        this.forumDao = new ForumDaoImpl();
        this.topicDao = new TopicDaoImpl();
        this.banRecordDao = new BanRecordDaoImpl();
        this.sensitiveWordDao = new SensitiveWordDaoImpl();
    }
    
    // ==================== 用户管理 ====================
    
    @Override
    public List<User> getAllUsers() {
        try {
            return userDao.getAllUsers();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<User> getUsersByPage(int page, int size) {
        if (page <= 0 || size <= 0) {
            return new ArrayList<>();
        }
        
        try {
            return userDao.getUsersByPage(page, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<User> getUsersByRole(User.UserRole role) {
        if (role == null) {
            return new ArrayList<>();
        }
        
        try {
            return userDao.getUsersByRole(role);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<User> getUsersByStatus(User.UserStatus status) {
        if (status == null) {
            return new ArrayList<>();
        }
        
        try {
            return userDao.getUsersByStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<User> searchUsers(String keyword) {
        if (ValidationUtil.isEmpty(keyword)) {
            return new ArrayList<>();
        }
        
        try {
            return userDao.searchUsers(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public User getUserDetails(int userId) {
        if (userId <= 0) {
            return null;
        }
        
        try {
            return userDao.getUserById(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public AdminResult updateUser(User user) {
        if (user == null) {
            return new AdminResult(false, "用户信息不能为空");
        }
        
        if (user.getUserId() <= 0) {
            return new AdminResult(false, "用户ID无效");
        }
        
        try {
            // 验证用户是否存在
            User existingUser = userDao.getUserById(user.getUserId());
            if (existingUser == null) {
                return new AdminResult(false, "用户不存在");
            }
            
            // 验证用户名是否已存在（排除自己）
            if (!existingUser.getUsername().equals(user.getUsername())) {
                if (userDao.usernameExists(user.getUsername())) {
                    return new AdminResult(false, "用户名已存在");
                }
            }
            
            // 验证邮箱是否已存在（排除自己）
            if (!existingUser.getEmail().equals(user.getEmail())) {
                if (userDao.emailExists(user.getEmail())) {
                    return new AdminResult(false, "邮箱已存在");
                }
            }
            
            // 更新用户信息
            boolean success = userDao.updateUser(user);
            if (success) {
                return new AdminResult(true, "用户信息更新成功");
            } else {
                return new AdminResult(false, "更新失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，更新失败");
        }
    }
    
    @Override
    public AdminResult changeUserRole(int userId, User.UserRole newRole) {
        if (userId <= 0) {
            return new AdminResult(false, "用户ID无效");
        }
        
        if (newRole == null) {
            return new AdminResult(false, "角色不能为空");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new AdminResult(false, "用户不存在");
            }
            
            if (user.getRole() == newRole) {
                return new AdminResult(false, "用户已经是该角色");
            }
            
            boolean success = userDao.changeUserRole(userId, newRole);
            if (success) {
                return new AdminResult(true, "用户角色修改成功");
            } else {
                return new AdminResult(false, "修改失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，修改失败");
        }
    }
    
    @Override
    public AdminResult changeUserStatus(int userId, User.UserStatus newStatus) {
        if (userId <= 0) {
            return new AdminResult(false, "用户ID无效");
        }
        
        if (newStatus == null) {
            return new AdminResult(false, "状态不能为空");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new AdminResult(false, "用户不存在");
            }
            
            if (user.getStatus() == newStatus) {
                return new AdminResult(false, "用户已经是该状态");
            }
            
            boolean success = userDao.changeUserStatus(userId, newStatus);
            if (success) {
                return new AdminResult(true, "用户状态修改成功");
            } else {
                return new AdminResult(false, "修改失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，修改失败");
        }
    }
    
    @Override
    public AdminResult deleteUser(int userId) {
        if (userId <= 0) {
            return new AdminResult(false, "用户ID无效");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new AdminResult(false, "用户不存在");
            }
            
            // 管理员不能删除自己
            if (user.getRole() == User.UserRole.ADMIN) {
                return new AdminResult(false, "不能删除管理员账户");
            }
            
            boolean success = userDao.deleteUser(userId);
            if (success) {
                return new AdminResult(true, "用户删除成功");
            } else {
                return new AdminResult(false, "删除失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，删除失败");
        }
    }
    
    @Override
    public AdminResult batchDeleteUsers(int[] userIds) {
        if (userIds == null || userIds.length == 0) {
            return new AdminResult(false, "请选择要删除的用户");
        }
        
        try {
            // 检查是否包含管理员
            for (int userId : userIds) {
                User user = userDao.getUserById(userId);
                if (user != null && user.getRole() == User.UserRole.ADMIN) {
                    return new AdminResult(false, "不能删除管理员账户");
                }
            }
            
            int deletedCount = userDao.batchDeleteUsers(userIds);
            if (deletedCount > 0) {
                return new AdminResult(true, "成功删除" + deletedCount + "个用户");
            } else {
                return new AdminResult(false, "删除失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，删除失败");
        }
    }
    
    @Override
    public AdminResult resetUserPassword(int userId, String newPassword) {
        if (userId <= 0) {
            return new AdminResult(false, "用户ID无效");
        }
        
        if (ValidationUtil.isEmpty(newPassword)) {
            return new AdminResult(false, "新密码不能为空");
        }
        
        if (newPassword.length() < 6) {
            return new AdminResult(false, "密码长度至少6位");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new AdminResult(false, "用户不存在");
            }
            
            boolean success = userDao.changePassword(userId, newPassword);
            if (success) {
                return new AdminResult(true, "密码重置成功");
            } else {
                return new AdminResult(false, "重置失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，重置失败");
        }
    }
    
    // ==================== 版主管理 ====================
    
    @Override
    public List<User> getAllModerators() {
        try {
            return userDao.getUsersByRole(User.UserRole.MODERATOR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public AdminResult appointModerator(int userId, int forumId) {
        if (userId <= 0) {
            return new AdminResult(false, "用户ID无效");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new AdminResult(false, "用户不存在");
            }
            
            if (user.getStatus() == User.UserStatus.BANNED) {
                return new AdminResult(false, "被封禁用户不能任命为版主");
            }
            
            // 如果指定了板块ID，设置为板块版主
            if (forumId > 0) {
                Forum forum = forumDao.getForumById(forumId);
                if (forum == null) {
                    return new AdminResult(false, "板块不存在");
                }
                
                boolean success = forumDao.setForumModerator(forumId, userId);
                if (!success) {
                    return new AdminResult(false, "设置板块版主失败");
                }
            }
            
            // 提升用户角色为版主
            if (user.getRole() == User.UserRole.USER) {
                boolean roleSuccess = userDao.changeUserRole(userId, User.UserRole.MODERATOR);
                if (!roleSuccess) {
                    return new AdminResult(false, "提升用户角色失败");
                }
            }
            
            return new AdminResult(true, "版主任命成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，任命失败");
        }
    }
    
    @Override
    public AdminResult revokeModerator(int userId, int forumId) {
        if (userId <= 0) {
            return new AdminResult(false, "用户ID无效");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new AdminResult(false, "用户不存在");
            }
            
            if (user.getRole() != User.UserRole.MODERATOR) {
                return new AdminResult(false, "用户不是版主");
            }
            
            // 如果指定了板块ID，撤销板块版主
            if (forumId > 0) {
                Forum forum = forumDao.getForumById(forumId);
                if (forum == null) {
                    return new AdminResult(false, "板块不存在");
                }
                
                if (forum.getModeratorId() != userId) {
                    return new AdminResult(false, "用户不是该板块的版主");
                }
                
                boolean success = forumDao.setForumModerator(forumId, 0);
                if (!success) {
                    return new AdminResult(false, "撤销板块版主失败");
                }
            }
            
            // 检查用户是否还管理其他板块
            List<Forum> managedForums = forumDao.getForumsByModerator(userId);
            if (managedForums.isEmpty()) {
                // 如果不再管理任何板块，降级为普通用户
                boolean roleSuccess = userDao.changeUserRole(userId, User.UserRole.USER);
                if (!roleSuccess) {
                    return new AdminResult(false, "降级用户角色失败");
                }
            }
            
            return new AdminResult(true, "版主撤销成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，撤销失败");
        }
    }
    
    @Override
    public List<Forum> getModeratorForums(int moderatorId) {
        if (moderatorId <= 0) {
            return new ArrayList<>();
        }
        
        try {
            return forumDao.getForumsByModerator(moderatorId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // ==================== 板块管理 ====================
    
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
    public List<Forum> getForumsByPage(int page, int size) {
        if (page <= 0 || size <= 0) {
            return new ArrayList<>();
        }
        
        try {
            return forumDao.getForumsByPage(page, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Forum> getForumsByStatus(Forum.ForumStatus status) {
        if (status == null) {
            return new ArrayList<>();
        }
        
        try {
            return forumDao.getForumsByStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Forum> searchForums(String keyword) {
        if (ValidationUtil.isEmpty(keyword)) {
            return new ArrayList<>();
        }
        
        try {
            return forumDao.searchForums(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public AdminResult createForum(Forum forum) {
        if (forum == null) {
            return new AdminResult(false, "板块信息不能为空");
        }
        
        if (ValidationUtil.isEmpty(forum.getForumName())) {
            return new AdminResult(false, "板块名称不能为空");
        }
        
        if (forum.getForumName().length() > 50) {
            return new AdminResult(false, "板块名称长度不能超过50字符");
        }
        
        try {
            // 检查板块名称是否已存在
            if (forumDao.forumNameExists(forum.getForumName(), 0)) {
                return new AdminResult(false, "板块名称已存在");
            }
            
            // 设置默认值
            forum.setCreateTime(LocalDateTime.now());
            forum.setTopicCount(0);
            forum.setPostCount(0);
            if (forum.getStatus() == null) {
                forum.setStatus(Forum.ForumStatus.ACTIVE);
            }
            
            boolean success = forumDao.addForum(forum);
            if (success) {
                return new AdminResult(true, "板块创建成功");
            } else {
                return new AdminResult(false, "创建失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，创建失败");
        }
    }
    
    @Override
    public AdminResult updateForum(Forum forum) {
        if (forum == null) {
            return new AdminResult(false, "板块信息不能为空");
        }
        
        if (forum.getForumId() <= 0) {
            return new AdminResult(false, "板块ID无效");
        }
        
        try {
            Forum existingForum = forumDao.getForumById(forum.getForumId());
            if (existingForum == null) {
                return new AdminResult(false, "板块不存在");
            }
            
            // 检查板块名称是否已存在（排除自己）
            if (!existingForum.getForumName().equals(forum.getForumName())) {
                if (forumDao.forumNameExists(forum.getForumName(), forum.getForumId())) {
                    return new AdminResult(false, "板块名称已存在");
                }
            }
            
            boolean success = forumDao.updateForum(forum);
            if (success) {
                return new AdminResult(true, "板块更新成功");
            } else {
                return new AdminResult(false, "更新失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，更新失败");
        }
    }
    
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
    public AdminResult batchDeleteForums(int[] forumIds) {
        if (forumIds == null || forumIds.length == 0) {
            return new AdminResult(false, "请选择要删除的板块");
        }
        
        try {
            int deletedCount = 0;
            for (int forumId : forumIds) {
                Forum forum = forumDao.getForumById(forumId);
                if (forum != null && forum.getTopicCount() == 0) {
                    if (forumDao.deleteForum(forumId)) {
                        deletedCount++;
                    }
                }
            }
            
            if (deletedCount > 0) {
                return new AdminResult(true, "成功删除" + deletedCount + "个板块");
            } else {
                return new AdminResult(false, "没有可删除的板块");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，删除失败");
        }
    }
    
    @Override
    public AdminResult changeForumStatus(int forumId, Forum.ForumStatus status) {
        if (forumId <= 0) {
            return new AdminResult(false, "板块ID无效");
        }
        
        if (status == null) {
            return new AdminResult(false, "状态不能为空");
        }
        
        try {
            Forum forum = forumDao.getForumById(forumId);
            if (forum == null) {
                return new AdminResult(false, "板块不存在");
            }
            
            if (forum.getStatus() == status) {
                return new AdminResult(false, "板块已经是该状态");
            }
            
            boolean success = forumDao.changeForumStatus(forumId, status);
            if (success) {
                return new AdminResult(true, "板块状态修改成功");
            } else {
                return new AdminResult(false, "修改失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，修改失败");
        }
    }
    
    @Override
    public AdminResult setForumModerator(int forumId, int moderatorId) {
        if (forumId <= 0) {
            return new AdminResult(false, "板块ID无效");
        }
        
        try {
            Forum forum = forumDao.getForumById(forumId);
            if (forum == null) {
                return new AdminResult(false, "板块不存在");
            }
            
            if (moderatorId > 0) {
                User moderator = userDao.getUserById(moderatorId);
                if (moderator == null) {
                    return new AdminResult(false, "版主不存在");
                }
                
                if (moderator.getStatus() == User.UserStatus.BANNED) {
                    return new AdminResult(false, "被封禁用户不能成为版主");
                }
                
                // 如果用户不是版主，提升为版主
                if (moderator.getRole() == User.UserRole.USER) {
                    userDao.changeUserRole(moderatorId, User.UserRole.MODERATOR);
                }
            }
            
            boolean success = forumDao.setForumModerator(forumId, moderatorId);
            if (success) {
                return new AdminResult(true, "版主设置成功");
            } else {
                return new AdminResult(false, "设置失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，设置失败");
        }
    }
    
    // ==================== 内容管理 ====================
    
    @Override
    public List<Topic> getAllTopics(Topic.TopicStatus status) {
        try {
            if (status == null) {
                // 获取所有正常状态的主题
                return topicDao.getTopicsByStatus(0, Topic.TopicStatus.NORMAL);
            } else {
                return topicDao.getTopicsByStatus(0, status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
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
    public AdminResult batchDeleteTopics(int[] topicIds) {
        if (topicIds == null || topicIds.length == 0) {
            return new AdminResult(false, "请选择要删除的主题");
        }
        
        try {
            int deletedCount = topicDao.batchDeleteTopics(topicIds);
            if (deletedCount > 0) {
                // 重新计算所有相关板块的统计数据
                Map<Integer, Integer> forumCounts = new HashMap<>();
                for (int topicId : topicIds) {
                    Topic topic = topicDao.getTopicById(topicId);
                    if (topic != null) {
                        forumCounts.put(topic.getForumId(), 
                            forumCounts.getOrDefault(topic.getForumId(), 0) + 1);
                    }
                }
                
                for (Map.Entry<Integer, Integer> entry : forumCounts.entrySet()) {
                    forumDao.updateTopicCount(entry.getKey(), -entry.getValue());
                }
                
                return new AdminResult(true, "成功删除" + deletedCount + "个主题");
            } else {
                return new AdminResult(false, "删除失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，删除失败");
        }
    }
    
    @Override
    public AdminResult changeTopicStatus(int topicId, Topic.TopicStatus status) {
        if (topicId <= 0) {
            return new AdminResult(false, "主题ID无效");
        }
        
        if (status == null) {
            return new AdminResult(false, "状态不能为空");
        }
        
        try {
            Topic topic = topicDao.getTopicById(topicId);
            if (topic == null) {
                return new AdminResult(false, "主题不存在");
            }
            
            if (topic.getStatus() == status) {
                return new AdminResult(false, "主题已经是该状态");
            }
            
            boolean success = topicDao.changeTopicStatus(topicId, status);
            if (success) {
                return new AdminResult(true, "主题状态修改成功");
            } else {
                return new AdminResult(false, "修改失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，修改失败");
        }
    }
    
    @Override
    public AdminResult moveTopic(int topicId, int targetForumId) {
        if (topicId <= 0 || targetForumId <= 0) {
            return new AdminResult(false, "参数无效");
        }
        
        try {
            Topic topic = topicDao.getTopicById(topicId);
            if (topic == null) {
                return new AdminResult(false, "主题不存在");
            }
            
            Forum targetForum = forumDao.getForumById(targetForumId);
            if (targetForum == null) {
                return new AdminResult(false, "目标板块不存在");
            }
            
            if (topic.getForumId() == targetForumId) {
                return new AdminResult(false, "主题已在目标板块中");
            }
            
            int originalForumId = topic.getForumId();
            boolean success = topicDao.moveTopic(topicId, targetForumId);
            if (success) {
                // 更新板块主题数量
                forumDao.updateTopicCount(originalForumId, -1);
                forumDao.updateTopicCount(targetForumId, 1);
                
                return new AdminResult(true, "主题移动成功");
            } else {
                return new AdminResult(false, "移动失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，移动失败");
        }
    }
    
    @Override
    public AdminResult batchMoveTopics(int[] topicIds, int targetForumId) {
        if (topicIds == null || topicIds.length == 0) {
            return new AdminResult(false, "请选择要移动的主题");
        }
        
        if (targetForumId <= 0) {
            return new AdminResult(false, "目标板块ID无效");
        }
        
        try {
            Forum targetForum = forumDao.getForumById(targetForumId);
            if (targetForum == null) {
                return new AdminResult(false, "目标板块不存在");
            }
            
            int movedCount = topicDao.batchMoveTopics(topicIds, targetForumId);
            if (movedCount > 0) {
                // 更新板块统计数据
                Map<Integer, Integer> forumCounts = new HashMap<>();
                for (int topicId : topicIds) {
                    Topic topic = topicDao.getTopicById(topicId);
                    if (topic != null && topic.getForumId() != targetForumId) {
                        forumCounts.put(topic.getForumId(), 
                            forumCounts.getOrDefault(topic.getForumId(), 0) + 1);
                    }
                }
                
                for (Map.Entry<Integer, Integer> entry : forumCounts.entrySet()) {
                    forumDao.updateTopicCount(entry.getKey(), -entry.getValue());
                }
                
                forumDao.updateTopicCount(targetForumId, movedCount);
                
                return new AdminResult(true, "成功移动" + movedCount + "个主题");
            } else {
                return new AdminResult(false, "移动失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，移动失败");
        }
    }
    
    // ==================== 封禁管理 ====================
    
    @Override
    public AdminResult banUser(int userId, String reason, int durationDays) {
        if (userId <= 0) {
            return new AdminResult(false, "用户ID无效");
        }
        
        if (ValidationUtil.isEmpty(reason)) {
            return new AdminResult(false, "封禁原因不能为空");
        }
        
        if (durationDays < 0) {
            return new AdminResult(false, "封禁天数不能为负数");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new AdminResult(false, "用户不存在");
            }
            
            if (user.getRole() == User.UserRole.ADMIN) {
                return new AdminResult(false, "不能封禁管理员");
            }
            
            // 检查是否已被封禁
            if (banRecordDao.isUserBanned(userId)) {
                return new AdminResult(false, "用户已被封禁");
            }
            
            // 创建封禁记录
            BanRecord banRecord = new BanRecord();
            banRecord.setUserId(userId);
            banRecord.setAdminId(1); // 这里应该传入当前管理员ID
            banRecord.setReason(reason);
            banRecord.setBanStart(LocalDateTime.now());
            banRecord.setPermanent(durationDays == 0);
            banRecord.setStatus(BanRecord.BanStatus.ACTIVE);
            
            if (durationDays > 0) {
                banRecord.setBanEnd(LocalDateTime.now().plusDays(durationDays));
            }
            
            boolean success = banRecordDao.addBanRecord(banRecord);
            if (success) {
                // 更新用户状态为封禁
                userDao.changeUserStatus(userId, User.UserStatus.BANNED);
                String message = durationDays == 0 ? "用户已永久封禁" : "用户已封禁" + durationDays + "天";
                return new AdminResult(true, message);
            } else {
                return new AdminResult(false, "封禁失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，封禁失败");
        }
    }
    
    @Override
    public AdminResult unbanUser(int userId) {
        if (userId <= 0) {
            return new AdminResult(false, "用户ID无效");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new AdminResult(false, "用户不存在");
            }
            
            if (!banRecordDao.isUserBanned(userId)) {
                return new AdminResult(false, "用户未被封禁");
            }
            
            boolean success = banRecordDao.liftUserBan(userId, 1); // 这里应该传入当前管理员ID
            if (success) {
                // 更新用户状态为正常
                userDao.changeUserStatus(userId, User.UserStatus.ACTIVE);
                return new AdminResult(true, "用户解封成功");
            } else {
                return new AdminResult(false, "解封失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，解封失败");
        }
    }
    
    @Override
    public List<BanRecord> getAllBanRecords() {
        try {
            // 使用现有方法
            return banRecordDao.getActiveBanRecords();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<BanRecord> getActiveBanRecords() {
        try {
            return banRecordDao.getActiveBanRecords();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<BanRecord> getBanRecordsByUserId(int userId) {
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
    public AdminResult deleteBanRecord(int banId) {
        if (banId <= 0) {
            return new AdminResult(false, "封禁记录ID无效");
        }
        
        try {
            BanRecord banRecord = banRecordDao.getBanRecordById(banId);
            if (banRecord == null) {
                return new AdminResult(false, "封禁记录不存在");
            }
            
            boolean success = banRecordDao.deleteBanRecord(banId);
            if (success) {
                return new AdminResult(true, "封禁记录删除成功");
            } else {
                return new AdminResult(false, "删除失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，删除失败");
        }
    }
    
    // ==================== 敏感词管理 ====================
    
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
    public AdminResult addSensitiveWord(String word, int level, String action) {
        if (ValidationUtil.isEmpty(word)) {
            return new AdminResult(false, "敏感词不能为空");
        }
        
        try {
            // 检查敏感词是否已存在
            if (sensitiveWordDao.existsSensitiveWord(word)) {
                return new AdminResult(false, "敏感词已存在");
            }
            
            SensitiveWord sensitiveWord = new SensitiveWord();
            sensitiveWord.setWord(word);
            sensitiveWord.setCreateTime(LocalDateTime.now());
            
            boolean success = sensitiveWordDao.addSensitiveWord(sensitiveWord);
            if (success) {
                return new AdminResult(true, "敏感词添加成功");
            } else {
                return new AdminResult(false, "添加失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，添加失败");
        }
    }
    
    @Override
    public AdminResult deleteSensitiveWord(int wordId) {
        if (wordId <= 0) {
            return new AdminResult(false, "敏感词ID无效");
        }
        
        try {
            SensitiveWord sensitiveWord = sensitiveWordDao.getSensitiveWordById(wordId);
            if (sensitiveWord == null) {
                return new AdminResult(false, "敏感词不存在");
            }
            
            boolean success = sensitiveWordDao.deleteSensitiveWord(wordId);
            if (success) {
                return new AdminResult(true, "敏感词删除成功");
            } else {
                return new AdminResult(false, "删除失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，删除失败");
        }
    }
    
    @Override
    public AdminResult batchDeleteSensitiveWords(int[] wordIds) {
        if (wordIds == null || wordIds.length == 0) {
            return new AdminResult(false, "请选择要删除的敏感词");
        }
        
        try {
            int deletedCount = sensitiveWordDao.batchDeleteSensitiveWords(wordIds);
            if (deletedCount > 0) {
                return new AdminResult(true, "成功删除" + deletedCount + "个敏感词");
            } else {
                return new AdminResult(false, "删除失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，删除失败");
        }
    }
    
    @Override
    public AdminResult updateSensitiveWord(SensitiveWord sensitiveWord) {
        if (sensitiveWord == null) {
            return new AdminResult(false, "敏感词信息不能为空");
        }
        
        if (sensitiveWord.getWordId() <= 0) {
            return new AdminResult(false, "敏感词ID无效");
        }
        
        try {
            SensitiveWord existingWord = sensitiveWordDao.getSensitiveWordById(sensitiveWord.getWordId());
            if (existingWord == null) {
                return new AdminResult(false, "敏感词不存在");
            }
            
            boolean success = sensitiveWordDao.updateSensitiveWord(sensitiveWord);
            if (success) {
                return new AdminResult(true, "敏感词更新成功");
            } else {
                return new AdminResult(false, "更新失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new AdminResult(false, "系统错误，更新失败");
        }
    }
    
    // ==================== 系统统计 ====================
    
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
            stats.put("activeBans", banRecordDao.getActiveBanRecords().size());
            
            // 敏感词统计 - 使用现有方法
            stats.put("totalSensitiveWords", sensitiveWordDao.getAllSensitiveWords().size());
            
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
            stats.put("admins", userDao.getUsersByRole(User.UserRole.ADMIN).size());
            stats.put("moderators", userDao.getUsersByRole(User.UserRole.MODERATOR).size());
            stats.put("users", userDao.getUsersByRole(User.UserRole.USER).size());
            
            // 按状态统计
            stats.put("activeUsersByStatus", userDao.getUsersByStatus(User.UserStatus.ACTIVE).size());
            stats.put("bannedUsers", userDao.getUsersByStatus(User.UserStatus.BANNED).size());
            
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
            
            // 按状态统计 - 只使用存在的状态
            stats.put("activeForumsByStatus", forumDao.getForumsByStatus(Forum.ForumStatus.ACTIVE).size());
            
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
            stats.put("normalTopics", topicDao.getTopicsByStatus(0, Topic.TopicStatus.NORMAL).size());
            stats.put("hiddenTopics", topicDao.getTopicsByStatus(0, Topic.TopicStatus.HIDDEN).size());
            stats.put("deletedTopics", topicDao.getTopicsByStatus(0, Topic.TopicStatus.DELETED).size());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getTodayStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            stats.put("todayNewUsers", userDao.getTodayRegisterCount());
            stats.put("todayTopics", topicDao.getTodayTopicCount(0));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    // ==================== 日志管理 ====================
    
    @Override
    public List<Map<String, Object>> getSystemLogs(int page, int size) {
        // 由于没有日志相关的DAO，暂时返回空列表
        return new ArrayList<>();
    }
    
    @Override
    public List<Map<String, Object>> getUserLogs(int userId, int page, int size) {
        // 由于没有日志相关的DAO，暂时返回空列表
        return new ArrayList<>();
    }
    
    @Override
    public AdminResult clearExpiredLogs(int days) {
        // 由于没有日志相关的DAO，暂时返回成功
        return new AdminResult(true, "日志清理功能待实现");
    }
    
    // ==================== 权限验证 ====================
    
    @Override
    public boolean isAdmin(int userId) {
        if (userId <= 0) {
            return false;
        }
        
        try {
            User user = userDao.getUserById(userId);
            return user != null && user.getRole() == User.UserRole.ADMIN;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean canPerformAdminOperation(int userId, String operation) {
        if (userId <= 0 || ValidationUtil.isEmpty(operation)) {
            return false;
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return false;
            }
            
            // 只有管理员可以执行管理操作
            if (user.getRole() == User.UserRole.ADMIN) {
                return true;
            }
            
            // 版主可以执行部分操作
            if (user.getRole() == User.UserRole.MODERATOR) {
                return operation.equals("view_users") || 
                       operation.equals("manage_topics") || 
                       operation.equals("manage_replies");
            }
            
            return false;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
