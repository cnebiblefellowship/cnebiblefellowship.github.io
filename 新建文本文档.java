import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class ShutdownChallenge extends JFrame {

    private JButton startShutdownButton;
    private JButton enterGameButton;
    private JButton startGameButton;
    private JButton optimizeButton; // 新增的优化按钮
    private JButton blueScreenButton; // 新增的蓝屏按钮
    private JButton confirmRecitationButton; // 确认背诵按钮
    private JButton refuseRecitationButton; // 拒绝背诵按钮
    private JLabel timerLabel; // 倒计时标签
    private Timer countdownTimer; // 倒计时定时器
    private Timer postPromptTimer; // 提示后倒计时定时器
    private boolean ctrlPressed = false; // 标记是否按下了Ctrl键
    private boolean ctrlPromptDisplayed = false; // 标记Ctrl键提示是否已显示
    private int blueScreenClickCount = 0; // 蓝屏按钮点击计数

    // 定义正确答案
    private final String correctAnswer = "A";
    // 《观沧海》文本
    private final String recitationText = "对海而唱《观沧海》\n" +
            "对海而唱《观沧海》\n" +
            "对海而唱《观沧海》\n" +
            "对海而唱《观沧海》\n" +
            "对海而唱《观沧海》\n";

    public ShutdownChallenge() {
        // 设置窗口标题
        setTitle("关机挑战");

        // 设置窗口大小
        setSize(400, 400);

        // 设置窗口居中
        setLocationRelativeTo(null);

        // 设置窗口始终在最上面
        setAlwaysOnTop(true);

        // 设置布局为BorderLayout
        setLayout(new BorderLayout());

        // 创建按钮
        startGameButton = new JButton("开始游戏");
        startShutdownButton = new JButton("开始关机");
        enterGameButton = new JButton("进入游戏");
        optimizeButton = new JButton("优化电脑"); // 新增的优化按钮
        blueScreenButton = new JButton("想喝蓝瓶钙吗？尝试一下"); // 新增的蓝屏按钮
        confirmRecitationButton = new JButton("确定"); // 确认背诵按钮
        refuseRecitationButton = new JButton("不会背毁灭吧"); // 拒绝背诵按钮

        // 创建倒计时标签
        timerLabel = new JLabel("请按下Ctrl键，5秒内未按则关机", SwingConstants.CENTER);

        // 创建面板来容纳按钮
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1));
        buttonPanel.add(startGameButton);
        buttonPanel.add(startShutdownButton);
        buttonPanel.add(optimizeButton);
        buttonPanel.add(enterGameButton);
        buttonPanel.add(blueScreenButton); // 添加蓝屏按钮

        // 将按钮面板添加到窗口的中部
        add(buttonPanel, BorderLayout.CENTER);

        // 将倒计时标签添加到窗口的底部
        add(timerLabel, BorderLayout.SOUTH);

        // 隐藏“进入游戏”按钮
        enterGameButton.setVisible(false);

        // 设置按钮的点击事件
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shutdownComputer();
            }
        });

        startShutdownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shutdownComputer();
            }
        });

        enterGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showQuestion();
            }
        });

        optimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOptimizationDialog();
            }
        });

        blueScreenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blueScreenClickCount++;
                if (blueScreenClickCount >= 5) {
                    showBlueScreenEffect();
                }
            }
        });

        // 确认背诵按钮的点击事件
        confirmRecitationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 读取用户输入
                String userInput = JOptionPane.showInputDialog(ShutdownChallenge.this, "请背诵《观沧海》：");
                // 判断是否正确
                if (userInput != null && userInput.trim().equals(recitationText)) {
                    JOptionPane.showMessageDialog(ShutdownChallenge.this, "背诵正确！程序关闭。");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(ShutdownChallenge.this, "背诵错误！即将关机...");
                    shutdownComputer();
                }
            }
        });

        // 拒绝背诵按钮的点击事件
        refuseRecitationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ShutdownChallenge.this, "背诵失败，程序即将关机...");
                shutdownComputer();
            }
        });

        // 监听键盘事件，模拟返回键行为
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) { // 按下Backspace键
                    enterGameButton.setVisible(true); // 显示“进入游戏”按钮
                    revalidate();
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_CONTROL) { // 按下Ctrl键
                    ctrlPressed = true; // 标记Ctrl键已被按下
                    if (ctrlPromptDisplayed) {
                        // 如果 Ctrl 提示已经显示且 Ctrl 键被按下，则取消关闭
                        cancelShutdown();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { // 按下ESC键
                    showAuthorPrompt();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) { // 释放Ctrl键
                    ctrlPressed = false;
                }
            }
        });

        // 设置窗口为焦点窗口以捕获按键事件
        setFocusable(true);
        requestFocusInWindow();

        // 显示窗口
        setVisible(true);

        // 开始倒计时
        startCountdown();
    }

    // 显示问题
    private void showQuestion() {
        String[] options = {"A", "B", "C"};
        int answer = JOptionPane.showOptionDialog(this,
                "选择正确的答案（A/B/C）：",
                "选择题",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        // 检查答案
        if (answer != -1 && options[answer].equals(correctAnswer)) {
            showRecitationPrompt();
        } else {
            JOptionPane.showMessageDialog(this, "回答错误！即将关机...");
            shutdownComputer();
        }
    }

    // 显示背诵提示
    private void showRecitationPrompt() {
        // 创建一个新的对话框显示背诵提示
        JDialog recitationDialog = new JDialog(this, "背诵《观沧海》", true);
        recitationDialog.setLayout(new BorderLayout());
        recitationDialog.setSize(300, 200);
        recitationDialog.setLocationRelativeTo(this);

        // 创建输入框和按钮
        JTextArea recitationTextArea = new JTextArea(5, 20);
        recitationTextArea.setWrapStyleWord(true);
        recitationTextArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(recitationTextArea);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(confirmRecitationButton);
        buttonPanel.add(refuseRecitationButton);

        recitationDialog.add(new JLabel("请背诵《观沧海》：", SwingConstants.CENTER), BorderLayout.NORTH);
        recitationDialog.add(scrollPane, BorderLayout.CENTER);
        recitationDialog.add(buttonPanel, BorderLayout.SOUTH);

        recitationDialog.setVisible(true);
    }

    // 显示优化电脑对话框
    private void showOptimizationDialog() {
        int choice = JOptionPane.showConfirmDialog(this,
                "重启就是最好的优化。点击确定将会重启电脑。",
                "优化电脑",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (choice == JOptionPane.OK_OPTION) {
            restartComputer();
        }
    }

    // 显示蓝屏效果
    private void showBlueScreenEffect() {
        JOptionPane.showMessageDialog(this,
                "蓝屏效果！模拟蓝屏显示。\n（请注意：此操作仅为模拟，实际不会引发蓝屏。）",
                "蓝屏效果",
                JOptionPane.ERROR_MESSAGE);
    }

    // 重启电脑方法
    private void restartComputer() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                Runtime.getRuntime().exec("shutdown -r -t 0"); // Windows重启
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                Runtime.getRuntime().exec("shutdown -r now"); // Linux/Mac重启
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 取消关机方法
    private void cancelShutdown() {
        JOptionPane.showMessageDialog(this, "关机已取消。");
    }

    // 显示作者提示
    private void showAuthorPrompt() {
        String response = JOptionPane.showInputDialog(this, "请输入作者：");
        if ("刘和平".equalsIgnoreCase(response)) {
            JOptionPane.showMessageDialog(this, "退出程序。");
            System.exit(0);
        } else {
            JOptionPane.showMessageDialog(this, "回答错误！");
        }
    }

    // 启动倒计时
    private void startCountdown() {
        countdownTimer = new Timer(1000, new ActionListener() {
            private int secondsLeft = 5; // 倒计时5秒

            @Override
            public void actionPerformed(ActionEvent e) {
                if (ctrlPressed) {
                    // 如果Ctrl键被按下，则取消倒计时
                    countdownTimer.stop();
                    timerLabel.setText("倒计时已取消。");
                    return;
                }

                secondsLeft--;
                timerLabel.setText("关机倒计时: " + secondsLeft + "秒");

                if (secondsLeft <= 0) {
                    timerLabel.setText("时间到，正在关机...");
                    shutdownComputer();
                    countdownTimer.stop();
                }
            }
        });
        countdownTimer.start();
    }

    // 启动提示后的倒计时
    private void startPostPromptCountdown() {
        postPromptTimer = new Timer(1000, new ActionListener() {
            private int secondsLeft = 5; // 提示后倒计时5秒

            @Override
            public void actionPerformed(ActionEvent e) {
                secondsLeft--;
                timerLabel.setText("提示后倒计时: " + secondsLeft + "秒");

                if (secondsLeft <= 0) {
                    timerLabel.setText("时间到，正在关机...");
                    shutdownComputer();
                    postPromptTimer.stop();
                }
            }
        });
        postPromptTimer.start();
    }

    // 关机方法
    private void shutdownComputer() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                Runtime.getRuntime().exec("shutdown -s -t 0"); // Windows关机
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                Runtime.getRuntime().exec("shutdown -h now"); // Linux/Mac关机
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ShutdownChallenge();
    }
}
