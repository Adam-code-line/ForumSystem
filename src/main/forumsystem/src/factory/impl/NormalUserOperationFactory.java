package main.forumsystem.src.factory.impl;

import main.forumsystem.src.factory.UserOperationFactory;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.Topic;
import main.forumsystem.src.entity.Reply;
import main.forumsystem.src.dao.ForumDao;
import main.forumsystem.src.dao.TopicDao;
import main.forumsystem.src.dao.impl.ForumDaoImpl;
import main.forumsystem.src.dao.impl.TopicDaoImpl;

import java.time.LocalDateTime;

/**
 * 普通用户操作工厂
 * 普通用户可以创建板块成为版主，在开放板块发帖回帖
 */
public class NormalUserOperationFactory implements UserOperationFactory {
    
    private final ForumDao forumDao;
    private final TopicDao topicDao;
    
    public NormalUserOperationFactory() {
        this.forumDao = new ForumDaoImpl();
        this.topicDao = new TopicDaoImpl();
    }
    
    @Override
    public Forum createForum(String forumName, String description, User creator) {
        // 普通用户创建板块后自动成为该板块版主
        Forum forum = new Forum();
        forum.setForumName(forumName);
        forum.setDescription(description);
        forum.setModeratorId(creator.getUserId()); // 设置为版主
        forum.setCreateTime(LocalDateTime.now());
        forum.setTopicCount(0);
        forum.setPostCount(0);
        forum.setStatus(Forum.ForumStatus.ACTIVE);
        forum.setSortOrder(0);
        return forum;
    }
    
    @Override
    public Topic createTopic(String title, String content, int forumId, User author) {
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setContent(content);
        topic.setForumId(forumId);
        topic.setUserId(author.getUserId());
        topic.setCreateTime(LocalDateTime.now());
        topic.setLastReplyTime(LocalDateTime.now());
        topic.setLastReplyUserId(author.getUserId());
        topic.setReplyCount(0);
        topic.setViewCount(0);
        topic.setStatus(Topic.TopicStatus.NORMAL);
        topic.setPinned(false);
        topic.setLocked(false);
        return topic;
    }
    
    @Override
    public Reply createReply(String content, int topicId, User author) {
        Reply reply = new Reply();
        reply.setContent(content);
        reply.setTopicId(topicId);
        reply.setUserId(author.getUserId());
        reply.setCreateTime(LocalDateTime.now());
        reply.setStatus(Reply.ReplyStatus.NORMAL);
        return reply;
    }
    
    @Override
    public boolean canCreateForum(User user) {
        return user != null && user.getRole() == User.UserRole.USER 
               && user.getStatus() == User.UserStatus.ACTIVE;
    }
    
    @Override
    public boolean canCreateTopic(User user, int forumId) {
        if (user == null || user.getRole() != User.UserRole.USER 
            || user.getStatus() != User.UserStatus.ACTIVE) {
            return false;
        }
        
        try {
            Forum forum = forumDao.getForumById(forumId);
            return forum != null && forum.getStatus() == Forum.ForumStatus.ACTIVE;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean canReply(User user, int topicId) {
        if (user == null || user.getRole() != User.UserRole.USER 
            || user.getStatus() != User.UserStatus.ACTIVE) {
            return false;
        }
        
        try {
            Topic topic = topicDao.getTopicById(topicId);
            return topic != null && topic.getStatus() == Topic.TopicStatus.NORMAL 
                   && !topic.isLocked();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean canManageForum(User user, int forumId) {
        if (user == null || user.getRole() != User.UserRole.USER 
            || user.getStatus() != User.UserStatus.ACTIVE) {
            return false;
        }
        
        try {
            Forum forum = forumDao.getForumById(forumId);
            // 普通用户如果是该板块的版主，可以管理
            return forum != null && forum.getModeratorId() == user.getUserId();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean canManageTopic(User user, int topicId) {
        if (user == null || user.getRole() != User.UserRole.USER 
            || user.getStatus() != User.UserStatus.ACTIVE) {
            return false;
        }
        
        try {
            Topic topic = topicDao.getTopicById(topicId);
            if (topic == null) {
                return false;
            }
            
            // 普通用户只能管理自己发布的主题
            if (topic.getUserId() == user.getUserId()) {
                return true;
            }
            
            // 或者如果是该板块的版主也可以管理
            Forum forum = forumDao.getForumById(topic.getForumId());
            return forum != null && forum.getModeratorId() == user.getUserId();
        } catch (Exception e) {
            return false;
        }
    }
}
