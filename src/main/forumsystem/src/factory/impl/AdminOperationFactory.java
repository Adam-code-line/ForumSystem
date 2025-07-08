package main.forumsystem.src.factory.impl;

import main.forumsystem.src.factory.UserOperationFactory;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.Topic;
import main.forumsystem.src.entity.Reply;

import java.time.LocalDateTime;

/**
 * 管理员操作工厂
 * 管理员拥有所有权限
 */
public class AdminOperationFactory implements UserOperationFactory {
    
    @Override
    public Forum createForum(String forumName, String description, User creator) {
        Forum forum = new Forum();
        forum.setForumName(forumName);
        forum.setDescription(description);
        forum.setModeratorId(creator.getUserId()); // 设置版主为创建者
        forum.setCreateTime(LocalDateTime.now());
        forum.setTopicCount(0);
        forum.setPostCount(0);
        forum.setStatus(Forum.ForumStatus.ACTIVE);
        forum.setSortOrder(0); // 设置默认排序
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
        return user != null && user.getRole() == User.UserRole.ADMIN 
               && user.getStatus() == User.UserStatus.ACTIVE;
    }
    
    @Override
    public boolean canCreateTopic(User user, int forumId) {
        return user != null && user.getRole() == User.UserRole.ADMIN 
               && user.getStatus() == User.UserStatus.ACTIVE;
    }
    
    @Override
    public boolean canReply(User user, int topicId) {
        return user != null && user.getRole() == User.UserRole.ADMIN 
               && user.getStatus() == User.UserStatus.ACTIVE;
    }
    
    @Override
    public boolean canManageForum(User user, int forumId) {
        return user != null && user.getRole() == User.UserRole.ADMIN 
               && user.getStatus() == User.UserStatus.ACTIVE;
    }
    
    @Override
    public boolean canManageTopic(User user, int topicId) {
        return user != null && user.getRole() == User.UserRole.ADMIN 
               && user.getStatus() == User.UserStatus.ACTIVE;
    }
}
