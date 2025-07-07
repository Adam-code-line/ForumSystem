package main.forumsystem.src.test;

import main.forumsystem.src.dao.BaseDao;

public class BaseDaoTest {
    public static void main(String[] args) {
        // 测试数据库连接
        BaseDao.testConnection();
    }
}
