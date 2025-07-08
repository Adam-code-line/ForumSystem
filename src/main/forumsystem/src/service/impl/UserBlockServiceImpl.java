package main.forumsystem.src.service.impl;

import main.forumsystem.src.dao.UserBlockDao;
import main.forumsystem.src.dao.UserDao;
import main.forumsystem.src.dao.ForumDao;
import main.forumsystem.src.dao.TopicDao;
import main.forumsystem.src.dao.impl.UserBlockDaoImpl;
import main.forumsystem.src.dao.impl.UserDaoImpl;
import main.forumsystem.src.dao.impl.ForumDaoImpl;
import main.forumsystem.src.dao.impl.TopicDaoImpl;
import main.forumsystem.src.entity.UserBlock;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.Topic;
import main.forumsystem.src.entity.Reply;
import main.forumsystem.src.service.UserBlockService;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 用户拉黑服务实现类
 */
public class UserBlockServiceImpl implements UserBlockService {
    
    private final UserBlockDao userBlockDao;
    private final UserDao userDao;
    private final ForumDao forumDao;
    private final TopicDao topicDao;
    
    public UserBlockServiceImpl() {
        this.userBlockDao = new UserBlockDaoImpl();
        this.userDao = new UserDaoImpl();
        this.forumDao = new ForumDaoImpl();
        this.topicDao = new TopicDaoImpl();
    }
    
    @Override
    public BlockResult blockUser(int blockerId, int blockedId, String reason) {
        if (blockerId <= 0 || blockedId <= 0) {
            return new BlockResult(false, "用户ID无效");
        }
        
        if (blockerId == blockedId) {
            return new BlockResult(false, "不能拉黑自己");
        }
        
        try {
            // 验证用户存在性
            User blocker = userDao.getUserById(blockerId);
            User blocked = userDao.getUserById(blockedId);
            
            if (blocker == null) {
                return new BlockResult(false, "拉黑用户不存在");
            }
            
            if (blocked == null) {
                return new BlockResult(false, "被拉黑用户不存在");
            }
            
            // 检查用户状态
            if (blocker.getStatus() == User.UserStatus.BANNED) {
                return new BlockResult(false, "被封禁用户无法拉黑其他用户");
            }
            
            // 不能拉黑管理员
            if (blocked.getRole() == User.UserRole.ADMIN) {
                return new BlockResult(false, "不能拉黑管理员");
            }
            
            // 检查是否已经拉黑
            if (userBlockDao.isBlocked(blockerId, blockedId)) {
                return new BlockResult(false, "已经拉黑过该用户");
            }
            
            // 创建拉黑记录
            UserBlock userBlock = new UserBlock(blockerId, blockedId, reason);
            boolean success = userBlockDao.addBlock(userBlock);
            
            if (success) {
                return new BlockResult(true, "拉黑成功", userBlock);
            } else {
                return new BlockResult(false, "拉黑失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new BlockResult(false, "系统错误，拉黑失败");
        }
    }
    
    @Override
    public BlockResult unblockUser(int blockerId, int blockedId) {
        if (blockerId <= 0 || blockedId <= 0) {
            return new BlockResult(false, "用户ID无效");
        }
        
        try {
            // 检查是否存在拉黑记录
            if (!userBlockDao.isBlocked(blockerId, blockedId)) {
                return new BlockResult(false, "未拉黑该用户");
            }
            
            // 移除拉黑记录
            boolean success = userBlockDao.removeBlock(blockerId, blockedId);
            
            if (success) {
                return new BlockResult(true, "取消拉黑成功");
            } else {
                return new BlockResult(false, "取消拉黑失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new BlockResult(false, "系统错误，取消拉黑失败");
        }
    }
    
    @Override
    public boolean isUserBlocked(int blockerId, int blockedId) {
        if (blockerId <= 0 || blockedId <= 0 || blockerId == blockedId) {
            return false;
        }
        
        try {
            return userBlockDao.isBlocked(blockerId, blockedId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean canUserPostInForum(int userId, int forumId) {
        if (userId <= 0 || forumId <= 0) {
            return false;
        }
        
        try {
            // 获取板块信息
            Forum forum = forumDao.getForumById(forumId);
            if (forum == null) {
                return false;
            }
            
            // 如果板块有版主，检查版主是否拉黑了该用户
            if (forum.getModeratorId() > 0) {
                return !userBlockDao.isBlocked(forum.getModeratorId(), userId);
            }
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean canUserReplyToTopic(int userId, int topicId) {
        if (userId <= 0 || topicId <= 0) {
            return false;
        }
        
        try {
            // 获取主题信息
            Topic topic = topicDao.getTopicById(topicId);
            if (topic == null) {
                return false;
            }
            
            // 检查主题作者是否拉黑了该用户
            if (userBlockDao.isBlocked(topic.getUserId(), userId)) {
                return false;
            }
            
            // 检查该用户是否可以在对应板块发言
            return canUserPostInForum(userId, topic.getForumId());
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<User> getBlockedUsers(int userId) {
        if (userId <= 0) {
            return new ArrayList<>();
        }
        
        try {
            List<UserBlock> blockList = userBlockDao.getUserBlockList(userId);
            List<User> blockedUsers = new ArrayList<>();
            
            for (UserBlock block : blockList) {
                if (block.getStatus() == UserBlock.BlockStatus.ACTIVE) {
                    User user = userDao.getUserById(block.getBlockedId());
                    if (user != null) {
                        blockedUsers.add(user);
                    }
                }
            }
            
            return blockedUsers;
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<User> getBlockedByUsers(int userId) {
        if (userId <= 0) {
            return new ArrayList<>();
        }
        
        try {
            List<UserBlock> blockList = userBlockDao.getBlockedByList(userId);
            List<User> blockedByUsers = new ArrayList<>();
            
            for (UserBlock block : blockList) {
                if (block.getStatus() == UserBlock.BlockStatus.ACTIVE) {
                    User user = userDao.getUserById(block.getBlockerId());
                    if (user != null) {
                        blockedByUsers.add(user);
                    }
                }
            }
            
            return blockedByUsers;
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> filterBlockedContent(int viewerId, List<T> contentList) {
        if (viewerId <= 0 || contentList == null || contentList.isEmpty()) {
            return contentList != null ? contentList : new ArrayList<>();
        }
        
        try {
            return contentList.stream()
                    .filter(content -> {
                        // 根据内容类型判断作者ID
                        int authorId = getAuthorIdFromContent(content);
                        if (authorId <= 0) {
                            return true;
                        }
                        
                        // 过滤掉被当前用户拉黑的用户的内容
                        return !userBlockDao.isBlocked(viewerId, authorId);
                    })
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            e.printStackTrace();
            return contentList;
        }
    }
    
    @Override
    public boolean isMutualBlock(int userId1, int userId2) {
        if (userId1 <= 0 || userId2 <= 0 || userId1 == userId2) {
            return false;
        }
        
        try {
            return userBlockDao.isBlocked(userId1, userId2) && 
                   userBlockDao.isBlocked(userId2, userId1);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 从内容对象中获取作者ID
     */
    @SuppressWarnings("unchecked")
    private <T> int getAuthorIdFromContent(T content) {
        try {
            if (content instanceof Topic) {
                return ((Topic) content).getUserId();
            } else if (content instanceof Reply) {
                return ((Reply) content).getUserId();
            }
            // 如果是其他类型，可以通过反射获取userId字段
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
