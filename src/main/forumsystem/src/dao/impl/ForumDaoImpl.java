package main.forumsystem.src.dao.impl;

import main.forumsystem.src.dao.BaseDao;
import main.forumsystem.src.dao.ForumDao;
import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版块数据访问实现类
 */
public class ForumDaoImpl extends BaseDao implements ForumDao {

    @Override
    public boolean addForum(Forum forum) {
        String sql = """
            INSERT INTO forums (forum_name, description, moderator_id, topic_count, 
                              post_count, status, create_time, sort_order) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try {
            // 如果创建时间为空，设置为当前时间
            if (forum.getCreateTime() == null) {
                forum.setCreateTime(LocalDateTime.now());
            }
            
            // 如果排序值为0，设置为最大值+1
            if (forum.getSortOrder() == 0) {
                forum.setSortOrder(getMaxSortOrder() + 1);
            }
            
            int result = executeUpdate(sql,
                forum.getForumName(),
                forum.getDescription(),
                forum.getModeratorId() > 0 ? forum.getModeratorId() : null,
                forum.getTopicCount(),
                forum.getPostCount(),
                forum.getStatus().getValue(),
                Timestamp.valueOf(forum.getCreateTime()),
                forum.getSortOrder()
            );
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteForum(int forumId) {
        // 注意：删除版块前应该先处理该版块下的主题和回复
        String sql = "DELETE FROM forums WHERE forum_id = ?";
        try {
            int result = executeUpdate(sql, forumId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateForum(Forum forum) {
        String sql = """
            UPDATE forums SET forum_name = ?, description = ?, moderator_id = ?, 
                            topic_count = ?, post_count = ?, status = ?, sort_order = ? 
            WHERE forum_id = ?
            """;
        
        try {
            int result = executeUpdate(sql,
                forum.getForumName(),
                forum.getDescription(),
                forum.getModeratorId() > 0 ? forum.getModeratorId() : null,
                forum.getTopicCount(),
                forum.getPostCount(),
                forum.getStatus().getValue(),
                forum.getSortOrder(),
                forum.getForumId()
            );
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Forum getForumById(int forumId) {
        String sql = "SELECT * FROM forums WHERE forum_id = ?";
        return getSingleForum(sql, forumId);
    }

    @Override
    public Forum getForumWithModerator(int forumId) {
        String sql = """
            SELECT f.*, u.username as moderator_name, u.nick_name as moderator_nick
            FROM forums f 
            LEFT JOIN users u ON f.moderator_id = u.user_id
            WHERE f.forum_id = ?
            """;
        
        try {
            ResultSet rs = executeQuery(sql, forumId);
            if (rs != null && rs.next()) {
                Forum forum = mapResultSetToForum(rs);
                
                // 设置版主信息
                if (rs.getString("moderator_name") != null) {
                    User moderator = new User();
                    moderator.setUserId(forum.getModeratorId());
                    moderator.setUsername(rs.getString("moderator_name"));
                    moderator.setNickName(rs.getString("moderator_nick"));
                    forum.setModerator(moderator);
                }
                
                return forum;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Forum getForumByName(String forumName) {
        String sql = "SELECT * FROM forums WHERE forum_name = ?";
        return getSingleForum(sql, forumName);
    }

    @Override
    public List<Forum> getAllForums() {
        String sql = "SELECT * FROM forums ORDER BY sort_order ASC, forum_id ASC";
        return getMultipleForums(sql);
    }

    @Override
    public List<Forum> getActiveForums() {
        String sql = "SELECT * FROM forums WHERE status = 'active' ORDER BY sort_order ASC, forum_id ASC";
        return getMultipleForums(sql);
    }

    @Override
    public List<Forum> getForumsByPage(int page, int size) {
        String sql = "SELECT * FROM forums ORDER BY sort_order ASC, forum_id ASC LIMIT ? OFFSET ?";
        int offset = (page - 1) * size;
        return getMultipleForums(sql, size, offset);
    }

    @Override
    public List<Forum> getForumsByModerator(int moderatorId) {
        String sql = "SELECT * FROM forums WHERE moderator_id = ? AND status = 'active'";
        return getMultipleForums(sql, moderatorId);
    }

    @Override
    public List<Forum> getForumsByStatus(Forum.ForumStatus status) {
        String sql = "SELECT * FROM forums WHERE status = ? ORDER BY sort_order ASC";
        return getMultipleForums(sql, status.getValue());
    }

    @Override
    public List<Forum> searchForums(String keyword) {
        String sql = """
            SELECT * FROM forums 
            WHERE forum_name LIKE ? OR description LIKE ? 
            ORDER BY sort_order ASC
            """;
        String searchPattern = "%" + keyword + "%";
        return getMultipleForums(sql, searchPattern, searchPattern);
    }

    @Override
    public boolean changeForumStatus(int forumId, Forum.ForumStatus status) {
        String sql = "UPDATE forums SET status = ? WHERE forum_id = ?";
        try {
            int result = executeUpdate(sql, status.getValue(), forumId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean setForumModerator(int forumId, int moderatorId) {
        String sql = "UPDATE forums SET moderator_id = ? WHERE forum_id = ?";
        try {
            int result = executeUpdate(sql, moderatorId, forumId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateTopicCount(int forumId, int increment) {
        String sql = "UPDATE forums SET topic_count = topic_count + ? WHERE forum_id = ?";
        try {
            int result = executeUpdate(sql, increment, forumId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updatePostCount(int forumId, int increment) {
        String sql = "UPDATE forums SET post_count = GREATEST(0, post_count + ?) WHERE forum_id = ?";
        try {
            int result = executeUpdate(sql, increment, forumId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean recalculateForumStats(int forumId) {
        try {
            // 重新计算主题数
            String topicCountSql = """
                UPDATE forums SET topic_count = (
                    SELECT COUNT(*) FROM topics 
                    WHERE forum_id = ? AND status != 'deleted'
                ) WHERE forum_id = ?
                """;
            executeUpdate(topicCountSql, forumId, forumId);
            
            // 重新计算帖子数（主题数 + 回复数）
            String postCountSql = """
                UPDATE forums SET post_count = (
                    SELECT 
                        COALESCE((SELECT COUNT(*) FROM topics WHERE forum_id = ? AND status != 'deleted'), 0) +
                        COALESCE((SELECT COUNT(*) FROM replies r 
                                 JOIN topics t ON r.topic_id = t.topic_id 
                                 WHERE t.forum_id = ? AND r.status != 'deleted'), 0)
                ) WHERE forum_id = ?
                """;
            executeUpdate(postCountSql, forumId, forumId, forumId);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateSortOrder(int forumId, int sortOrder) {
        String sql = "UPDATE forums SET sort_order = ? WHERE forum_id = ?";
        try {
            int result = executeUpdate(sql, sortOrder, forumId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int batchUpdateSortOrder(int[] forumIds, int[] sortOrders) {
        if (forumIds == null || sortOrders == null || 
            forumIds.length == 0 || forumIds.length != sortOrders.length) {
            return 0;
        }

        int successCount = 0;
        String sql = "UPDATE forums SET sort_order = ? WHERE forum_id = ?";
        
        for (int i = 0; i < forumIds.length; i++) {
            try {
                int result = executeUpdate(sql, sortOrders[i], forumIds[i]);
                if (result > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return successCount;
    }

    @Override
    public int getForumCount() {
        String sql = "SELECT COUNT(*) as count FROM forums";
        return getCount(sql);
    }

    @Override
    public int getActiveForumCount() {
        String sql = "SELECT COUNT(*) as count FROM forums WHERE status = 'active'";
        return getCount(sql);
    }

    @Override
    public boolean forumNameExists(String forumName, int excludeId) {
        String sql = "SELECT COUNT(*) as count FROM forums WHERE forum_name = ? AND forum_id != ?";
        try {
            ResultSet rs = executeQuery(sql, forumName, excludeId);
            if (rs != null && rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Map<String, Object> getForumStatistics(int forumId) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            if (forumId == 0) {
                // 全站统计
                stats.put("totalForums", getForumCount());
                stats.put("activeForums", getActiveForumCount());
                
                // 总主题数
                String topicSql = "SELECT COUNT(*) as count FROM topics WHERE status != 'deleted'";
                stats.put("totalTopics", getCount(topicSql));
                
                // 总回复数
                String replySql = "SELECT COUNT(*) as count FROM replies WHERE status != 'deleted'";
                stats.put("totalReplies", getCount(replySql));
                
            } else {
                // 特定版块统计
                Forum forum = getForumById(forumId);
                if (forum != null) {
                    stats.put("topicCount", forum.getTopicCount());
                    stats.put("postCount", forum.getPostCount());
                    stats.put("status", forum.getStatus().getValue());
                    
                    // 今日新增主题
                    String todayTopicSql = """
                        SELECT COUNT(*) as count FROM topics 
                        WHERE forum_id = ? AND DATE(create_time) = CURDATE() AND status != 'deleted'
                        """;
                    stats.put("todayTopics", getCount(todayTopicSql, forumId));
                    
                    // 今日新增回复
                    String todayReplySql = """
                        SELECT COUNT(*) as count FROM replies r
                        JOIN topics t ON r.topic_id = t.topic_id
                        WHERE t.forum_id = ? AND DATE(r.create_time) = CURDATE() AND r.status != 'deleted'
                        """;
                    stats.put("todayReplies", getCount(todayReplySql, forumId));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }

    @Override
    public List<Forum> getHotForums(int limit) {
        String sql = """
            SELECT * FROM forums 
            WHERE status = 'active' 
            ORDER BY post_count DESC, topic_count DESC 
            LIMIT ?
            """;
        return getMultipleForums(sql, limit);
    }

    @Override
    public List<Forum> getLatestForums(int limit) {
        String sql = """
            SELECT * FROM forums 
            ORDER BY create_time DESC 
            LIMIT ?
            """;
        return getMultipleForums(sql, limit);
    }

    @Override
    public boolean isUserModerator(int userId, int forumId) {
        String sql = "SELECT COUNT(*) as count FROM forums WHERE moderator_id = ? AND forum_id = ?";
        return getCount(sql, userId, forumId) > 0;
    }

    @Override
    public List<Map<String, Object>> getLatestTopicsInfo(int forumId, int limit) {
        String sql = """
            SELECT t.topic_id, t.title, t.create_time, u.username as author
            FROM topics t
            LEFT JOIN users u ON t.user_id = u.user_id
            WHERE t.forum_id = ? AND t.status != 'deleted'
            ORDER BY t.create_time DESC
            LIMIT ?
            """;
        
        List<Map<String, Object>> topicsInfo = new ArrayList<>();
        
        try {
            ResultSet rs = executeQuery(sql, forumId, limit);
            while (rs != null && rs.next()) {
                Map<String, Object> topicInfo = new HashMap<>();
                topicInfo.put("topicId", rs.getInt("topic_id"));
                topicInfo.put("title", rs.getString("title"));
                topicInfo.put("createTime", rs.getTimestamp("create_time"));
                topicInfo.put("author", rs.getString("author"));
                topicsInfo.add(topicInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return topicsInfo;
    }

    @Override
    public int batchDeleteForums(int[] forumIds) {
        if (forumIds == null || forumIds.length == 0) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("DELETE FROM forums WHERE forum_id IN (");
        for (int i = 0; i < forumIds.length; i++) {
            sql.append("?");
            if (i < forumIds.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        try {
            Object[] params = new Object[forumIds.length];
            for (int i = 0; i < forumIds.length; i++) {
                params[i] = forumIds[i];
            }
            return executeUpdate(sql.toString(), params);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public List<Forum> getForumsByParent(int parentId) {
        // 这个方法预留给将来支持父子版块功能
        // 目前返回空列表
        return new ArrayList<>();
    }

    // 私有辅助方法：获取最大排序值
    private int getMaxSortOrder() {
        String sql = "SELECT COALESCE(MAX(sort_order), 0) as max_order FROM forums";
        try {
            ResultSet rs = executeQuery(sql);
            if (rs != null && rs.next()) {
                return rs.getInt("max_order");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 私有辅助方法：获取单个版块
    private Forum getSingleForum(String sql, Object... params) {
        try {
            ResultSet rs = executeQuery(sql, params);
            if (rs != null && rs.next()) {
                return mapResultSetToForum(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 私有辅助方法：获取多个版块
    private List<Forum> getMultipleForums(String sql, Object... params) {
        List<Forum> forums = new ArrayList<>();
        try {
            ResultSet rs = executeQuery(sql, params);
            while (rs != null && rs.next()) {
                forums.add(mapResultSetToForum(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return forums;
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

    // 私有辅助方法：将ResultSet映射为Forum对象
    private Forum mapResultSetToForum(ResultSet rs) throws SQLException {
        Forum forum = new Forum();
        forum.setForumId(rs.getInt("forum_id"));
        forum.setForumName(rs.getString("forum_name"));
        forum.setDescription(rs.getString("description"));
        forum.setModeratorId(rs.getInt("moderator_id"));
        forum.setTopicCount(rs.getInt("topic_count"));
        forum.setPostCount(rs.getInt("post_count"));
        forum.setStatus(Forum.ForumStatus.fromValue(rs.getString("status")));
        forum.setSortOrder(rs.getInt("sort_order"));

        // 处理创建时间
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            forum.setCreateTime(createTime.toLocalDateTime());
        }

        return forum;
    }
}
