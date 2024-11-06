
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Menu extends JPanel {

    // import wallpaper
    public ImageIcon wallpaper = new ImageIcon(this.getClass().getResource("wall1.gif"));

    // Start&Exit button
    private ImageIcon starts = new ImageIcon(this.getClass().getResource("start.png"));
    public JButton Start = new JButton(starts);

    private ImageIcon exits = new ImageIcon(this.getClass().getResource("exit.png"));
    public JButton Exit = new JButton(exits);

    Menu() {

        setLayout(null);

        // Set Button Location
        this.add(Start);
        Start.setBounds(180, 200, 387, 67);
        this.add(Exit);
        Exit.setBounds(210, 300, 337, 52);

    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        // setColor
        g.setColor(Color.WHITE);

        // paint wallpaper
        g.drawImage(wallpaper.getImage(), 0, 0, 800, 600, this);

        // setFont
        g.setFont(new Font("Arcade Gamer", Font.CENTER_BASELINE, 50));
        g.drawString("Terrorist Hunt Game", 150, 100);

    }

}
