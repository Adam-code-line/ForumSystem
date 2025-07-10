package main.forumsystem.src.dao.impl;

import main.forumsystem.src.dao.BaseDao;
import main.forumsystem.src.dao.SensitiveWordDao;
import main.forumsystem.src.entity.SensitiveWord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 敏感词数据访问实现类
 */
public class SensitiveWordDaoImpl extends BaseDao implements SensitiveWordDao {

    @Override
    public boolean addSensitiveWord(SensitiveWord sensitiveWord) {
        // 检查敏感词是否已存在
        if (existsSensitiveWord(sensitiveWord.getWord())) {
            return false; // 已存在，不重复添加
        }
        
        String sql = "INSERT INTO sensitive_words (word, replacement, create_time) VALUES (?, ?, ?)";
        
        try {
            // 如果创建时间为空，设置为当前时间
            if (sensitiveWord.getCreateTime() == null) {
                sensitiveWord.setCreateTime(LocalDateTime.now());
            }
            
            int result = executeUpdate(sql,
                sensitiveWord.getWord().trim(),
                sensitiveWord.getReplacement(),
                Timestamp.valueOf(sensitiveWord.getCreateTime())
            );
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int batchAddSensitiveWords(List<SensitiveWord> sensitiveWords) {
        if (sensitiveWords == null || sensitiveWords.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        String sql = "INSERT IGNORE INTO sensitive_words (word, replacement, create_time) VALUES (?, ?, ?)";
        
        for (SensitiveWord sensitiveWord : sensitiveWords) {
            if (!sensitiveWord.isValid()) {
                continue;
            }
            
            try {
                if (sensitiveWord.getCreateTime() == null) {
                    sensitiveWord.setCreateTime(LocalDateTime.now());
                }
                
                int result = executeUpdate(sql,
                    sensitiveWord.getWord().trim(),
                    sensitiveWord.getReplacement(),
                    Timestamp.valueOf(sensitiveWord.getCreateTime())
                );
                
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
    public boolean deleteSensitiveWord(int wordId) {
        String sql = "DELETE FROM sensitive_words WHERE word_id = ?";
        try {
            int result = executeUpdate(sql, wordId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteSensitiveWordByWord(String word) {
        String sql = "DELETE FROM sensitive_words WHERE word = ?";
        try {
            int result = executeUpdate(sql, word.trim());
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int batchDeleteSensitiveWords(int[] wordIds) {
        if (wordIds == null || wordIds.length == 0) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("DELETE FROM sensitive_words WHERE word_id IN (");
        for (int i = 0; i < wordIds.length; i++) {
            sql.append("?");
            if (i < wordIds.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        try {
            Object[] params = new Object[wordIds.length];
            for (int i = 0; i < wordIds.length; i++) {
                params[i] = wordIds[i];
            }
            return executeUpdate(sql.toString(), params);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean updateSensitiveWord(SensitiveWord sensitiveWord) {
        String sql = "UPDATE sensitive_words SET word = ?, replacement = ? WHERE word_id = ?";
        
        try {
            int result = executeUpdate(sql,
                sensitiveWord.getWord().trim(),
                sensitiveWord.getReplacement(),
                sensitiveWord.getWordId()
            );
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public SensitiveWord getSensitiveWordById(int wordId) {
        String sql = "SELECT * FROM sensitive_words WHERE word_id = ?";
        return getSingleSensitiveWord(sql, wordId);
    }

    @Override
    public SensitiveWord getSensitiveWordByWord(String word) {
        String sql = "SELECT * FROM sensitive_words WHERE word = ?";
        return getSingleSensitiveWord(sql, word.trim());
    }

    @Override
    public List<SensitiveWord> getAllSensitiveWords() {
        String sql = "SELECT * FROM sensitive_words ORDER BY create_time DESC";
        return getMultipleSensitiveWords(sql);
    }

    @Override
    public Set<String> getAllSensitiveWordSet() {
        String sql = "SELECT word FROM sensitive_words";
        Set<String> wordSet = new HashSet<>();
        
        try {
            ResultSet rs = executeQuery(sql);
            while (rs != null && rs.next()) {
                wordSet.add(rs.getString("word"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return wordSet;
    }

    @Override
    public List<SensitiveWord> getSensitiveWordsByPage(int page, int size) {
        String sql = "SELECT * FROM sensitive_words ORDER BY create_time DESC LIMIT ? OFFSET ?";
        int offset = (page - 1) * size;
        return getMultipleSensitiveWords(sql, size, offset);
    }

    @Override
    public List<SensitiveWord> searchSensitiveWords(String keyword) {
        String sql = "SELECT * FROM sensitive_words WHERE word LIKE ? ORDER BY create_time DESC";
        String searchPattern = "%" + keyword + "%";
        return getMultipleSensitiveWords(sql, searchPattern);
    }

    @Override
    public boolean existsSensitiveWord(String word) {
        String sql = "SELECT COUNT(*) as count FROM sensitive_words WHERE word = ?";
        try {
            ResultSet rs = executeQuery(sql, word.trim());
            if (rs != null && rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getSensitiveWordCount() {
        String sql = "SELECT COUNT(*) as count FROM sensitive_words";
        return getCount(sql);
    }

    @Override
    public int getTodayAddedCount() {
        String sql = "SELECT COUNT(*) as count FROM sensitive_words WHERE DATE(create_time) = CURDATE()";
        return getCount(sql);
    }

    @Override
    public List<String> findSensitiveWordsInText(String text) {
        List<String> foundWords = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return foundWords;
        }
        
        Set<String> sensitiveWords = getAllSensitiveWordSet();
        String lowerText = text.toLowerCase();
        
        for (String word : sensitiveWords) {
            if (lowerText.contains(word.toLowerCase())) {
                foundWords.add(word);
            }
        }
        
        return foundWords;
    }

    @Override
    public String replaceSensitiveWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        List<SensitiveWord> sensitiveWords = getAllSensitiveWords();
        String result = text;
        
        for (SensitiveWord sensitiveWord : sensitiveWords) {
            String word = sensitiveWord.getWord();
            String replacement = sensitiveWord.getReplacement();
            
            // 添加空值检查
            if (word == null || word.trim().isEmpty()) {
                continue; // 跳过无效的敏感词
            }
            
            // 如果替换字符为空，使用默认值
            if (replacement == null) {
                replacement = "***"; // 默认替换字符
            }
            
            try {
                // 使用正则表达式进行不区分大小写的替换
                Pattern pattern = Pattern.compile(Pattern.quote(word), Pattern.CASE_INSENSITIVE);
                result = pattern.matcher(result).replaceAll(replacement);
            } catch (Exception e) {
                // 如果正则替换失败，使用简单字符串替换
                System.err.println("正则替换失败，使用简单替换: " + word);
                result = result.replaceAll("(?i)" + Pattern.quote(word), replacement);
            }
        }
        
        return result;
    }

    @Override
    public List<SensitiveWord> getSensitiveWordsByLength(int minLength, int maxLength) {
        String sql = "SELECT * FROM sensitive_words WHERE LENGTH(word) BETWEEN ? AND ? ORDER BY LENGTH(word), word";
        return getMultipleSensitiveWords(sql, minLength, maxLength);
    }

    @Override
    public boolean clearAllSensitiveWords() {
        String sql = "DELETE FROM sensitive_words";
        try {
            executeUpdate(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int importSensitiveWordsFromFile(String filePath) {
        List<SensitiveWord> sensitiveWords = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) { // 忽略空行和注释行
                    String[] parts = line.split("\\|"); // 使用|分隔词和替换字符
                    String word = parts[0].trim();
                    String replacement = parts.length > 1 ? parts[1].trim() : "***";
                    
                    if (!word.isEmpty()) {
                        sensitiveWords.add(new SensitiveWord(word, replacement));
                    }
                }
            }
            
            return batchAddSensitiveWords(sensitiveWords);
            
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean exportSensitiveWordsToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            
            writer.write("# 敏感词列表 - 格式: 敏感词|替换字符");
            writer.newLine();
            writer.write("# 导出时间: " + LocalDateTime.now());
            writer.newLine();
            writer.newLine();
            
            List<SensitiveWord> sensitiveWords = getAllSensitiveWords();
            for (SensitiveWord sensitiveWord : sensitiveWords) {
                writer.write(sensitiveWord.getWord() + "|" + sensitiveWord.getReplacement());
                writer.newLine();
            }
            
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 私有辅助方法：获取单个敏感词
    private SensitiveWord getSingleSensitiveWord(String sql, Object... params) {
        try {
            ResultSet rs = executeQuery(sql, params);
            if (rs != null && rs.next()) {
                return mapResultSetToSensitiveWord(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 私有辅助方法：获取多个敏感词
    private List<SensitiveWord> getMultipleSensitiveWords(String sql, Object... params) {
        List<SensitiveWord> sensitiveWords = new ArrayList<>();
        try {
            ResultSet rs = executeQuery(sql, params);
            while (rs != null && rs.next()) {
                sensitiveWords.add(mapResultSetToSensitiveWord(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sensitiveWords;
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

    // 私有辅助方法：将ResultSet映射为SensitiveWord对象
    private SensitiveWord mapResultSetToSensitiveWord(ResultSet rs) throws SQLException {
        SensitiveWord sensitiveWord = new SensitiveWord();
        sensitiveWord.setWordId(rs.getInt("word_id"));
        sensitiveWord.setWord(rs.getString("word"));
        sensitiveWord.setReplacement(rs.getString("replacement"));

        // 处理创建时间
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            sensitiveWord.setCreateTime(createTime.toLocalDateTime());
        }

        return sensitiveWord;
    }
}
