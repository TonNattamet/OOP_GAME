import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayState extends JPanel {

    private ImageIcon player = new ImageIcon(this.getClass().getResource("player.png"));
    private ImageIcon enemy = new ImageIcon(this.getClass().getResource("enemy.png"));
    private ImageIcon bulletImage = new ImageIcon(this.getClass().getResource("bullet.png"));
    private ImageIcon background = new ImageIcon(this.getClass().getResource("Firstbg.gif"));

    private ImageIcon NextIcon = new ImageIcon(this.getClass().getResource("next.png"));

    private int playerX = 300;
    private int playerY = 450;
    private int score = 0;
    private int playerLives = 3;
    private boolean isGameOver = false;

    private List<Bullet> bullets = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();

    private Random random = new Random();
    private JButton restartButton;
    private JButton exitButton;
    private JButton changeBackgroundButton;

    private int currentBackgroundIndex = 0;
    private ImageIcon[] backgroundImages = {
            new ImageIcon(this.getClass().getResource("change1.gif")),
            new ImageIcon(this.getClass().getResource("change2.gif")),
            new ImageIcon(this.getClass().getResource("change3.gif"))
    };

    // Level settings
    private int level = 1;
    private int maxEnemies = 2;
    private int enemiesDefeated = 0;

    public PlayState() {
        setupUI();
        setFocusable(true);
        requestFocus();
        startGameLoop();
    }

    private void startGameLoop() {
        Timer timer = new Timer(1000 / 60, e -> gameUpdate());
        timer.start();
    }

    private void gameUpdate() {
        if (!isGameOver) {
            spawnEnemies();
            updatePositions();
            checkCollisions();
            repaint();

            if (enemiesDefeated >= maxEnemies) {
                advanceLevel();
            }
        }
    }

    private void spawnEnemies() {
        if (enemies.size() < maxEnemies && Math.random() < 0.02) {
            int enemyX = random.nextInt(getWidth() - 80);
            int enemySpeed = level + random.nextInt(3) + 1;
            enemies.add(new Enemy(enemyX, 0, enemySpeed));
        }
    }

    private void updatePositions() {
        enemies.forEach(Enemy::move);
        bullets.forEach(Bullet::move);
        bullets.removeIf(bullet -> bullet.getY() < 0);
        enemies.removeIf(enemy -> enemy.getY() > getHeight());
    }

    private void checkCollisions() {
        List<Bullet> bulletsToRemove = new ArrayList<>();
        List<Enemy> enemiesToRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            for (Enemy enemy : enemies) {
                if (bullet.collidesWith(enemy)) {
                    bulletsToRemove.add(bullet);
                    enemiesToRemove.add(enemy);
                    score += 10;
                    enemiesDefeated++;
                }
            }
        }

        bullets.removeAll(bulletsToRemove);
        enemies.removeAll(enemiesToRemove);

        for (Enemy enemy : enemies) {
            if (enemy.collidesWithPlayer(playerX, playerY)) {
                playerLives--;
                enemiesToRemove.add(enemy);
            }
        }

        enemies.removeAll(enemiesToRemove);

        if (playerLives <= 0) {
            isGameOver = true;
            restartButton.setVisible(true);
            exitButton.setVisible(true);
        }
    }

    private void advanceLevel() {
        level++;
        maxEnemies += 1;
        enemiesDefeated = 0;
        enemies.clear();
        bullets.clear();

        switchBackground();
    }

    private void switchBackground() {
        currentBackgroundIndex = (currentBackgroundIndex + 1) % backgroundImages.length;
        background = backgroundImages[currentBackgroundIndex];
        repaint();
        requestFocusInWindow();
    }

    private void restartGame() {
        score = 0;
        playerLives = 3;
        isGameOver = false;
        level = 1;
        maxEnemies = 2;
        enemiesDefeated = 0;
        playerX = 300;
        playerY = 450;
        enemies.clear();
        bullets.clear();

        restartButton.setVisible(false);
        exitButton.setVisible(false);

        repaint();
    }

    // Bullet class
    private class Bullet {
        private int x, y;
        private final int width = 20;
        private final int height = 20;

        public Bullet(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void move() {
            y -= 10;
        }

        public void draw(Graphics g) {
            g.drawImage(bulletImage.getImage(), x, y, width, height, null);
        }

        public boolean collidesWith(Enemy enemy) {
            return x < enemy.getX() + 80 && x + width > enemy.getX() && y < enemy.getY() + 70
                    && y + height > enemy.getY();
        }

        public int getY() {
            return y;
        }
    }

    // Enemy class
    private class Enemy {
        private int x, y, speed;

        public Enemy(int x, int y, int speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
        }

        public void move() {
            y += speed;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Image getImage() {
            return enemy.getImage();
        }

        public boolean collidesWithPlayer(int playerX, int playerY) {
            return x < playerX + 80 && x + 80 > playerX && y < playerY + 70 && y + 70 > playerY;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
        g.drawImage(player.getImage(), playerX, playerY, 80, 70, this);

        for (Enemy enemy : enemies) {
            g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), 80, 70, this);
        }

        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Score: " + score, 10, 30);
        g.drawString("Lives: " + playerLives, 10, 60);
        g.drawString("LEVEL: " + level, 620, 30);
        if (isGameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over", getWidth() / 2 - 100, getHeight() / 2);
        }
    }

    private void setupUI() {
        // Next Background Button
        changeBackgroundButton = new JButton(NextIcon);
        changeBackgroundButton.setBounds(100, 10, 30, 30);
        changeBackgroundButton.addActionListener(e -> switchBackground());
        add(changeBackgroundButton);

        // Restart Button
        restartButton = new JButton("Restart");
        restartButton.setBounds(10, 10, 100, 30);
        restartButton.addActionListener(e -> restartGame());
        restartButton.setVisible(false); // Hide initially
        add(restartButton);

        // Exit Button
        exitButton = new JButton("Exit");
        exitButton.setBounds(120, 10, 100, 30);
        exitButton.addActionListener(e -> System.exit(0));
        exitButton.setVisible(false); // Hide initially
        add(exitButton);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isGameOver) {
                    handleKeyPress(e);
                }
            }
        });
    }

    private void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT:
                playerX -= 10;
                break;
            case KeyEvent.VK_RIGHT:
                playerX += 10;
                break;
            case KeyEvent.VK_DOWN:
                playerY += 10;
                break;
            case KeyEvent.VK_UP:
                playerY -= 10;
                break;
            case KeyEvent.VK_SPACE:
                bullets.add(new Bullet(playerX + 28, playerY));
                break;
        }
        repaint();
    }

}
