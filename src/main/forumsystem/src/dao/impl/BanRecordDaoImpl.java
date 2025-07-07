package main.forumsystem.src.dao.impl;

import main.forumsystem.src.dao.BaseDao;
import main.forumsystem.src.dao.BanRecordDao;
import main.forumsystem.src.entity.BanRecord;
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
 * 封禁记录数据访问实现类
 */
public class BanRecordDaoImpl extends BaseDao implements BanRecordDao {

    @Override
    public boolean addBanRecord(BanRecord banRecord) {
        String sql = """
            INSERT INTO ban_records (user_id, admin_id, reason, ban_start, ban_end, 
                                   is_permanent, status) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        
        try {
            // 如果封禁开始时间为空，设置为当前时间
            if (banRecord.getBanStart() == null) {
                banRecord.setBanStart(LocalDateTime.now());
            }
            
            int result = executeUpdate(sql,
                banRecord.getUserId(),
                banRecord.getAdminId(),
                banRecord.getReason(),
                Timestamp.valueOf(banRecord.getBanStart()),
                banRecord.getBanEnd() != null ? Timestamp.valueOf(banRecord.getBanEnd()) : null,
                banRecord.isPermanent(),
                banRecord.getStatus().getValue()
            );
            
            // 如果添加成功，同时更新用户状态为封禁
            if (result > 0) {
                updateUserBanStatus(banRecord.getUserId(), true);
            }
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteBanRecord(int banId) {
        String sql = "DELETE FROM ban_records WHERE ban_id = ?";
        try {
            int result = executeUpdate(sql, banId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateBanRecord(BanRecord banRecord) {
        String sql = """
            UPDATE ban_records SET user_id = ?, admin_id = ?, reason = ?, 
                                 ban_start = ?, ban_end = ?, is_permanent = ?, status = ? 
            WHERE ban_id = ?
            """;
        
        try {
            int result = executeUpdate(sql,
                banRecord.getUserId(),
                banRecord.getAdminId(),
                banRecord.getReason(),
                Timestamp.valueOf(banRecord.getBanStart()),
                banRecord.getBanEnd() != null ? Timestamp.valueOf(banRecord.getBanEnd()) : null,
                banRecord.isPermanent(),
                banRecord.getStatus().getValue(),
                banRecord.getBanId()
            );
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public BanRecord getBanRecordById(int banId) {
        String sql = "SELECT * FROM ban_records WHERE ban_id = ?";
        return getSingleBanRecord(sql, banId);
    }

    @Override
    public BanRecord getBanRecordWithDetails(int banId) {
        String sql = """
            SELECT br.*, u.username as user_name, u.nick_name as user_nick,
                   a.username as admin_name, a.nick_name as admin_nick
            FROM ban_records br
            LEFT JOIN users u ON br.user_id = u.user_id
            LEFT JOIN users a ON br.admin_id = a.user_id
            WHERE br.ban_id = ?
            """;
        
        try {
            ResultSet rs = executeQuery(sql, banId);
            if (rs != null && rs.next()) {
                BanRecord banRecord = mapResultSetToBanRecord(rs);
                
                // 设置用户信息
                if (rs.getString("user_name") != null) {
                    User user = new User();
                    user.setUserId(banRecord.getUserId());
                    user.setUsername(rs.getString("user_name"));
                    user.setNickName(rs.getString("user_nick"));
                    banRecord.setUser(user);
                }
                
                // 设置管理员信息
                if (rs.getString("admin_name") != null) {
                    User admin = new User();
                    admin.setUserId(banRecord.getAdminId());
                    admin.setUsername(rs.getString("admin_name"));
                    admin.setNickName(rs.getString("admin_nick"));
                    banRecord.setAdmin(admin);
                }
                
                return banRecord;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<BanRecord> getBanRecordsByUserId(int userId) {
        String sql = """
            SELECT * FROM ban_records 
            WHERE user_id = ? 
            ORDER BY ban_start DESC
            """;
        return getMultipleBanRecords(sql, userId);
    }

    @Override
    public BanRecord getCurrentBanRecord(int userId) {
        String sql = """
            SELECT * FROM ban_records 
            WHERE user_id = ? AND status = 'active' 
            AND (is_permanent = true OR ban_end > NOW())
            ORDER BY ban_start DESC 
            LIMIT 1
            """;
        return getSingleBanRecord(sql, userId);
    }

    @Override
    public List<BanRecord> getBanRecordsByAdminId(int adminId) {
        String sql = """
            SELECT * FROM ban_records 
            WHERE admin_id = ? 
            ORDER BY ban_start DESC
            """;
        return getMultipleBanRecords(sql, adminId);
    }

    @Override
    public List<BanRecord> getBanRecordsByPage(int page, int size) {
        String sql = """
            SELECT * FROM ban_records 
            ORDER BY ban_start DESC 
            LIMIT ? OFFSET ?
            """;
        int offset = (page - 1) * size;
        return getMultipleBanRecords(sql, size, offset);
    }

    @Override
    public List<BanRecord> getBanRecordsByStatus(BanRecord.BanStatus status) {
        String sql = """
            SELECT * FROM ban_records 
            WHERE status = ? 
            ORDER BY ban_start DESC
            """;
        return getMultipleBanRecords(sql, status.getValue());
    }

    @Override
    public List<BanRecord> getActiveBanRecords() {
        String sql = """
            SELECT * FROM ban_records 
            WHERE status = 'active' 
            AND (is_permanent = true OR ban_end > NOW())
            ORDER BY ban_start DESC
            """;
        return getMultipleBanRecords(sql);
    }

    @Override
    public List<BanRecord> getExpiredBanRecords() {
        String sql = """
            SELECT * FROM ban_records 
            WHERE status = 'active' 
            AND is_permanent = false 
            AND ban_end <= NOW()
            ORDER BY ban_end ASC
            """;
        return getMultipleBanRecords(sql);
    }

    @Override
    public boolean liftUserBan(int userId, int adminId) {
        try {
            // 更新所有该用户的活跃封禁记录为已解除状态
            String sql = "UPDATE ban_records SET status = 'lifted' WHERE user_id = ? AND status = 'active'";
            int result = executeUpdate(sql, userId);
            
            // 更新用户状态为正常
            if (result > 0) {
                updateUserBanStatus(userId, false);
            }
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean liftBanRecord(int banId, int adminId) {
        try {
            // 获取封禁记录
            BanRecord banRecord = getBanRecordById(banId);
            if (banRecord == null) {
                return false;
            }
            
            // 更新封禁记录状态
            String sql = "UPDATE ban_records SET status = 'lifted' WHERE ban_id = ?";
            int result = executeUpdate(sql, banId);
            
            // 检查该用户是否还有其他活跃的封禁记录
            if (result > 0) {
                BanRecord currentBan = getCurrentBanRecord(banRecord.getUserId());
                if (currentBan == null) {
                    // 没有其他活跃封禁，解除用户封禁状态
                    updateUserBanStatus(banRecord.getUserId(), false);
                }
            }
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isUserBanned(int userId) {
        BanRecord currentBan = getCurrentBanRecord(userId);
        return currentBan != null;
    }

    @Override
    public List<BanRecord> searchBanRecords(String keyword) {
        String sql = """
            SELECT br.* FROM ban_records br
            LEFT JOIN users u ON br.user_id = u.user_id
            WHERE u.username LIKE ? OR br.reason LIKE ?
            ORDER BY br.ban_start DESC
            """;
        String searchPattern = "%" + keyword + "%";
        return getMultipleBanRecords(sql, searchPattern, searchPattern);
    }

    @Override
    public List<BanRecord> advancedSearchBanRecords(Integer userId, Integer adminId,
                                                   BanRecord.BanStatus status,
                                                   String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ban_records WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (userId != null && userId > 0) {
            sql.append(" AND user_id = ?");
            params.add(userId);
        }
        
        if (adminId != null && adminId > 0) {
            sql.append(" AND admin_id = ?");
            params.add(adminId);
        }
        
        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status.getValue());
        }
        
        if (startTime != null && !startTime.trim().isEmpty()) {
            sql.append(" AND ban_start >= ?");
            params.add(startTime);
        }
        
        if (endTime != null && !endTime.trim().isEmpty()) {
            sql.append(" AND ban_start <= ?");
            params.add(endTime);
        }
        
        sql.append(" ORDER BY ban_start DESC");
        
        return getMultipleBanRecords(sql.toString(), params.toArray());
    }

    @Override
    public int batchLiftBans(int[] banIds, int adminId) {
        if (banIds == null || banIds.length == 0) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("UPDATE ban_records SET status = 'lifted' WHERE ban_id IN (");
        for (int i = 0; i < banIds.length; i++) {
            sql.append("?");
            if (i < banIds.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        try {
            Object[] params = new Object[banIds.length];
            for (int i = 0; i < banIds.length; i++) {
                params[i] = banIds[i];
            }
            
            int result = executeUpdate(sql.toString(), params);
            
            // 批量检查并更新用户状态
            if (result > 0) {
                updateUsersAfterBatchLift(banIds);
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int processExpiredBans() {
        try {
            // 获取过期的封禁记录
            List<BanRecord> expiredBans = getExpiredBanRecords();
            
            if (expiredBans.isEmpty()) {
                return 0;
            }
            
            // 更新过期封禁记录状态
            String sql = """
                UPDATE ban_records SET status = 'lifted' 
                WHERE status = 'active' AND is_permanent = false AND ban_end <= NOW()
                """;
            int updatedRecords = executeUpdate(sql);
            
            // 更新用户状态
            for (BanRecord banRecord : expiredBans) {
                // 检查用户是否还有其他活跃封禁
                BanRecord currentBan = getCurrentBanRecord(banRecord.getUserId());
                if (currentBan == null) {
                    updateUserBanStatus(banRecord.getUserId(), false);
                }
            }
            
            return updatedRecords;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Map<String, Object> getBanStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 总封禁记录数
            stats.put("totalBanRecords", getBanRecordCount());
            
            // 当前封禁用户数
            stats.put("currentBannedUsers", getCurrentBannedUserCount());
            
            // 今日封禁数
            stats.put("todayBans", getTodayBanCount());
            
            // 本周封禁数
            String weekSql = """
                SELECT COUNT(*) as count FROM ban_records 
                WHERE ban_start >= DATE_SUB(NOW(), INTERVAL 7 DAY)
                """;
            stats.put("weekBans", getCount(weekSql));
            
            // 本月封禁数
            String monthSql = """
                SELECT COUNT(*) as count FROM ban_records 
                WHERE ban_start >= DATE_SUB(NOW(), INTERVAL 30 DAY)
                """;
            stats.put("monthBans", getCount(monthSql));
            
            // 永久封禁数
            String permanentSql = """
                SELECT COUNT(*) as count FROM ban_records 
                WHERE is_permanent = true AND status = 'active'
                """;
            stats.put("permanentBans", getCount(permanentSql));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }

    @Override
    public Map<String, Object> getUserBanHistory(int userId) {
        Map<String, Object> history = new HashMap<>();
        
        try {
            // 总封禁次数
            String totalSql = "SELECT COUNT(*) as count FROM ban_records WHERE user_id = ?";
            history.put("totalBans", getCount(totalSql, userId));
            
            // 当前是否被封禁
            history.put("currentlyBanned", isUserBanned(userId));
            
            // 最近一次封禁记录
            List<BanRecord> recentBans = getBanRecordsByUserId(userId);
            if (!recentBans.isEmpty()) {
                history.put("lastBanRecord", recentBans.get(0));
            }
            
            // 封禁记录列表
            history.put("banRecords", recentBans);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return history;
    }

    @Override
    public Map<String, Object> getAdminBanStats(int adminId) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 总执行封禁数
            String totalSql = "SELECT COUNT(*) as count FROM ban_records WHERE admin_id = ?";
            stats.put("totalBansExecuted", getCount(totalSql, adminId));
            
            // 今日执行封禁数
            String todaySql = """
                SELECT COUNT(*) as count FROM ban_records 
                WHERE admin_id = ? AND DATE(ban_start) = CURDATE()
                """;
            stats.put("todayBansExecuted", getCount(todaySql, adminId));
            
            // 本月执行封禁数
            String monthSql = """
                SELECT COUNT(*) as count FROM ban_records 
                WHERE admin_id = ? AND ban_start >= DATE_SUB(NOW(), INTERVAL 30 DAY)
                """;
            stats.put("monthBansExecuted", getCount(monthSql, adminId));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return stats;
    }

    @Override
    public int getBanRecordCount() {
        String sql = "SELECT COUNT(*) as count FROM ban_records";
        return getCount(sql);
    }

    @Override
    public int getCurrentBannedUserCount() {
        String sql = """
            SELECT COUNT(DISTINCT user_id) as count FROM ban_records 
            WHERE status = 'active' 
            AND (is_permanent = true OR ban_end > NOW())
            """;
        return getCount(sql);
    }

    @Override
    public int getTodayBanCount() {
        String sql = """
            SELECT COUNT(*) as count FROM ban_records 
            WHERE DATE(ban_start) = CURDATE()
            """;
        return getCount(sql);
    }

    @Override
    public List<BanRecord> getBanRecordsByTimeRange(String startTime, String endTime) {
        String sql = """
            SELECT * FROM ban_records 
            WHERE ban_start BETWEEN ? AND ? 
            ORDER BY ban_start DESC
            """;
        return getMultipleBanRecords(sql, startTime, endTime);
    }

    @Override
    public List<BanRecord> getExpiringBanRecords() {
        String sql = """
            SELECT * FROM ban_records 
            WHERE status = 'active' 
            AND is_permanent = false 
            AND ban_end BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 24 HOUR)
            ORDER BY ban_end ASC
            """;
        return getMultipleBanRecords(sql);
    }

    @Override
    public boolean extendBanTime(int banId, LocalDateTime newEndTime) {
        String sql = "UPDATE ban_records SET ban_end = ? WHERE ban_id = ?";
        try {
            int result = executeUpdate(sql, Timestamp.valueOf(newEndTime), banId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateBanReason(int banId, String newReason) {
        String sql = "UPDATE ban_records SET reason = ? WHERE ban_id = ?";
        try {
            int result = executeUpdate(sql, newReason, banId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<BanRecord> getRecentBanRecords(int limit) {
        String sql = """
            SELECT * FROM ban_records 
            ORDER BY ban_start DESC 
            LIMIT ?
            """;
        return getMultipleBanRecords(sql, limit);
    }

    // 私有辅助方法：更新用户封禁状态
    private void updateUserBanStatus(int userId, boolean isBanned) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try {
            String status = isBanned ? "banned" : "active";
            executeUpdate(sql, status, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 私有辅助方法：批量解封后更新用户状态
    private void updateUsersAfterBatchLift(int[] banIds) {
        for (int banId : banIds) {
            try {
                BanRecord banRecord = getBanRecordById(banId);
                if (banRecord != null) {
                    BanRecord currentBan = getCurrentBanRecord(banRecord.getUserId());
                    if (currentBan == null) {
                        updateUserBanStatus(banRecord.getUserId(), false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 私有辅助方法：获取单个封禁记录
    private BanRecord getSingleBanRecord(String sql, Object... params) {
        try {
            ResultSet rs = executeQuery(sql, params);
            if (rs != null && rs.next()) {
                return mapResultSetToBanRecord(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 私有辅助方法：获取多个封禁记录
    private List<BanRecord> getMultipleBanRecords(String sql, Object... params) {
        List<BanRecord> banRecords = new ArrayList<>();
        try {
            ResultSet rs = executeQuery(sql, params);
            while (rs != null && rs.next()) {
                banRecords.add(mapResultSetToBanRecord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banRecords;
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

    // 私有辅助方法：将ResultSet映射为BanRecord对象
    private BanRecord mapResultSetToBanRecord(ResultSet rs) throws SQLException {
        BanRecord banRecord = new BanRecord();
        banRecord.setBanId(rs.getInt("ban_id"));
        banRecord.setUserId(rs.getInt("user_id"));
        banRecord.setAdminId(rs.getInt("admin_id"));
        banRecord.setReason(rs.getString("reason"));
        banRecord.setPermanent(rs.getBoolean("is_permanent"));
        banRecord.setStatus(BanRecord.BanStatus.fromValue(rs.getString("status")));

        // 处理时间字段
        Timestamp banStart = rs.getTimestamp("ban_start");
        if (banStart != null) {
            banRecord.setBanStart(banStart.toLocalDateTime());
        }

        Timestamp banEnd = rs.getTimestamp("ban_end");
        if (banEnd != null) {
            banRecord.setBanEnd(banEnd.toLocalDateTime());
        }

        return banRecord;
    }
}
