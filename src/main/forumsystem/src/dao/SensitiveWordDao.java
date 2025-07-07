package main.forumsystem.src.dao;

import main.forumsystem.src.entity.SensitiveWord;
import java.util.List;
import java.util.Set;

/**
 * 敏感词数据访问接口
 * 定义所有与敏感词相关的数据库操作
 */
public interface SensitiveWordDao {
    
    /**
     * 添加敏感词
     * @param sensitiveWord 敏感词对象
     * @return 是否添加成功
     */
    boolean addSensitiveWord(SensitiveWord sensitiveWord);
    
    /**
     * 批量添加敏感词
     * @param sensitiveWords 敏感词列表
     * @return 成功添加的数量
     */
    int batchAddSensitiveWords(List<SensitiveWord> sensitiveWords);
    
    /**
     * 删除敏感词
     * @param wordId 敏感词ID
     * @return 是否删除成功
     */
    boolean deleteSensitiveWord(int wordId);
    
    /**
     * 根据敏感词内容删除
     * @param word 敏感词内容
     * @return 是否删除成功
     */
    boolean deleteSensitiveWordByWord(String word);
    
    /**
     * 批量删除敏感词
     * @param wordIds 敏感词ID数组
     * @return 删除成功的数量
     */
    int batchDeleteSensitiveWords(int[] wordIds);
    
    /**
     * 更新敏感词
     * @param sensitiveWord 敏感词对象
     * @return 是否更新成功
     */
    boolean updateSensitiveWord(SensitiveWord sensitiveWord);
    
    /**
     * 根据ID查询敏感词
     * @param wordId 敏感词ID
     * @return 敏感词对象
     */
    SensitiveWord getSensitiveWordById(int wordId);
    
    /**
     * 根据敏感词内容查询
     * @param word 敏感词内容
     * @return 敏感词对象
     */
    SensitiveWord getSensitiveWordByWord(String word);
    
    /**
     * 获取所有敏感词
     * @return 敏感词列表
     */
    List<SensitiveWord> getAllSensitiveWords();
    
    /**
     * 获取所有敏感词内容（用于快速检查）
     * @return 敏感词内容集合
     */
    Set<String> getAllSensitiveWordSet();
    
    /**
     * 分页查询敏感词
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 敏感词列表
     */
    List<SensitiveWord> getSensitiveWordsByPage(int page, int size);
    
    /**
     * 搜索敏感词（模糊查询）
     * @param keyword 关键词
     * @return 敏感词列表
     */
    List<SensitiveWord> searchSensitiveWords(String keyword);
    
    /**
     * 检查敏感词是否存在
     * @param word 敏感词内容
     * @return 是否存在
     */
    boolean existsSensitiveWord(String word);
    
    /**
     * 获取敏感词总数
     * @return 敏感词总数
     */
    int getSensitiveWordCount();
    
    /**
     * 获取今日添加的敏感词数量
     * @return 今日添加数量
     */
    int getTodayAddedCount();
    
    /**
     * 批量检查文本中的敏感词
     * @param text 要检查的文本
     * @return 找到的敏感词列表
     */
    List<String> findSensitiveWordsInText(String text);
    
    /**
     * 替换文本中的敏感词
     * @param text 原始文本
     * @return 替换后的文本
     */
    String replaceSensitiveWords(String text);
    
    /**
     * 根据长度范围查询敏感词
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 敏感词列表
     */
    List<SensitiveWord> getSensitiveWordsByLength(int minLength, int maxLength);
    
    /**
     * 清空所有敏感词
     * @return 是否清空成功
     */
    boolean clearAllSensitiveWords();
    
    /**
     * 从文件导入敏感词
     * @param filePath 文件路径
     * @return 导入成功的数量
     */
    int importSensitiveWordsFromFile(String filePath);
    
    /**
     * 导出敏感词到文件
     * @param filePath 文件路径
     * @return 是否导出成功
     */
    boolean exportSensitiveWordsToFile(String filePath);
}
