/* Minesweeper AI Pseudocode
 * Check that at least 20% of the board is revealed, or 20% of the cells are
 * not unclicked (not -1 in the 2D array)
 * 
 * If <20%, random choose a spot to reveal.
 * Highlight spot, then reveal spot.
 * 
 * If >20%, check each number tile with the same number of unrevealed neighbors
 * as their numbers.
 * Highlight all the mines that are confirmed, then flag.
 * If there is no such case, then guess. Highlight, then reveal.
 * Then, check for all the numbers with their conditions fufilled. That is, there
 * is the correct number of flags around it. Highlight these numbers, then reveal
 * the entire grid around each number that has not been revealed.
 * 
 * once board is all open and all the mines are flagged (or the number of flags 
 * equals the number of mines), then the game is won.
 * 
 */
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.net.URL;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
public class Minesweeper extends MouseAdapter implements ActionListener{
    int screenWidth;
    int screenHeight;
    int frameWidth;
    int frameHeight;
    
    int[][] bombGrid = new int[9][9];
    int[][] displayGrid = new int[9][9];
    
    JFrame window = new JFrame("MINESWEEPER");
    MyJPanel playingGrid = new MyJPanel();
    
    JLabel bombsRemaining;
    JLabel timeSpent;
    
    int numBombs = 10;
    int currBombs = 10;
    int timeElapsed = 0;
    int gridSizeWidth = 9;
    int gridSizeHeight = 9;
    int playingGridSizeWidth = gridSizeWidth*20 + 2;
    int playingGridSizeHeight = gridSizeHeight*20 + 2;
    
    private javax.swing.JMenu jMenu1; //game
    private javax.swing.JMenu jMenu2; //option
    private javax.swing.JMenu jMenu3; //help
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1; //new game
    private javax.swing.JMenuItem jMenuItem2; //exit
    private javax.swing.JMenuItem jMenuItem3; //total mines
    private javax.swing.JMenuItem jMenuItem4; //how to play
    private javax.swing.JMenuItem jMenuItem5; //about
    private javax.swing.JMenuItem jMenuItem6; //change grid size
    
    BufferedImage blankMine;
    BufferedImage noMine;
    BufferedImage yesMine;
    BufferedImage flag;
    
    boolean gameStarted = false;
    javax.swing.Timer timer;
    
    JToggleButton flagClick;
    boolean flagMode = false;
    
    String[] difficultyStrings = {"Easy", "Intermediate", "Expert", "Custom"};
    JComboBox difficulty; 
    
    int bombLabelWidth;
    int bombLabelHeight;
    int timeLabelWidth;
    int timeLabelHeight;
    int flagButtonWidth;
    int flagButtonHeight;
    int difficultyWidth;
    int difficultyHeight;
    public static void main(String[] args){
        Minesweeper test = new Minesweeper();
        test.createGUI();
    }
    
    public void createGUI(){
        //create jframe
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        screenWidth = (int)(window.getToolkit().getScreenSize().getWidth());
        screenHeight = (int)(window.getToolkit().getScreenSize().getHeight());
        frameWidth = playingGridSizeWidth + 40;
        frameHeight = playingGridSizeHeight + 250;
        window.setBounds(screenWidth/2 - frameWidth/2, screenHeight/2 - frameHeight/2, frameWidth, frameHeight);
        window.setLayout(null);
        
        
        
        //add playing grid
        window.add(playingGrid);
        
        playingGrid.setBounds(20,20,playingGridSizeWidth,playingGridSizeHeight);
        playingGrid.setBackground(Color.white);
        playingGrid.addMouseListener(this);
        
        //create all images
        try{
            blankMine = ImageIO.read(new File("unclicked.png"));
        }catch(Exception e){
            e.printStackTrace();
        }
        
        try{
            noMine = ImageIO.read(new File("clicked_blank.png"));
        }catch(Exception e){
            e.printStackTrace();
        }
        
        try{
            yesMine = ImageIO.read(new File("mine.png"));
        }catch(Exception e){
            e.printStackTrace();
        }
        
        try{
            flag = ImageIO.read(new File("flag.png"));
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
        //add bombs left
        bombsRemaining = new JLabel(currBombs + "", (int)JLabel.CENTER_ALIGNMENT);
        bombsRemaining.setBorder(new TitledBorder("Bombs"));
        window.add(bombsRemaining);
        bombLabelWidth = 80;
        bombLabelHeight = 80;
        bombsRemaining.setBounds(frameWidth/2, playingGridSizeHeight + 20, bombLabelWidth, bombLabelHeight);
        
        
        //add time elapsed
        timeSpent = new JLabel(timeElapsed + "", (int)JLabel.CENTER_ALIGNMENT);
        timeSpent.setBorder(new TitledBorder("Time"));
        window.add(timeSpent);
        timeLabelWidth = 80;
        timeLabelHeight = 80;
        timeSpent.setBounds(frameWidth/2-timeLabelWidth , 
                            playingGridSizeHeight + 20, timeLabelWidth, timeLabelHeight);
        
        //add flag button
        flagClick = new JToggleButton("Flag");
        window.add(flagClick);
        flagButtonWidth = 60;
        flagButtonHeight = 30;
        flagClick.setBounds(frameWidth/2- flagButtonWidth/2, 
                            playingGridSizeHeight + 20 + timeLabelHeight, flagButtonWidth, flagButtonHeight);
        flagClick.addActionListener(this);
        
        //add difficulty selection
        difficulty = new JComboBox(difficultyStrings);
        difficulty.setSelectedIndex(0);
        difficulty.setBorder(new TitledBorder("Difficulty"));
        difficulty.addActionListener(this);
        
        window.add(difficulty);
        difficultyWidth = 200;
        difficultyHeight = 60;
        difficulty.setBounds(frameWidth/2- difficultyWidth/2, 
                            playingGridSizeHeight + 20 + timeLabelHeight + flagButtonHeight, 
                            difficultyWidth, difficultyHeight);
        
        
        //add time
        timer = new javax.swing.Timer(1000,this);
        //timer.addActionListener(this);
        
        //add menu stuff
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        
        jMenu1.setText("Game");

        jMenuItem1.setText("New Game");
        jMenuItem1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                initializeGame();
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Exit");
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Options");

        jMenuItem3.setText("Total Mines");
        jMenuItem3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                try{
                    JOptionPane setMines = new JOptionPane();
                    String prompt = "Enter the number of bombs you want. \nThe game will restart with " +
                                    "the new amount of bombs.";
                    String newBombs = setMines.showInputDialog(null, prompt, "Set Bombs", JOptionPane.PLAIN_MESSAGE);
                    try{
                        numBombs = Integer.parseInt(newBombs);
                        currBombs = numBombs;
                        bombsRemaining.setText(numBombs + "");
                    }catch(Exception ex){
                        ex.printStackTrace();
                        System.out.println("Enter a number without spaces!");
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                initializeGame();
                playingGrid.repaint();
            }
        });
        
        
        jMenuItem6.setText("Change Grid Size");
        
        
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Help");

        jMenuItem4.setText("How to Play");
        jMenuItem4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                try{
                    JTextPane howTo = new JTextPane();
                    howTo.setPage(new URL("file:minesweeper_howtoplay.html"));
                    howTo.setEditable(false);
                    JScrollPane helpScroll = new JScrollPane(howTo);
                    helpScroll.setPreferredSize(new Dimension(400,400));
                    JOptionPane display = new JOptionPane(helpScroll);
                    display.showMessageDialog(null, helpScroll, "How To Play", JOptionPane.PLAIN_MESSAGE);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                
            }
        });
        

        jMenuItem5.setText("About");
        jMenuItem5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                try{
                    JTextPane about = new JTextPane();
                    about.setPage(new URL("file:minesweeper_about.html"));
                    about.setEditable(false);
                    JOptionPane display = new JOptionPane(about);
                    display.showMessageDialog(null, about, "About", JOptionPane.PLAIN_MESSAGE);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                
            }
        });
        
        
        jMenu3.add(jMenuItem5);

        jMenuBar1.add(jMenu3);

        window.setJMenuBar(jMenuBar1);
        
        
        
        window.setVisible(true);
        initializeGame();
    }
    
    
    public void initializeGame(){
        gameStarted = false;
        bombGrid = newBoard(gridSizeHeight, gridSizeWidth, numBombs);
        for(int r = 0; r<gridSizeHeight; r++){
            for(int c = 0; c<gridSizeWidth; c++){
                displayGrid[r][c] = -1;
            }
        }
        timer.stop();
        timeElapsed = 0;
        timeSpent.setText(timeElapsed + "");
        currBombs = numBombs;
        bombsRemaining.setText(currBombs+ "");
        if(flagClick.isSelected()){
            flagClick.doClick();
        }
        playingGrid.repaint();
    }
    
    public int[][] newBoard(int height, int width, int numBombs){
        Random ran = new Random();
        int[][] result = new int[height][width];
        int r = 0;
        int c = 0;
        for(int i = 0; i<numBombs; i++){
            r = ran.nextInt(height);
            c = ran.nextInt(width);
            while(result[r][c] == 10){//while there is a mine, try another spot
                r = ran.nextInt(height);
                c = ran.nextInt(width);
            }
            result[r][c] = 10;
        }
        return result;
    }
    
    public void gameOver(){
        timer.stop();
        for(int r = 0; r<gridSizeHeight; r++){
            for(int c = 0; c<gridSizeWidth; c++){
                if(bombGrid[r][c] == 10){
                    displayGrid[r][c] = 10;
                }
            }
        }
        playingGrid.repaint();
        JOptionPane gameOverDialog = new JOptionPane();
        String title = "Game Over!";
        String message = "You hit a mine!\nTime Expired: "+timeElapsed+"\nClick ok for new game";
        gameOverDialog.showMessageDialog(null,message,title,JOptionPane.PLAIN_MESSAGE);
        initializeGame();
        playingGrid.repaint();
    }
    
    public boolean checkGameWon(int[][] board){
        for(int r = 0; r<board.length; r++){
            for(int c = 0; c<board[0].length; c++){
                if(board[r][c] == -1){
                    return false;
                }
                else if(board[r][c] == 9 && bombGrid[r][c] != 10){
                    return false;
                }
                else if(currBombs != 0){
                    return false;
                }
            }
        }
        return true;
    }
    
    public void gameWon(){
        timer.stop();
        JOptionPane won = new JOptionPane();
        String title = "You won!";
        String message = "Congratulations, you won!\nYou solved this level in " + timeElapsed +
                            "seconds,\nPress ok for a new game.";
        won.showMessageDialog(null,message,title,JOptionPane.PLAIN_MESSAGE);
        initializeGame();
        playingGrid.repaint();
    }
    
    public void reveal(int row, int col, int[][] board){
        if(isInBounds(row,col,board)){
            if(bombGrid[row][col] == 10 && board[row][col] != 9){
                //it is game over!
                gameOver();
            }
            else if(board[row][col] == -1){ //blank at that spot
                if(neighbors(row,col,bombGrid,10) == 0){
                    //go into recursion
                    board[row][col] = 0;
                    reveal(row-1, col-1, board);
                    reveal(row-1, col, board);
                    reveal(row-1, col+1, board);
                    reveal(row, col-1, board);
                    reveal(row, col+1, board);
                    reveal(row+1, col-1, board);
                    reveal(row+1, col, board);
                    reveal(row+1, col+1, board);
                }
                else{
                    board[row][col] = neighbors(row,col,bombGrid,10);
                }
            }
        }
    }
    
    public int neighbors(int row, int col, int[][] board, int type){
        int result = 0;
        if(isInBounds(row-1, col-1, board)){
            if(board[row-1][col-1] == type){
                result++;
            }
        }
        
        if(isInBounds(row-1, col, board)){
            if(board[row-1][col] == type){
                result++;
            }
        }
        
        if(isInBounds(row-1, col+1, board)){
            if(board[row-1][col+1] == type){
                result++;
            }
        }
        
        if(isInBounds(row, col-1, board)){
            if(board[row][col-1] == type){
                result++;
            }
        }
        
        if(isInBounds(row, col+1, board)){
            if(board[row][col+1] == type){
                result++;
            }
        }
        
        if(isInBounds(row+1, col-1, board)){
            if(board[row+1][col-1] == type){
                result++;
            }
        }
        
        if(isInBounds(row+1, col, board)){
            if(board[row+1][col] == type){
                result++;
            }
        }
        
        if(isInBounds(row+1, col+1, board)){
            if(board[row+1][col+1] == type){
                result++;
            }
        }
        
        return result;
    }
    
    public boolean isInBounds(int row, int col, int[][] board){
        int numRows = board.length;
        int numCols = board[0].length;
        
        if(row < numRows && col < numCols && row >= 0 && col >= 0){
            return true;
        }
        else{
            return false;
        }
    }
    
    
    
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == timer){
            timeElapsed++;
            timeSpent.setText(timeElapsed + "");
        }
        if(e.getSource() == flagClick){
            if(flagClick.isSelected()){
                flagMode = true;
            }
            else{
                flagMode = false;
            }
        }
        if(e.getSource() == difficulty){
            String selectedDifficulty = (String)difficulty.getSelectedItem();
            if(selectedDifficulty.equals("Easy")){
                gridSizeWidth = 9;
                gridSizeHeight = 9;
                bombGrid = new int[gridSizeHeight][gridSizeWidth];
                displayGrid = new int[gridSizeHeight][gridSizeWidth];
                numBombs = 10;
                currBombs = 10;
                timeElapsed = 0;
                playingGridSizeWidth = gridSizeWidth*20 + 2;
                playingGridSizeHeight = gridSizeHeight*20 + 2;
                frameWidth = playingGridSizeWidth + 40;
                frameHeight = playingGridSizeHeight + 250;
                window.setBounds(screenWidth/2 - frameWidth/2, screenHeight/2 - frameHeight/2, 
                                frameWidth, frameHeight);
                playingGrid.setBounds(20,20,playingGridSizeWidth,playingGridSizeHeight);
                bombsRemaining.setBounds(frameWidth/2, playingGridSizeHeight + 20, bombLabelWidth, bombLabelHeight);
                timeSpent.setBounds(frameWidth/2-timeLabelWidth , 
                            playingGridSizeHeight + 20, timeLabelWidth, timeLabelHeight);
                flagClick.setBounds(frameWidth/2- flagButtonWidth/2, 
                            playingGridSizeHeight + 20 + timeLabelHeight, flagButtonWidth, flagButtonHeight);
                difficulty.setBounds(frameWidth/2- difficultyWidth/2, 
                            playingGridSizeHeight + 20 + timeLabelHeight + flagButtonHeight, 
                            difficultyWidth, difficultyHeight);
                window.repaint();
                initializeGame();
            }
            else if(selectedDifficulty.equals("Intermediate")){
                gridSizeWidth = 16;
                gridSizeHeight = 16;
                bombGrid = new int[gridSizeHeight][gridSizeWidth];
                displayGrid = new int[gridSizeHeight][gridSizeWidth];
                numBombs = 40;
                currBombs = 40;
                timeElapsed = 0;
                playingGridSizeWidth = gridSizeWidth*20 + 2;
                playingGridSizeHeight = gridSizeHeight*20 + 2;
                frameWidth = playingGridSizeWidth + 40;
                frameHeight = playingGridSizeHeight + 250;
                window.setBounds(screenWidth/2 - frameWidth/2, screenHeight/2 - frameHeight/2, 
                                frameWidth, frameHeight);
                playingGrid.setBounds(20,20,playingGridSizeWidth,playingGridSizeHeight);
                bombsRemaining.setBounds(frameWidth/2, playingGridSizeHeight + 20, bombLabelWidth, bombLabelHeight);
                timeSpent.setBounds(frameWidth/2-timeLabelWidth , 
                            playingGridSizeHeight + 20, timeLabelWidth, timeLabelHeight);
                flagClick.setBounds(frameWidth/2- flagButtonWidth/2, 
                            playingGridSizeHeight + 20 + timeLabelHeight, flagButtonWidth, flagButtonHeight);
                difficulty.setBounds(frameWidth/2- difficultyWidth/2, 
                            playingGridSizeHeight + 20 + timeLabelHeight + flagButtonHeight, 
                            difficultyWidth, difficultyHeight);
                window.repaint();
                initializeGame();
            }
            else if(selectedDifficulty.equals("Expert")){
                gridSizeWidth = 30;
                gridSizeHeight = 16;
                bombGrid = new int[gridSizeHeight][gridSizeWidth];
                displayGrid = new int[gridSizeHeight][gridSizeWidth];
                numBombs = 99;
                currBombs = 99;
                timeElapsed = 0;
                playingGridSizeWidth = gridSizeWidth*20 + 2;
                playingGridSizeHeight = gridSizeHeight*20 + 2;
                frameWidth = playingGridSizeWidth + 40;
                frameHeight = playingGridSizeHeight + 250;
                window.setBounds(screenWidth/2 - frameWidth/2, screenHeight/2 - frameHeight/2, 
                                frameWidth, frameHeight);
                playingGrid.setBounds(20,20,playingGridSizeWidth,playingGridSizeHeight);
                bombsRemaining.setBounds(frameWidth/2, playingGridSizeHeight + 20, bombLabelWidth, bombLabelHeight);
                timeSpent.setBounds(frameWidth/2-timeLabelWidth , 
                            playingGridSizeHeight + 20, timeLabelWidth, timeLabelHeight);
                flagClick.setBounds(frameWidth/2- flagButtonWidth/2, 
                            playingGridSizeHeight + 20 + timeLabelHeight, flagButtonWidth, flagButtonHeight);
                difficulty.setBounds(frameWidth/2- difficultyWidth/2, 
                            playingGridSizeHeight + 20 + timeLabelHeight + flagButtonHeight, 
                            difficultyWidth, difficultyHeight);
                window.repaint();
                initializeGame();
            }
            else if(selectedDifficulty.equals("Custom")){
                
            }
        }
    }
    
    public void mouseClicked(MouseEvent e){
        if((e.getButton() == MouseEvent.BUTTON1 && flagMode == false)){
            if(gameStarted == false){
                gameStarted = true;
                timer.start();
            }
            int xPos = e.getX()/20;
            int yPos = e.getY()/20;
            if(displayGrid[yPos][xPos] == -1){
                reveal(yPos, xPos, displayGrid);
            }
            else if(displayGrid[yPos][xPos] != 9 && displayGrid[yPos][xPos] != 0){
                if(displayGrid[yPos][xPos] == neighbors(yPos,xPos,displayGrid,9)){
                    reveal(yPos-1, xPos-1, displayGrid);
                    reveal(yPos-1, xPos, displayGrid);
                    reveal(yPos-1, xPos+1, displayGrid);
                    reveal(yPos, xPos-1, displayGrid);
                    reveal(yPos, xPos+1, displayGrid);
                    reveal(yPos+1, xPos-1, displayGrid);
                    reveal(yPos+1, xPos, displayGrid);
                    reveal(yPos+1, xPos+1, displayGrid);
                }
            }
            
            playingGrid.repaint();
            if(checkGameWon(displayGrid)){
                gameWon();
            }
        }
        
        else if((e.getButton() == MouseEvent.BUTTON3 && flagMode == false)){
            int xPos = e.getX()/20;
            int yPos = e.getY()/20;
            if(displayGrid[yPos][xPos] == -1){
                displayGrid[yPos][xPos] = 9;
                currBombs--;
                bombsRemaining.setText(currBombs + "");
            }
            else if(displayGrid[yPos][xPos] == 9){
                displayGrid[yPos][xPos] = -1;
                currBombs++;
                bombsRemaining.setText(currBombs + "");
            }
            playingGrid.repaint();
            if(checkGameWon(displayGrid)){
                gameWon();
            }
        }
        else if(e.getButton() == MouseEvent.BUTTON3 && flagMode == true){
            if(gameStarted == false){
                gameStarted = true;
                timer.start();
            }
            int xPos = e.getX()/20;
            int yPos = e.getY()/20;
            if(displayGrid[yPos][xPos] == -1){
                reveal(yPos, xPos, displayGrid);
            }
            
            
            playingGrid.repaint();
            if(checkGameWon(displayGrid)){
                gameWon();
            }
        }
        else if(e.getButton() == MouseEvent.BUTTON1 && flagMode == true){
            int xPos = e.getX()/20;
            int yPos = e.getY()/20;
            if(displayGrid[yPos][xPos] == -1){
                displayGrid[yPos][xPos] = 9;
                currBombs--;
                bombsRemaining.setText(currBombs + "");
            }
            else if(displayGrid[yPos][xPos] == 9){
                displayGrid[yPos][xPos] = -1;
                currBombs++;
                bombsRemaining.setText(currBombs + "");
            }
            else if(displayGrid[yPos][xPos] != 9 && displayGrid[yPos][xPos] != 0){
                if(displayGrid[yPos][xPos] == neighbors(yPos,xPos,displayGrid,9)){
                    reveal(yPos-1, xPos-1, displayGrid);
                    reveal(yPos-1, xPos, displayGrid);
                    reveal(yPos-1, xPos+1, displayGrid);
                    reveal(yPos, xPos-1, displayGrid);
                    reveal(yPos, xPos+1, displayGrid);
                    reveal(yPos+1, xPos-1, displayGrid);
                    reveal(yPos+1, xPos, displayGrid);
                    reveal(yPos+1, xPos+1, displayGrid);
                }
            }
            playingGrid.repaint();
            if(checkGameWon(displayGrid)){
                gameWon();
            }
        }
    }
    
    
    private class MyJPanel extends JPanel{
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            
            int gridLength = 20;
            g2.setFont(new Font("Sansserif", Font.BOLD, 22));
            for(int r = 0; r<gridSizeHeight; r++){
                for(int c = 0; c<gridSizeWidth; c++){
                    g2.drawImage(blankMine,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                    double x = c + .1;
                    double y = r+.9;
                    if(displayGrid[r][c] == 0){
                        g2.setColor(new Color(58,95,205));
                        g2.drawImage(noMine,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                    }
                    else if(displayGrid[r][c] == 1){
                        g2.setColor(new Color(58,95,205));
                        g2.drawImage(noMine,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                        g2.drawString("1", (int)(1 + (x)*gridLength), (int)(1 + (y)*gridLength));
                    }
                    else if(displayGrid[r][c] == 2){
                        g2.setColor(new Color(219,112,147));
                        g2.drawImage(noMine,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                        g2.drawString("2", (int)(1 + (x)*gridLength), (int)(1 + (y)*gridLength));
                    }
                    else if(displayGrid[r][c] == 3){
                        g2.setColor(new Color(0,128,0));
                        g2.drawImage(noMine,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                        g2.drawString("3", (int)(1 + (x)*gridLength), (int)(1 + (y)*gridLength));
                    }
                    else if(displayGrid[r][c] == 4){
                        g2.setColor(new Color(218,165,32));
                        g2.drawImage(noMine,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                        g2.drawString("4", (int)(1 + (x)*gridLength), (int)(1 + (y)*gridLength));
                    }
                    else if(displayGrid[r][c] == 5){
                        g2.setColor(new Color(139,0,139));
                        g2.drawImage(noMine,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                        g2.drawString("5", (int)(1 + (x)*gridLength), (int)(1 + (y)*gridLength));
                    }
                    else if(displayGrid[r][c] == 6){
                        g2.setColor(new Color(105,139,34));
                        g2.drawImage(noMine,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                        g2.drawString("6", (int)(1 + (x)*gridLength), (int)(1 + (y)*gridLength));
                    }
                    else if(displayGrid[r][c] == 7){ 
                        g2.setColor(new Color(139,58,58));
                        g2.drawImage(noMine,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                        g2.drawString("7", (int)(1 + (x)*gridLength), (int)(1 + (y)*gridLength));
                    }
                    else if(displayGrid[r][c] == 8){ 
                        g2.setColor(Color.white);
                        g2.drawImage(noMine,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                        g2.drawString("8", (int)(1 + (x)*gridLength), (int)(1 + (y)*gridLength));
                    }
                    else if(displayGrid[r][c] == 9){ //flag
                        g2.setColor(Color.blue);
                        g2.drawImage(blankMine,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                        g2.drawImage(flag,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                    }
                    else if(displayGrid[r][c] == 10){ //bomb
                        g2.setColor(Color.blue);
                        g2.drawImage(noMine,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                        g2.drawImage(yesMine,1 + c*gridLength, 1 + r*gridLength, gridLength, gridLength, null, null);
                    }
                }
            }
        }
    }
}