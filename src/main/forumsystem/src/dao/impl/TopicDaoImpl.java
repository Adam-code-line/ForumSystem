package main.forumsystem.src.dao.impl;

import main.forumsystem.src.dao.BaseDao;
import main.forumsystem.src.dao.TopicDao;
import main.forumsystem.src.entity.Topic;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.Forum;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主题帖数据访问实现类
 */
public class TopicDaoImpl extends BaseDao implements TopicDao {

    @Override
    public boolean addTopic(Topic topic) {
        String sql = """
            INSERT INTO topics (forum_id, user_id, title, content, is_pinned, is_locked, 
                              view_count, reply_count, create_time, status) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try {
            // 如果创建时间为空，设置为当前时间
            if (topic.getCreateTime() == null) {
                topic.setCreateTime(LocalDateTime.now());
            }
            
            int result = executeUpdate(sql,
                topic.getForumId(),
                topic.getUserId(),
                topic.getTitle(),
                topic.getContent(),
                topic.isPinned(),
                topic.isLocked(),
                topic.getViewCount(),
                topic.getReplyCount(),
                Timestamp.valueOf(topic.getCreateTime()),
                topic.getStatus().getValue()
            );
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteTopic(int topicId) {
        String sql = "UPDATE topics SET status = 'deleted' WHERE topic_id = ?";
        try {
            int result = executeUpdate(sql, topicId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateTopic(Topic topic) {
        String sql = """
            UPDATE topics SET title = ?, content = ?, is_pinned = ?, is_locked = ?, 
                            view_count = ?, reply_count = ?, status = ? 
            WHERE topic_id = ?
            """;
        
        try {
            int result = executeUpdate(sql,
                topic.getTitle(),
                topic.getContent(),
                topic.isPinned(),
                topic.isLocked(),
                topic.getViewCount(),
                topic.getReplyCount(),
                topic.getStatus().getValue(),
                topic.getTopicId()
            );
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Topic getTopicById(int topicId) {
        String sql = "SELECT * FROM topics WHERE topic_id = ? AND status != 'deleted'";
        return getSingleTopic(sql, topicId);
    }

    @Override
    public Topic getTopicWithDetails(int topicId) {
        String sql = """
            SELECT t.*, u.username as author_name, u.nick_name as author_nick, 
                   f.forum_name, lr.username as last_reply_name
            FROM topics t 
            LEFT JOIN users u ON t.user_id = u.user_id
            LEFT JOIN forums f ON t.forum_id = f.forum_id
            LEFT JOIN users lr ON t.last_reply_user_id = lr.user_id
            WHERE t.topic_id = ? AND t.status != 'deleted'
            """;
        
        try {
            ResultSet rs = executeQuery(sql, topicId);
            if (rs != null && rs.next()) {
                Topic topic = mapResultSetToTopic(rs);
                
                // 设置关联对象信息
                if (rs.getString("author_name") != null) {
                    User author = new User();
                    author.setUserId(topic.getUserId());
                    author.setUsername(rs.getString("author_name"));
                    author.setNickName(rs.getString("author_nick"));
                    topic.setAuthor(author);
                }
                
                if (rs.getString("forum_name") != null) {
                    Forum forum = new Forum();
                    forum.setForumId(topic.getForumId());
                    forum.setForumName(rs.getString("forum_name"));
                    topic.setForum(forum);
                }
                
                if (rs.getString("last_reply_name") != null) {
                    User lastReplyUser = new User();
                    lastReplyUser.setUserId(topic.getLastReplyUserId());
                    lastReplyUser.setUsername(rs.getString("last_reply_name"));
                    topic.setLastReplyUser(lastReplyUser);
                }
                
                return topic;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Topic> getTopicsByForumId(int forumId) {
        String sql = """
            SELECT * FROM topics 
            WHERE forum_id = ? AND status = 'normal' 
            ORDER BY is_pinned DESC, last_reply_time DESC, create_time DESC
            """;
        return getMultipleTopics(sql, forumId);
    }

    @Override
    public List<Topic> getTopicsByUserId(int userId) {
        String sql = """
            SELECT * FROM topics 
            WHERE user_id = ? AND status != 'deleted' 
            ORDER BY create_time DESC
            """;
        return getMultipleTopics(sql, userId);
    }

    @Override
    public List<Topic> getTopicsByPage(int forumId, int page, int size, String orderBy, boolean isDesc) {
        String order = isDesc ? "DESC" : "ASC";
        String sql = String.format("""
            SELECT * FROM topics 
            WHERE forum_id = ? AND status = 'normal' 
            ORDER BY is_pinned DESC, %s %s 
            LIMIT ? OFFSET ?
            """, orderBy, order);
        
        int offset = (page - 1) * size;
        return getMultipleTopics(sql, forumId, size, offset);
    }

    @Override
    public List<Topic> getPinnedTopics(int forumId) {
        String sql;
        if (forumId == 0) {
            sql = "SELECT * FROM topics WHERE is_pinned = true AND status = 'normal' ORDER BY create_time DESC";
            return getMultipleTopics(sql);
        } else {
            sql = """
                SELECT * FROM topics 
                WHERE forum_id = ? AND is_pinned = true AND status = 'normal' 
                ORDER BY create_time DESC
                """;
            return getMultipleTopics(sql, forumId);
        }
    }

    @Override
    public List<Topic> searchTopics(String keyword, int forumId) {
        String sql;
        String searchPattern = "%" + keyword + "%";
        
        if (forumId == 0) {
            sql = """
                SELECT * FROM topics 
                WHERE (title LIKE ? OR content LIKE ?) AND status = 'normal' 
                ORDER BY create_time DESC
                """;
            return getMultipleTopics(sql, searchPattern, searchPattern);
        } else {
            sql = """
                SELECT * FROM topics 
                WHERE forum_id = ? AND (title LIKE ? OR content LIKE ?) AND status = 'normal' 
                ORDER BY create_time DESC
                """;
            return getMultipleTopics(sql, forumId, searchPattern, searchPattern);
        }
    }

    @Override
    public List<Topic> advancedSearchTopics(String keyword, int forumId, int userId, 
                                          String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("SELECT * FROM topics WHERE status = 'normal'");
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (title LIKE ? OR content LIKE ?)");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (forumId > 0) {
            sql.append(" AND forum_id = ?");
            params.add(forumId);
        }
        
        if (userId > 0) {
            sql.append(" AND user_id = ?");
            params.add(userId);
        }
        
        if (startTime != null && !startTime.trim().isEmpty()) {
            sql.append(" AND create_time >= ?");
            params.add(startTime);
        }
        
        if (endTime != null && !endTime.trim().isEmpty()) {
            sql.append(" AND create_time <= ?");
            params.add(endTime);
        }
        
        sql.append(" ORDER BY create_time DESC");
        
        return getMultipleTopics(sql.toString(), params.toArray());
    }

    @Override
    public boolean incrementViewCount(int topicId) {
        String sql = "UPDATE topics SET view_count = view_count + 1 WHERE topic_id = ?";
        try {
            int result = executeUpdate(sql, topicId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateReplyCount(int topicId, int increment) {
        String sql = "UPDATE topics SET reply_count = reply_count + ? WHERE topic_id = ?";
        try {
            int result = executeUpdate(sql, increment, topicId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateLastReplyInfo(int topicId, int lastReplyUserId) {
        String sql = """
            UPDATE topics SET last_reply_time = ?, last_reply_user_id = ? 
            WHERE topic_id = ?
            """;
        try {
            int result = executeUpdate(sql, 
                Timestamp.valueOf(LocalDateTime.now()), 
                lastReplyUserId, 
                topicId
            );
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean pinTopic(int topicId, boolean isPinned) {
        String sql = "UPDATE topics SET is_pinned = ? WHERE topic_id = ?";
        try {
            int result = executeUpdate(sql, isPinned, topicId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean lockTopic(int topicId, boolean isLocked) {
        String sql = "UPDATE topics SET is_locked = ? WHERE topic_id = ?";
        try {
            int result = executeUpdate(sql, isLocked, topicId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changeTopicStatus(int topicId, Topic.TopicStatus status) {
        String sql = "UPDATE topics SET status = ? WHERE topic_id = ?";
        try {
            int result = executeUpdate(sql, status.getValue(), topicId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean moveTopic(int topicId, int newForumId) {
        String sql = "UPDATE topics SET forum_id = ? WHERE topic_id = ?";
        try {
            int result = executeUpdate(sql, newForumId, topicId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Topic> getHotTopics(int limit, int days) {
        String sql;
        if (days > 0) {
            sql = """
                SELECT * FROM topics 
                WHERE status = 'normal' AND create_time >= DATE_SUB(NOW(), INTERVAL ? DAY)
                ORDER BY view_count DESC, reply_count DESC 
                LIMIT ?
                """;
            return getMultipleTopics(sql, days, limit);
        } else {
            sql = """
                SELECT * FROM topics 
                WHERE status = 'normal' 
                ORDER BY view_count DESC, reply_count DESC 
                LIMIT ?
                """;
            return getMultipleTopics(sql, limit);
        }
    }

    @Override
    public List<Topic> getLatestTopics(int limit) {
        String sql = """
            SELECT * FROM topics 
            WHERE status = 'normal' 
            ORDER BY create_time DESC 
            LIMIT ?
            """;
        return getMultipleTopics(sql, limit);
    }

    @Override
    public List<Topic> getFeaturedTopics(int limit, int minReplies) {
        String sql = """
            SELECT * FROM topics 
            WHERE status = 'normal' AND reply_count >= ? 
            ORDER BY reply_count DESC, view_count DESC 
            LIMIT ?
            """;
        return getMultipleTopics(sql, minReplies, limit);
    }

    @Override
    public int getTopicCount(int forumId) {
        String sql;
        if (forumId == 0) {
            sql = "SELECT COUNT(*) as count FROM topics WHERE status != 'deleted'";
            return getCount(sql);
        } else {
            sql = "SELECT COUNT(*) as count FROM topics WHERE forum_id = ? AND status != 'deleted'";
            return getCount(sql, forumId);
        }
    }

    @Override
    public int getUserTopicCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM topics WHERE user_id = ? AND status != 'deleted'";
        return getCount(sql, userId);
    }

    @Override
    public int getTodayTopicCount(int forumId) {
        String sql;
        if (forumId == 0) {
            sql = "SELECT COUNT(*) as count FROM topics WHERE DATE(create_time) = CURDATE() AND status != 'deleted'";
            return getCount(sql);
        } else {
            sql = """
                SELECT COUNT(*) as count FROM topics 
                WHERE forum_id = ? AND DATE(create_time) = CURDATE() AND status != 'deleted'
                """;
            return getCount(sql, forumId);
        }
    }

    @Override
    public int batchDeleteTopics(int[] topicIds) {
        if (topicIds == null || topicIds.length == 0) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("UPDATE topics SET status = 'deleted' WHERE topic_id IN (");
        for (int i = 0; i < topicIds.length; i++) {
            sql.append("?");
            if (i < topicIds.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        try {
            Object[] params = new Object[topicIds.length];
            for (int i = 0; i < topicIds.length; i++) {
                params[i] = topicIds[i];
            }
            return executeUpdate(sql.toString(), params);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int batchMoveTopics(int[] topicIds, int newForumId) {
        if (topicIds == null || topicIds.length == 0) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("UPDATE topics SET forum_id = ? WHERE topic_id IN (");
        for (int i = 0; i < topicIds.length; i++) {
            sql.append("?");
            if (i < topicIds.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        try {
            Object[] params = new Object[topicIds.length + 1];
            params[0] = newForumId;
            for (int i = 0; i < topicIds.length; i++) {
                params[i + 1] = topicIds[i];
            }
            return executeUpdate(sql.toString(), params);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Map<String, Object> getTopicStatistics(int forumId) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 总主题数
            stats.put("totalTopics", getTopicCount(forumId));
            
            // 今日主题数
            stats.put("todayTopics", getTodayTopicCount(forumId));
            
            // 本周主题数
            String weekSql = forumId == 0 ? 
                "SELECT COUNT(*) as count FROM topics WHERE create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) AND status != 'deleted'" :
                "SELECT COUNT(*) as count FROM topics WHERE forum_id = ? AND create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) AND status != 'deleted'";
            stats.put("weekTopics", forumId == 0 ? getCount(weekSql) : getCount(weekSql, forumId));
            
            // 本月主题数
            String monthSql = forumId == 0 ? 
                "SELECT COUNT(*) as count FROM topics WHERE create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) AND status != 'deleted'" :
                "SELECT COUNT(*) as count FROM topics WHERE forum_id = ? AND create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) AND status != 'deleted'";
            stats.put("monthTopics", forumId == 0 ? getCount(monthSql) : getCount(monthSql, forumId));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }

    @Override
    public List<Topic> getUserTopicsInForum(int userId, int forumId, int page, int size) {
        String sql = """
            SELECT * FROM topics 
            WHERE user_id = ? AND forum_id = ? AND status != 'deleted' 
            ORDER BY create_time DESC 
            LIMIT ? OFFSET ?
            """;
        int offset = (page - 1) * size;
        return getMultipleTopics(sql, userId, forumId, size, offset);
    }

    @Override
    public boolean canUserPostInForum(int userId, int forumId) {
        // 这里可以根据业务需求实现权限检查
        // 比如检查用户状态、版块权限等
        String sql = """
            SELECT COUNT(*) as count FROM users u 
            LEFT JOIN forums f ON f.forum_id = ? 
            WHERE u.user_id = ? AND u.status = 'active'
            """;
        return getCount(sql, forumId, userId) > 0;
    }

    @Override
    public List<Topic> getRelatedTopics(int topicId, int limit) {
        // 获取当前主题的标题关键词，查找相关主题
        String sql = """
            SELECT t2.* FROM topics t1, topics t2 
            WHERE t1.topic_id = ? AND t2.topic_id != ? 
            AND t2.status = 'normal' 
            AND (t2.title LIKE CONCAT('%', SUBSTRING(t1.title, 1, 10), '%') 
                 OR t2.forum_id = t1.forum_id)
            ORDER BY t2.reply_count DESC, t2.view_count DESC 
            LIMIT ?
            """;
        return getMultipleTopics(sql, topicId, topicId, limit);
    }

    // 私有辅助方法：获取单个主题
    private Topic getSingleTopic(String sql, Object... params) {
        try {
            ResultSet rs = executeQuery(sql, params);
            if (rs != null && rs.next()) {
                return mapResultSetToTopic(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 私有辅助方法：获取多个主题
    private List<Topic> getMultipleTopics(String sql, Object... params) {
        List<Topic> topics = new ArrayList<>();
        try {
            ResultSet rs = executeQuery(sql, params);
            while (rs != null && rs.next()) {
                topics.add(mapResultSetToTopic(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topics;
    }

    // 私有辅助方法：获取数量
    private int getCount(String sql, Object... params) {
        try {
            ResultSet rs = executeQuery(sql, params);
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 私有辅助方法：将ResultSet映射为Topic对象
    private Topic mapResultSetToTopic(ResultSet rs) throws SQLException {
        Topic topic = new Topic();
        topic.setTopicId(rs.getInt("topic_id"));
        topic.setForumId(rs.getInt("forum_id"));
        topic.setUserId(rs.getInt("user_id"));
        topic.setTitle(rs.getString("title"));
        topic.setContent(rs.getString("content"));
        topic.setPinned(rs.getBoolean("is_pinned"));
        topic.setLocked(rs.getBoolean("is_locked"));
        topic.setViewCount(rs.getInt("view_count"));
        topic.setReplyCount(rs.getInt("reply_count"));
        topic.setLastReplyUserId(rs.getInt("last_reply_user_id"));
        topic.setStatus(Topic.TopicStatus.fromValue(rs.getString("status")));

        // 处理时间字段
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            topic.setCreateTime(createTime.toLocalDateTime());
        }

        Timestamp lastReplyTime = rs.getTimestamp("last_reply_time");
        if (lastReplyTime != null) {
            topic.setLastReplyTime(lastReplyTime.toLocalDateTime());
        }

        return topic;
    }
}
