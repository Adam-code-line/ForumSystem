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
 * 版主操作工厂
 * 版主可以管理自己的板块，在任何板块发帖回帖
 */
public class ModeratorOperationFactory implements UserOperationFactory {
    
    private final ForumDao forumDao;
    private final TopicDao topicDao;
    
    public ModeratorOperationFactory() {
        this.forumDao = new ForumDaoImpl();
        this.topicDao = new TopicDaoImpl();
    }
    
    @Override
    public Forum createForum(String forumName, String description, User creator) {
        Forum forum = new Forum();
        forum.setForumName(forumName);
        forum.setDescription(description);
        forum.setModeratorId(creator.getUserId());
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
        return user != null && user.getRole() == User.UserRole.MODERATOR 
               && user.getStatus() == User.UserStatus.ACTIVE;
    }
    
    @Override
    public boolean canCreateTopic(User user, int forumId) {
        if (user == null || user.getRole() != User.UserRole.MODERATOR 
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
        if (user == null || user.getRole() != User.UserRole.MODERATOR 
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
        if (user == null || user.getRole() != User.UserRole.MODERATOR 
            || user.getStatus() != User.UserStatus.ACTIVE) {
            return false;
        }
        
        try {
            Forum forum = forumDao.getForumById(forumId);
            // 版主只能管理自己负责的板块
            return forum != null && forum.getModeratorId() == user.getUserId();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean canManageTopic(User user, int topicId) {
        if (user == null || user.getRole() != User.UserRole.MODERATOR 
            || user.getStatus() != User.UserStatus.ACTIVE) {
            return false;
        }
        
        try {
            Topic topic = topicDao.getTopicById(topicId);
            if (topic == null) {
                return false;
            }
            
            Forum forum = forumDao.getForumById(topic.getForumId());
            // 版主可以管理自己板块内的主题，或者自己发布的主题
            return forum != null && (forum.getModeratorId() == user.getUserId() 
                   || topic.getUserId() == user.getUserId());
        } catch (Exception e) {
            return false;
        }
    }
}
