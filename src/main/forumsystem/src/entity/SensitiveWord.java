package main.forumsystem.src.entity;

import java.time.LocalDateTime;

/**
 * 敏感词实体类
 * 对应数据库表：sensitive_words
 */
public class SensitiveWord {
    private int wordId;
    private String word;
    private String replacement;
    private LocalDateTime createTime;

    // 构造函数
    public SensitiveWord() {}

    public SensitiveWord(String word) {
        this.word = word;
        this.replacement = "***"; // 默认替换字符
    }

    public SensitiveWord(String word, String replacement) {
        this.word = word;
        this.replacement = replacement;
    }

    // Getters and Setters
    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    // 便民方法
    public boolean isValid() {
        return word != null && !word.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "SensitiveWord{" +
                "wordId=" + wordId +
                ", word='" + word + '\'' +
                ", replacement='" + replacement + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SensitiveWord that = (SensitiveWord) obj;
        return word != null ? word.equals(that.word) : that.word == null;
    }

    @Override
    public int hashCode() {
        return word != null ? word.hashCode() : 0;
    }
}
