package frame;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import model.javaBean.AppUser;
import service.AppUserService;
import service.MovieReviewService;
import service.MovieSystemService;

/**
 * MainFrame.java
 * 程式主入口，整合用戶管理、電影搜尋、撰寫評論面板
 * 需在 UserPanel 登入成功時觸發 PropertyChange "login"
 */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private AppUserService userService;
    private MovieSystemService movieService;
    private MovieReviewService reviewService;

    private UserPanel userPanel;
    private MovieSystemPanel moviePanel;
    private ReviewPanel reviewPanel;
    private JTabbedPane tabs;

    public MainFrame() {
        userService = new AppUserService();
        movieService = new MovieSystemService();
        reviewService = new MovieReviewService();

        // Initial panel
        userPanel = new UserPanel(userService);
        moviePanel = new MovieSystemPanel(movieService);
        reviewPanel = new ReviewPanel(userService, movieService, reviewService);

        // Create Pagination
        tabs = new JTabbedPane();
        tabs.addTab("帳號設定", userPanel);
        tabs.addTab("來點電影", moviePanel);
        tabs.addTab("會員專區", reviewPanel);
        // lock members area
        tabs.setEnabledAt(2, false);

        // Main window
        setTitle("電影推薦系統");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);

        // listener for login
        PropertyChangeListener loginListener = evt -> {
            AppUser user = (AppUser) evt.getNewValue();
            // record user name
            reviewPanel.enableWriteReview(user.username());
            // unlock member area
            tabs.setEnabledAt(2, true);
            // switch to movie list
            tabs.setSelectedIndex(1);
        };
        userPanel.addPropertyChangeListener("login", loginListener);
        
        // listener for logout
        PropertyChangeListener logoutListener = evt -> {
            tabs.setEnabledAt(2, false);
            // return login UI
            tabs.setSelectedIndex(0);
        };
        userPanel.addPropertyChangeListener("logout", logoutListener);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
