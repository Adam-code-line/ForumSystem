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
 * 提供对敏感词表（sensitive_words）的增删改查操作，以及相关的业务逻辑处理。
 */
public class SensitiveWordDaoImpl extends BaseDao implements SensitiveWordDao {

    /**
     * 添加敏感词
     * 将新的敏感词插入到数据库中。
     * @param sensitiveWord 敏感词对象，包含敏感词、替换字符和创建时间
     * @return boolean 是否添加成功
     */
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

    /**
     * 批量添加敏感词
     * 将多个敏感词插入到数据库中。
     * @param sensitiveWords 敏感词列表
     * @return int 成功添加的敏感词数量
     */
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

    /**
     * 删除敏感词（根据ID）
     * 从数据库中删除指定ID的敏感词。
     * @param wordId 敏感词ID
     * @return boolean 是否删除成功
     */
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

    /**
     * 删除敏感词（根据词）
     * 从数据库中删除指定敏感词。
     * @param word 敏感词
     * @return boolean 是否删除成功
     */
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

    /**
     * 批量删除敏感词
     * 从数据库中删除多个敏感词。
     * @param wordIds 敏感词ID数组
     * @return int 成功删除的敏感词数量
     */
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

    /**
     * 更新敏感词
     * 修改敏感词的内容或替换字符。
     * @param sensitiveWord 敏感词对象，包含需要更新的内容
     * @return boolean 是否更新成功
     */
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

    /**
     * 根据敏感词ID获取敏感词
     * 查询指定ID的敏感词。
     * @param wordId 敏感词ID
     * @return SensitiveWord 敏感词对象
     */
    @Override
    public SensitiveWord getSensitiveWordById(int wordId) {
        String sql = "SELECT * FROM sensitive_words WHERE word_id = ?";
        return getSingleSensitiveWord(sql, wordId);
    }

    /**
     * 根据敏感词内容获取敏感词
     * 查询指定敏感词的详细信息。
     * @param word 敏感词
     * @return SensitiveWord 敏感词对象
     */
    @Override
    public SensitiveWord getSensitiveWordByWord(String word) {
        String sql = "SELECT * FROM sensitive_words WHERE word = ?";
        return getSingleSensitiveWord(sql, word.trim());
    }

    /**
     * 获取所有敏感词
     * 查询数据库中的所有敏感词。
     * @return List<SensitiveWord> 敏感词列表
     */
    @Override
    public List<SensitiveWord> getAllSensitiveWords() {
        String sql = "SELECT * FROM sensitive_words ORDER BY create_time DESC";
        return getMultipleSensitiveWords(sql);
    }

    /**
     * 获取所有敏感词集合
     * 查询数据库中的所有敏感词，并返回一个集合。
     * @return Set<String> 敏感词集合
     */
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

    /**
     * 分页获取敏感词
     * 查询数据库中的敏感词，并进行分页。
     * @param page 页码（从1开始）
     * @param size 每页记录数
     * @return List<SensitiveWord> 敏感词列表
     */
    @Override
    public List<SensitiveWord> getSensitiveWordsByPage(int page, int size) {
        String sql = "SELECT * FROM sensitive_words ORDER BY create_time DESC LIMIT ? OFFSET ?";
        int offset = (page - 1) * size;
        return getMultipleSensitiveWords(sql, size, offset);
    }

    /**
     * 搜索敏感词
     * 根据关键词模糊查询敏感词。
     * @param keyword 搜索关键词
     * @return List<SensitiveWord> 敏感词列表
     */
    @Override
    public List<SensitiveWord> searchSensitiveWords(String keyword) {
        String sql = "SELECT * FROM sensitive_words WHERE word LIKE ? ORDER BY create_time DESC";
        String searchPattern = "%" + keyword + "%";
        return getMultipleSensitiveWords(sql, searchPattern);
    }

    /**
     * 检查敏感词是否存在
     * 查询指定敏感词是否已存在于数据库中。
     * @param word 敏感词
     * @return boolean 是否存在
     */
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

    /**
     * 获取敏感词总数
     * 查询数据库中的敏感词总数量。
     * @return int 敏感词数量
     */
    @Override
    public int getSensitiveWordCount() {
        String sql = "SELECT COUNT(*) as count FROM sensitive_words";
        return getCount(sql);
    }

    /**
     * 获取当天新增的敏感词数量
     * 查询当天添加的敏感词数量。
     * @return int 当天新增敏感词数量
     */
    @Override
    public int getTodayAddedCount() {
        String sql = "SELECT COUNT(*) as count FROM sensitive_words WHERE DATE(create_time) = CURDATE()";
        return getCount(sql);
    }

    /**
     * 在文本中查找敏感词
     * 检查指定文本中是否包含敏感词。
     * @param text 待检查的文本
     * @return List<String> 包含的敏感词列表
     */
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

    /**
     * 替换文本中的敏感词
     * 将文本中的敏感词替换为指定的替换字符。
     * @param text 待替换的文本
     * @return String 替换后的文本
     */
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

    /**
     * 根据敏感词长度范围获取敏感词
     * 查询指定长度范围内的敏感词。
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return List<SensitiveWord> 敏感词列表
     */
    @Override
    public List<SensitiveWord> getSensitiveWordsByLength(int minLength, int maxLength) {
        String sql = "SELECT * FROM sensitive_words WHERE LENGTH(word) BETWEEN ? AND ? ORDER BY LENGTH(word), word";
        return getMultipleSensitiveWords(sql, minLength, maxLength);
    }

    /**
     * 清空所有敏感词
     * 删除数据库中的所有敏感词。
     * @return boolean 是否清空成功
     */
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

    /**
     * 从文件导入敏感词
     * 读取文件中的敏感词并导入到数据库中。
     * @param filePath 文件路径
     * @return int 成功导入的敏感词数量
     */
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

    /**
     * 导出敏感词到文件
     * 将数据库中的敏感词导出到指定文件。
     * @param filePath 文件路径
     * @return boolean 是否导出成功
     */
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
