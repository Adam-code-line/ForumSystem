import main.forumsystem.src.controller.MainController;

public class Main {
    public static void main(String[] args) {
        try {
            MainController mainController = new MainController();
            mainController.start();
        } catch (Exception e) {
            System.err.println("系统启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}