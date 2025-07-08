package main.forumsystem.src.dao.impl;

import main.forumsystem.src.dao.BaseDao;
import main.forumsystem.src.dao.UserBlockDao;
import main.forumsystem.src.entity.UserBlock;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 用户拉黑数据访问实现类
 */
public class UserBlockDaoImpl extends BaseDao implements UserBlockDao {
    
    @Override
    public boolean addBlock(UserBlock userBlock) {
        if (userBlock == null) {
            return false;
        }
        
        String sql = "INSERT INTO user_blocks (blocker_id, blocked_id, block_time, reason, status, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // 检查是否已存在拉黑记录
            if (isBlocked(userBlock.getBlockerId(), userBlock.getBlockedId())) {
                return false;
            }
            
            pstmt.setInt(1, userBlock.getBlockerId());
            pstmt.setInt(2, userBlock.getBlockedId());
            pstmt.setTimestamp(3, Timestamp.valueOf(userBlock.getBlockTime()));
            pstmt.setString(4, userBlock.getReason());
            pstmt.setString(5, userBlock.getStatus().getValue());
            pstmt.setTimestamp(6, Timestamp.valueOf(userBlock.getCreatedAt()));
            pstmt.setTimestamp(7, Timestamp.valueOf(userBlock.getUpdatedAt()));
            
            int result = pstmt.executeUpdate();
            System.out.println("添加拉黑记录: 用户" + userBlock.getBlockerId() + 
                             " 拉黑了用户" + userBlock.getBlockedId());
            return result > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean removeBlock(int blockerId, int blockedId) {
        if (blockerId <= 0 || blockedId <= 0) {
            return false;
        }
        
        String sql = "UPDATE user_blocks SET status = 'removed', updated_at = ? " +
                    "WHERE blocker_id = ? AND blocked_id = ? AND status = 'active'";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, blockerId);
            pstmt.setInt(3, blockedId);
            
            int result = pstmt.executeUpdate();
            System.out.println("移除拉黑记录: 用户" + blockerId + 
                             " 取消拉黑用户" + blockedId);
            return result > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean isBlocked(int blockerId, int blockedId) {
        if (blockerId <= 0 || blockedId <= 0 || blockerId == blockedId) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM user_blocks " +
                    "WHERE blocker_id = ? AND blocked_id = ? AND status = 'active'";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, blockerId);
            pstmt.setInt(2, blockedId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public List<UserBlock> getUserBlockList(int userId) {
        if (userId <= 0) {
            return new ArrayList<>();
        }
        
        String sql = "SELECT * FROM user_blocks WHERE blocker_id = ? AND status = 'active' " +
                    "ORDER BY block_time DESC";
        
        List<UserBlock> blockList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    UserBlock userBlock = mapResultSetToUserBlock(rs);
                    blockList.add(userBlock);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return blockList;
    }
    
    @Override
    public List<UserBlock> getBlockedByList(int blockedUserId) {
        if (blockedUserId <= 0) {
            return new ArrayList<>();
        }
        
        String sql = "SELECT * FROM user_blocks WHERE blocked_id = ? AND status = 'active' " +
                    "ORDER BY block_time DESC";
        
        List<UserBlock> blockList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, blockedUserId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    UserBlock userBlock = mapResultSetToUserBlock(rs);
                    blockList.add(userBlock);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return blockList;
    }
    
    @Override
    public UserBlock getBlockRecord(int blockerId, int blockedId) {
        if (blockerId <= 0 || blockedId <= 0) {
            return null;
        }
        
        String sql = "SELECT * FROM user_blocks WHERE blocker_id = ? AND blocked_id = ? " +
                    "ORDER BY created_at DESC LIMIT 1";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, blockerId);
            pstmt.setInt(2, blockedId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserBlock(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public boolean cleanupBlocksForDeletedUser(int userId) {
        if (userId <= 0) {
            return false;
        }
        
        String sql = "DELETE FROM user_blocks WHERE blocker_id = ? OR blocked_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            
            int result = pstmt.executeUpdate();
            System.out.println("清理用户" + userId + "的所有拉黑记录，删除了" + result + "条记录");
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 将ResultSet映射为UserBlock对象
     */
    private UserBlock mapResultSetToUserBlock(ResultSet rs) throws SQLException {
        UserBlock userBlock = new UserBlock();
        userBlock.setBlockId(rs.getInt("block_id"));
        userBlock.setBlockerId(rs.getInt("blocker_id"));
        userBlock.setBlockedId(rs.getInt("blocked_id"));
        
        Timestamp blockTime = rs.getTimestamp("block_time");
        if (blockTime != null) {
            userBlock.setBlockTime(blockTime.toLocalDateTime());
        }
        
        String status = rs.getString("status");
        userBlock.setStatus(UserBlock.BlockStatus.fromValue(status));
        
        userBlock.setReason(rs.getString("reason"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            userBlock.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            userBlock.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return userBlock;
    }
}
