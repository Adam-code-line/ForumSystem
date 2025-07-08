package main.forumsystem.src.factory;

import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.Topic;
import main.forumsystem.src.entity.Reply;

/**
 * 用户操作工厂接口
 * 根据用户角色提供不同的操作权限
 */
public interface UserOperationFactory {
    
    /**
     * 创建板块对象（不保存到数据库）
     * @param forumName 板块名称
     * @param description 板块描述
     * @param creator 创建者
     * @return 板块对象
     */
    Forum createForum(String forumName, String description, User creator);
    
    /**
     * 创建主题对象（不保存到数据库）
     * @param title 主题标题
     * @param content 主题内容
     * @param forumId 所属板块ID
     * @param author 作者
     * @return 主题对象
     */
    Topic createTopic(String title, String content, int forumId, User author);
    
    /**
     * 创建回复对象（不保存到数据库）
     * @param content 回复内容
     * @param topicId 主题ID
     * @param author 作者
     * @return 回复对象
     */
    Reply createReply(String content, int topicId, User author);
    
    /**
     * 检查是否可以创建板块
     * @param user 用户
     * @return 是否有权限
     */
    boolean canCreateForum(User user);
    
    /**
     * 检查是否可以创建主题
     * @param user 用户
     * @param forumId 板块ID
     * @return 是否有权限
     */
    boolean canCreateTopic(User user, int forumId);
    
    /**
     * 检查是否可以回复
     * @param user 用户
     * @param topicId 主题ID
     * @return 是否有权限
     */
    boolean canReply(User user, int topicId);
    
    /**
     * 检查是否可以管理板块
     * @param user 用户
     * @param forumId 板块ID
     * @return 是否有权限
     */
    boolean canManageForum(User user, int forumId);
    
    /**
     * 检查是否可以管理主题
     * @param user 用户
     * @param topicId 主题ID
     * @return 是否有权限
     */
    boolean canManageTopic(User user, int topicId);
}
