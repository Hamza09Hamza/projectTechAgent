package part2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public class AuctionInterface {
    
  private static AuctionInterface INSTANCE;
    
    
    private final JFrame mainWindow;
    private final JTextArea logArea;
    private final JPanel statusPanel;
    private final JLabel statusLabel;
    
    private AuctionInterface() {
        mainWindow = new JFrame("Auction Monitoring System");
        mainWindow.setSize(600, 450);
        mainWindow.setLayout(new BorderLayout(10, 10));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(51, 102, 153));
        JLabel title = new JLabel("Multi-Agent Auction System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(title);
        mainWindow.add(titlePanel, BorderLayout.NORTH);
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(logArea);
        mainWindow.add(scrollPane, BorderLayout.CENTER);
        
        // Status panel at the bottom
        statusPanel = new JPanel();
        statusPanel.setBackground(new Color(240, 240, 240));
        statusLabel = new JLabel("Auction status: Waiting for bids");
        statusPanel.add(statusLabel);
        mainWindow.add(statusPanel, BorderLayout.SOUTH);
        
        // Configure window
        mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                INSTANCE = null;
            }
        });
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }
    
    /**
     * Get the singleton instance
     */
    public static AuctionInterface getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AuctionInterface();
        }
        return INSTANCE;
    }
    
    /**
     * Add message to the log
     */
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            // Auto-scroll to the bottom
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    /**
     * Display the auction winner
     */
    public void displayWinner(String seller, double score) {
        SwingUtilities.invokeLater(() -> {
            // Format the winner announcement
            StringBuilder sb = new StringBuilder();
            sb.append("\n-------------------------------------------\n");
            sb.append("🏆 AUCTION RESULT 🏆\n");
            sb.append("-------------------------------------------\n");
            sb.append("WINNER: ").append(seller).append("\n");
            sb.append("SCORE:  ").append(String.format("%.4f", score)).append("\n");
            sb.append("-------------------------------------------\n");
            
            // Update log and status
            logArea.append(sb.toString());
            statusLabel.setText("Auction status: Completed - Winner: " + seller);
            
            // Highlight the winner panel
            statusPanel.setBackground(new Color(200, 255, 200));
        });
    }
} 