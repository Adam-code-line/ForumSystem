package main.forumsystem.src.factory.impl;

import main.forumsystem.src.factory.UserOperationFactory;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.Topic;
import main.forumsystem.src.entity.Reply;

import java.time.LocalDateTime;

/**
 * 管理员操作工厂
 * 管理员拥有所有权限，能够创建论坛、主题、回复，并管理论坛和主题。
 */
public class AdminOperationFactory implements UserOperationFactory {

    /**
     * 创建论坛
     * 管理员可以创建新的论坛，并设置论坛的基本信息。
     * @param forumName 论坛名称
     * @param description 论坛描述
     * @param creator 创建者（管理员）
     * @return Forum 创建的论坛对象
     */
    @Override
    public Forum createForum(String forumName, String description, User creator) {
        Forum forum = new Forum();
        forum.setForumName(forumName);
        forum.setDescription(description);
        forum.setModeratorId(creator.getUserId()); // 设置版主为创建者
        forum.setCreateTime(LocalDateTime.now());
        forum.setTopicCount(0); // 初始化主题数量为0
        forum.setPostCount(0); // 初始化帖子数量为0
        forum.setStatus(Forum.ForumStatus.ACTIVE); // 设置论坛状态为活跃
        forum.setSortOrder(0); // 设置默认排序
        return forum;
    }

    /**
     * 创建主题
     * 管理员可以在指定论坛中创建新的主题，并设置主题的基本信息。
     * @param title 主题标题
     * @param content 主题内容
     * @param forumId 所属论坛ID
     * @param author 创建者（管理员）
     * @return Topic 创建的主题对象
     */
    @Override
    public Topic createTopic(String title, String content, int forumId, User author) {
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setContent(content);
        topic.setForumId(forumId);
        topic.setUserId(author.getUserId()); // 设置创建者ID
        topic.setCreateTime(LocalDateTime.now()); // 设置创建时间
        topic.setLastReplyTime(LocalDateTime.now()); // 初始化最后回复时间为当前时间
        topic.setLastReplyUserId(author.getUserId()); // 初始化最后回复用户为创建者
        topic.setReplyCount(0); // 初始化回复数量为0
        topic.setViewCount(0); // 初始化浏览数量为0
        topic.setStatus(Topic.TopicStatus.NORMAL); // 设置主题状态为正常
        topic.setPinned(false); // 默认不置顶
        topic.setLocked(false); // 默认不锁定
        return topic;
    }

    /**
     * 创建回复
     * 管理员可以在指定主题中创建新的回复，并设置回复的基本信息。
     * @param content 回复内容
     * @param topicId 所属主题ID
     * @param author 回复作者（管理员）
     * @return Reply 创建的回复对象
     */
    @Override
    public Reply createReply(String content, int topicId, User author) {
        Reply reply = new Reply();
        reply.setContent(content);
        reply.setTopicId(topicId);
        reply.setUserId(author.getUserId()); // 设置回复作者ID
        reply.setCreateTime(LocalDateTime.now()); // 设置创建时间
        reply.setStatus(Reply.ReplyStatus.NORMAL); // 设置回复状态为正常
        return reply;
    }

    /**
     * 检查管理员是否可以创建论坛
     * @param user 用户对象
     * @return boolean 是否可以创建论坛
     */
    @Override
    public boolean canCreateForum(User user) {
        return user != null && user.getRole() == User.UserRole.ADMIN 
               && user.getStatus() == User.UserStatus.ACTIVE;
    }

    /**
     * 检查管理员是否可以创建主题
     * @param user 用户对象
     * @param forumId 所属论坛ID
     * @return boolean 是否可以创建主题
     */
    @Override
    public boolean canCreateTopic(User user, int forumId) {
        return user != null && user.getRole() == User.UserRole.ADMIN 
               && user.getStatus() == User.UserStatus.ACTIVE;
    }

    /**
     * 检查管理员是否可以回复主题
     * @param user 用户对象
     * @param topicId 所属主题ID
     * @return boolean 是否可以回复主题
     */
    @Override
    public boolean canReply(User user, int topicId) {
        return user != null && user.getRole() == User.UserRole.ADMIN 
               && user.getStatus() == User.UserStatus.ACTIVE;
    }

    /**
     * 检查管理员是否可以管理论坛
     * @param user 用户对象
     * @param forumId 所属论坛ID
     * @return boolean 是否可以管理论坛
     */
    @Override
    public boolean canManageForum(User user, int forumId) {
        return user != null && user.getRole() == User.UserRole.ADMIN 
               && user.getStatus() == User.UserStatus.ACTIVE;
    }

    /**
     * 检查管理员是否可以管理主题
     * @param user 用户对象
     * @param topicId 所属主题ID
     * @return boolean 是否可以管理主题
     */
    @Override
    public boolean canManageTopic(User user, int topicId) {
        return user != null && user.getRole() == User.UserRole.ADMIN 
               && user.getStatus() == User.UserStatus.ACTIVE;
    }
}
