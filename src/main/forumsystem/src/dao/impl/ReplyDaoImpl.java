package main.forumsystem.src.dao.impl;

import main.forumsystem.src.dao.BaseDao;
import main.forumsystem.src.dao.ReplyDao;
import main.forumsystem.src.entity.Reply;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.Topic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 回复数据访问实现类
 */
public class ReplyDaoImpl extends BaseDao implements ReplyDao {

    @Override
    public boolean addReply(Reply reply) {
        String sql = """
            INSERT INTO replies (topic_id, user_id, content, create_time, status, reply_to_id) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try {
            // 如果创建时间为空，设置为当前时间
            if (reply.getCreateTime() == null) {
                reply.setCreateTime(LocalDateTime.now());
            }
            
            int result = executeUpdate(sql,
                reply.getTopicId(),
                reply.getUserId(),
                reply.getContent(),
                Timestamp.valueOf(reply.getCreateTime()),
                reply.getStatus().getValue(),
                reply.getReplyToId() > 0 ? reply.getReplyToId() : null
            );
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteReply(int replyId) {
        String sql = "UPDATE replies SET status = 'deleted' WHERE reply_id = ?";
        try {
            int result = executeUpdate(sql, replyId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean hardDeleteReply(int replyId) {
        String sql = "DELETE FROM replies WHERE reply_id = ?";
        try {
            int result = executeUpdate(sql, replyId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateReply(Reply reply) {
        String sql = "UPDATE replies SET content = ?, status = ? WHERE reply_id = ?";
        
        try {
            int result = executeUpdate(sql,
                reply.getContent(),
                reply.getStatus().getValue(),
                reply.getReplyId()
            );
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Reply getReplyById(int replyId) {
        String sql = "SELECT * FROM replies WHERE reply_id = ?";
        return getSingleReply(sql, replyId);
    }

    @Override
    public Reply getReplyWithDetails(int replyId) {
        String sql = """
            SELECT r.*, u.username as author_name, u.nick_name as author_nick, 
                   t.title as topic_title, rt.content as reply_to_content, 
                   ru.username as reply_to_author
            FROM replies r 
            LEFT JOIN users u ON r.user_id = u.user_id
            LEFT JOIN topics t ON r.topic_id = t.topic_id
            LEFT JOIN replies rt ON r.reply_to_id = rt.reply_id
            LEFT JOIN users ru ON rt.user_id = ru.user_id
            WHERE r.reply_id = ?
            """;
        
        try {
            ResultSet rs = executeQuery(sql, replyId);
            if (rs != null && rs.next()) {
                Reply reply = mapResultSetToReply(rs);
                
                // 设置作者信息
                if (rs.getString("author_name") != null) {
                    User author = new User();
                    author.setUserId(reply.getUserId());
                    author.setUsername(rs.getString("author_name"));
                    author.setNickName(rs.getString("author_nick"));
                    reply.setAuthor(author);
                }
                
                // 设置主题信息
                if (rs.getString("topic_title") != null) {
                    Topic topic = new Topic();
                    topic.setTopicId(reply.getTopicId());
                    topic.setTitle(rs.getString("topic_title"));
                    reply.setTopic(topic);
                }
                
                // 设置被回复的回复信息
                if (reply.getReplyToId() > 0 && rs.getString("reply_to_content") != null) {
                    Reply replyTo = new Reply();
                    replyTo.setReplyId(reply.getReplyToId());
                    replyTo.setContent(rs.getString("reply_to_content"));
                    
                    User replyToAuthor = new User();
                    replyToAuthor.setUsername(rs.getString("reply_to_author"));
                    replyTo.setAuthor(replyToAuthor);
                    
                    reply.setReplyTo(replyTo);
                }
                
                return reply;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Reply> getRepliesByTopicId(int topicId) {
        String sql = """
            SELECT * FROM replies 
            WHERE topic_id = ? AND status != 'deleted' 
            ORDER BY create_time ASC
            """;
        return getMultipleReplies(sql, topicId);
    }

    @Override
    public List<Reply> getRepliesByTopicId(int topicId, int page, int size) {
        String sql = """
            SELECT * FROM replies 
            WHERE topic_id = ? AND status != 'deleted' 
            ORDER BY create_time ASC 
            LIMIT ? OFFSET ?
            """;
        int offset = (page - 1) * size;
        return getMultipleReplies(sql, topicId, size, offset);
    }

    @Override
    public List<Reply> getRepliesByUserId(int userId) {
        String sql = """
            SELECT * FROM replies 
            WHERE user_id = ? AND status != 'deleted' 
            ORDER BY create_time DESC
            """;
        return getMultipleReplies(sql, userId);
    }

    @Override
    public List<Reply> getRepliesByUserId(int userId, int page, int size) {
        String sql = """
            SELECT * FROM replies 
            WHERE user_id = ? AND status != 'deleted' 
            ORDER BY create_time DESC 
            LIMIT ? OFFSET ?
            """;
        int offset = (page - 1) * size;
        return getMultipleReplies(sql, userId, size, offset);
    }

    @Override
    public List<Reply> getChildReplies(int replyToId) {
        String sql = """
            SELECT * FROM replies 
            WHERE reply_to_id = ? AND status != 'deleted' 
            ORDER BY create_time ASC
            """;
        return getMultipleReplies(sql, replyToId);
    }

    @Override
    public List<Reply> getReplyTreeByTopicId(int topicId) {
        // 首先获取所有顶级回复（reply_to_id为NULL或0）
        String topLevelSql = """
            SELECT * FROM replies 
            WHERE topic_id = ? AND (reply_to_id IS NULL OR reply_to_id = 0) 
            AND status != 'deleted' 
            ORDER BY create_time ASC
            """;
        
        List<Reply> topLevelReplies = getMultipleReplies(topLevelSql, topicId);
        List<Reply> allReplies = new ArrayList<>();
        
        // 为每个顶级回复构建子回复树
        for (Reply topReply : topLevelReplies) {
            allReplies.add(topReply);
            addChildRepliesToList(topReply.getReplyId(), allReplies, 0);
        }
        
        return allReplies;
    }

    /**
     * 递归添加子回复到列表中
     */
    private void addChildRepliesToList(int parentReplyId, List<Reply> allReplies, int depth) {
        if (depth > 10) return; // 防止无限递归，限制最大深度
        
        List<Reply> childReplies = getChildReplies(parentReplyId);
        for (Reply childReply : childReplies) {
            allReplies.add(childReply);
            addChildRepliesToList(childReply.getReplyId(), allReplies, depth + 1);
        }
    }

    @Override
    public boolean changeReplyStatus(int replyId, Reply.ReplyStatus status) {
        String sql = "UPDATE replies SET status = ? WHERE reply_id = ?";
        try {
            int result = executeUpdate(sql, status.getValue(), replyId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int batchDeleteReplies(int[] replyIds) {
        if (replyIds == null || replyIds.length == 0) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("UPDATE replies SET status = 'deleted' WHERE reply_id IN (");
        for (int i = 0; i < replyIds.length; i++) {
            sql.append("?");
            if (i < replyIds.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        try {
            Object[] params = new Object[replyIds.length];
            for (int i = 0; i < replyIds.length; i++) {
                params[i] = replyIds[i];
            }
            return executeUpdate(sql.toString(), params);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int batchChangeReplyStatus(int[] replyIds, Reply.ReplyStatus status) {
        if (replyIds == null || replyIds.length == 0) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("UPDATE replies SET status = ? WHERE reply_id IN (");
        for (int i = 0; i < replyIds.length; i++) {
            sql.append("?");
            if (i < replyIds.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        try {
            Object[] params = new Object[replyIds.length + 1];
            params[0] = status.getValue();
            for (int i = 0; i < replyIds.length; i++) {
                params[i + 1] = replyIds[i];
            }
            return executeUpdate(sql.toString(), params);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public List<Reply> searchReplies(String keyword, int topicId) {
        String sql;
        String searchPattern = "%" + keyword + "%";
        
        if (topicId == 0) {
            sql = """
                SELECT * FROM replies 
                WHERE content LIKE ? AND status != 'deleted' 
                ORDER BY create_time DESC
                """;
            return getMultipleReplies(sql, searchPattern);
        } else {
            sql = """
                SELECT * FROM replies 
                WHERE topic_id = ? AND content LIKE ? AND status != 'deleted' 
                ORDER BY create_time DESC
                """;
            return getMultipleReplies(sql, topicId, searchPattern);
        }
    }

    @Override
    public List<Reply> advancedSearchReplies(String keyword, int topicId, int userId, 
                                           String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("SELECT * FROM replies WHERE status != 'deleted'");
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND content LIKE ?");
            params.add("%" + keyword + "%");
        }
        
        if (topicId > 0) {
            sql.append(" AND topic_id = ?");
            params.add(topicId);
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
        
        return getMultipleReplies(sql.toString(), params.toArray());
    }

    @Override
    public int getReplyCount(int topicId) {
        String sql = "SELECT COUNT(*) as count FROM replies WHERE topic_id = ? AND status != 'deleted'";
        return getCount(sql, topicId);
    }

    @Override
    public int getUserReplyCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM replies WHERE user_id = ? AND status != 'deleted'";
        return getCount(sql, userId);
    }

    @Override
    public int getTodayReplyCount(int topicId) {
        String sql;
        if (topicId == 0) {
            sql = "SELECT COUNT(*) as count FROM replies WHERE DATE(create_time) = CURDATE() AND status != 'deleted'";
            return getCount(sql);
        } else {
            sql = """
                SELECT COUNT(*) as count FROM replies 
                WHERE topic_id = ? AND DATE(create_time) = CURDATE() AND status != 'deleted'
                """;
            return getCount(sql, topicId);
        }
    }

    @Override
    public List<Reply> getLatestReplies(int limit) {
        String sql = """
            SELECT * FROM replies 
            WHERE status != 'deleted' 
            ORDER BY create_time DESC 
            LIMIT ?
            """;
        return getMultipleReplies(sql, limit);
    }

    @Override
    public List<Reply> getUserRepliesInTopic(int userId, int topicId) {
        String sql = """
            SELECT * FROM replies 
            WHERE user_id = ? AND topic_id = ? AND status != 'deleted' 
            ORDER BY create_time ASC
            """;
        return getMultipleReplies(sql, userId, topicId);
    }

    @Override
    public Map<String, Object> getReplyStatistics(int topicId) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 总回复数
            stats.put("totalReplies", getReplyCount(topicId));
            
            // 今日回复数
            stats.put("todayReplies", getTodayReplyCount(topicId));
            
            // 本周回复数
            String weekSql = topicId == 0 ? 
                "SELECT COUNT(*) as count FROM replies WHERE create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) AND status != 'deleted'" :
                "SELECT COUNT(*) as count FROM replies WHERE topic_id = ? AND create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) AND status != 'deleted'";
            stats.put("weekReplies", topicId == 0 ? getCount(weekSql) : getCount(weekSql, topicId));
            
            // 参与用户数
            String userSql = topicId == 0 ? 
                "SELECT COUNT(DISTINCT user_id) as count FROM replies WHERE status != 'deleted'" :
                "SELECT COUNT(DISTINCT user_id) as count FROM replies WHERE topic_id = ? AND status != 'deleted'";
            stats.put("participantCount", topicId == 0 ? getCount(userSql) : getCount(userSql, topicId));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }

    @Override
    public boolean canUserReplyToTopic(int userId, int topicId) {
        // 检查用户状态和主题状态
        String sql = """
            SELECT COUNT(*) as count FROM users u, topics t 
            WHERE u.user_id = ? AND t.topic_id = ? 
            AND u.status = 'active' AND t.status = 'normal' AND t.is_locked = false
            """;
        return getCount(sql, userId, topicId) > 0;
    }

    @Override
    public List<Reply> getRepliesByTimeRange(String startTime, String endTime, int limit) {
        String sql = """
            SELECT * FROM replies 
            WHERE create_time BETWEEN ? AND ? AND status != 'deleted' 
            ORDER BY create_time DESC 
            LIMIT ?
            """;
        return getMultipleReplies(sql, startTime, endTime, limit);
    }

    @Override
    public int deleteAllRepliesByTopicId(int topicId) {
        String sql = "UPDATE replies SET status = 'deleted' WHERE topic_id = ?";
        try {
            return executeUpdate(sql, topicId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getReplyDepth(int replyId) {
        String sql = """
            WITH RECURSIVE reply_tree AS (
                SELECT reply_id, reply_to_id, 0 as depth
                FROM replies 
                WHERE reply_id = ?
                
                UNION ALL
                
                SELECT r.reply_id, r.reply_to_id, rt.depth + 1
                FROM replies r
                INNER JOIN reply_tree rt ON r.reply_id = rt.reply_to_id
                WHERE rt.reply_to_id IS NOT NULL
            )
            SELECT MAX(depth) as max_depth FROM reply_tree
            """;
        
        try {
            ResultSet rs = executeQuery(sql, replyId);
            if (rs != null && rs.next()) {
                return rs.getInt("max_depth");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    @Override
    public List<Reply> getHotReplies(int topicId, int limit) {
        String sql = """
            SELECT r.*, COUNT(cr.reply_id) as child_count
            FROM replies r
            LEFT JOIN replies cr ON r.reply_id = cr.reply_to_id AND cr.status != 'deleted'
            WHERE r.topic_id = ? AND r.status != 'deleted'
            GROUP BY r.reply_id
            ORDER BY child_count DESC, r.create_time ASC
            LIMIT ?
            """;
        return getMultipleReplies(sql, topicId, limit);
    }

    // 私有辅助方法：获取单个回复
    private Reply getSingleReply(String sql, Object... params) {
        try {
            ResultSet rs = executeQuery(sql, params);
            if (rs != null && rs.next()) {
                return mapResultSetToReply(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 私有辅助方法：获取多个回复
    private List<Reply> getMultipleReplies(String sql, Object... params) {
        List<Reply> replies = new ArrayList<>();
        try {
            ResultSet rs = executeQuery(sql, params);
            while (rs != null && rs.next()) {
                replies.add(mapResultSetToReply(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return replies;
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

    // 私有辅助方法：将ResultSet映射为Reply对象
    private Reply mapResultSetToReply(ResultSet rs) throws SQLException {
        Reply reply = new Reply();
        reply.setReplyId(rs.getInt("reply_id"));
        reply.setTopicId(rs.getInt("topic_id"));
        reply.setUserId(rs.getInt("user_id"));
        reply.setContent(rs.getString("content"));
        reply.setStatus(Reply.ReplyStatus.fromValue(rs.getString("status")));
        
        // 处理reply_to_id，可能为NULL
        int replyToId = rs.getInt("reply_to_id");
        if (!rs.wasNull()) {
            reply.setReplyToId(replyToId);
        } else {
            reply.setReplyToId(0);
        }

        // 处理创建时间
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            reply.setCreateTime(createTime.toLocalDateTime());
        }

        return reply;
    }
}
