//lauryn holloway2024
//edited by mich
import java.awt.*;
import java.awt.event.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;  // Correct Random import
import javax.swing.*;

public class ghostApproach extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardHeight = 640;

    // images
    Image backgrndImg;
    Image ghostImg;
    Image toppipeImg;
    Image bottompipeImg;
    Image titleImg;

    // ghost
    int ghostX = boardWidth / 8;
    int ghostY = boardHeight / 2;
    int ghostWidth = 34;
    int ghostHeight = 24;

    class Ghost {
        int x = ghostX;
        int y = ghostY;
        int width = ghostWidth;
        int height = ghostHeight;
        Image img;

        Ghost(Image img) {
            this.img = img;
        }
    }

    //pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    //game logic
    Ghost ghost;
    int velocityX = -4; // rate the pipe moves
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random(); // Fix random initialization

    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;
    double score = 0;

    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel passwordLabel, titleLabel;
    private boolean isAuthenticated = false;
    private static final String CORRECT_HASH = "ead6ef03d61ee60c533d6d450c50a1e559a8a37f6b796a4094cd0dac6b744428";
    // ^^^ this is the hashed password 


    //canvas and background
    ghostApproach() {
        setLayout(null);
        setBackground(Color.BLACK);

        // parts for the password screen
        passwordLabel = new JLabel("Enter Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(120, 400, 120, 30);  // Position label

        passwordField = new JPasswordField(15);
        passwordField.setBounds(120, 430, 120, 30);  // Position password field

        loginButton = new JButton("Login");
        loginButton.setBounds(140, 460, 80, 30);  // Position login button

        // load title image (resized to fit)
        ImageIcon originalTitleImage = new ImageIcon(getClass().getResource("/title.png"));
        Image titleImage = originalTitleImage.getImage().getScaledInstance(300, 100, Image.SCALE_SMOOTH);
        ImageIcon resizedTitleImage = new ImageIcon(titleImage);

        // JLabel with the resized title image
        titleLabel = new JLabel(resizedTitleImage);
        titleLabel.setBounds(20, 200, 300, 100);

        // Add components to the panel
        add(titleLabel);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        

        // action listenerrrr
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enteredPassword = new String(passwordField.getPassword());

                try {
                    // this hashes the password entered
                    String enteredHash = PasswordHasher.hashPassword(enteredPassword);

                    // this compares the hashed password to the stored hashed password
                    if (enteredHash.equals(CORRECT_HASH)) {
                        isAuthenticated = true;
                        removePasswordComponents();
                        titleLabel.setVisible(false);
                        startGame();  // Start the game logic
                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect Password!");
                    }
                } catch (NoSuchAlgorithmException ex) {
                    JOptionPane.showMessageDialog(null, "Error hashing the password: " + ex.getMessage());
                }
            }
        });

        // Here is back to the continuation of Lauryn's original code
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load images
        backgrndImg = new ImageIcon(getClass().getResource("/background.png")).getImage();
        ghostImg = new ImageIcon(getClass().getResource("/ghost.png")).getImage();
        toppipeImg = new ImageIcon(getClass().getResource("/top.png")).getImage();
        bottompipeImg = new ImageIcon(getClass().getResource("/bottom.png")).getImage();

        ghost = new Ghost(ghostImg);
        pipes = new ArrayList<Pipe>();
    }

    // removes password area after password is entered
    private void removePasswordComponents() {
        passwordLabel.setVisible(false);
        passwordField.setVisible(false);
        loginButton.setVisible(false);
    }

    // method that starts game after password is entered
    // the placePipesTimer and gameLoop has been placed in here
    private void startGame() {
        // Initialize game loop timer
        gameLoop = new Timer(1000 / 60, this); // 60 FPS
        gameLoop.start();  // Start the game loop

        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();  // Start the pipe timer
    }

    private void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(toppipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottompipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // background
        g.drawImage(backgrndImg, 0, 0, boardWidth, boardHeight, null);

        //ghost
        g.drawImage(ghost.img, ghost.x, ghost.y, ghost.width, ghost.height, null);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameOver){
            g.drawString("Game Over: " + String.valueOf((int)score), 10, 35);
        }
        else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }

    }

    public void move() {
        //ghost
        velocityY += gravity;
        ghost.y += velocityY;
        ghost.y = Math.max(ghost.y, 0); // Prevent the ghost from going off the top

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && ghost.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5;
            }

            if (collision(ghost, pipe)){
                gameOver = true;

            }

            // Remove pipes that are off screen
            if (pipe.x + pipe.width < 0) {
                pipes.remove(i);
                i--; // Adjust the index to compensate for removal
            }
        }

        if(ghost.y > boardHeight){
            gameOver = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    public boolean collision(Ghost a, Pipe b){
        return a.x < b.x + b.width &&
        a.x + a.width > b.x &&
        a.y < b.y + b.height &&
        a.y + a.height > b.y;

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9; // Make the ghost "jump"
            if(gameOver){
                //restart
                ghost.y = ghostY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed for this game
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not needed for this game
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ghost Approach");
        ghostApproach gamePanel = new ghostApproach();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
