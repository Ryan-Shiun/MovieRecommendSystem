package frame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import exception.DataAccessRuntimeException;
import exception.EmailAlreadyExistsException;
import exception.InvalidCredentialException;
import exception.UserAlreadyExistsException;
import exception.UserNotFoundException;
import model.javaBean.AppUser;
import service.AppUserService;

public class UserPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private AppUserService userService;
    private AppUser currentUser;

    private CardLayout cardLayout;
    private JPanel cards;

    // 登入／註冊 元件
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JTextField registerEmailField;
    private JButton loginBtn;
    private JButton registerBtn;
    private JTextArea loginOutputArea;

    // 帳戶管理 元件
    private JLabel welcomeLabel;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton updateEmailBtn;
    private JButton updatePasswordBtn;
    private JButton deleteUserBtn;
    private JButton logoutBtn;
    private JTextArea manageOutputArea;

    public UserPanel(AppUserService service) {
        this.userService = service;
        initComponents();
    }
    
    private void initComponents() {
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        cards.add(createLoginPanel(), "LOGIN");
        cards.add(createManagePanel(), "MANAGE");

        setLayout(new BorderLayout());
        add(cards, BorderLayout.CENTER);
        cardLayout.show(cards, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("登入 / 註冊 ( 首次登入請先註冊 )"));

        form.add(new JLabel("帳號："));
        loginUsernameField = new JTextField();
        form.add(loginUsernameField);

        form.add(new JLabel("密碼："));
        loginPasswordField = new JPasswordField();
        form.add(loginPasswordField);

        form.add(new JLabel("Email（註冊用）："));
        registerEmailField = new JTextField();
        form.add(registerEmailField);

        loginBtn = new JButton("登入");
        form.add(loginBtn);
        registerBtn = new JButton("註冊");
        form.add(registerBtn);

        panel.add(form, BorderLayout.NORTH);

        loginOutputArea = new JTextArea(5, 20);
        loginOutputArea.setEditable(false);
        panel.add(new JScrollPane(loginOutputArea), BorderLayout.CENTER);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> handleRegister());

        return panel;
    }

    private JPanel createManagePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // userName
        welcomeLabel = new JLabel();
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(10));

        // update email
        JPanel emailRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailRow.add(new JLabel("更新 Email: "));
        emailField = new JTextField(20);
        emailRow.add(emailField);
        updateEmailBtn = new JButton("更新 Email");
        emailRow.add(updateEmailBtn);
        panel.add(emailRow);
        panel.add(Box.createVerticalStrut(10));

        // update password
        JPanel passRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        passRow.add(new JLabel("更新密碼: "));
        passwordField = new JPasswordField(20);
        passRow.add(passwordField);
        updatePasswordBtn = new JButton("更新密碼");
        passRow.add(updatePasswordBtn);
        panel.add(passRow);
        panel.add(Box.createVerticalStrut(10));

        // delete and logout
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteUserBtn = new JButton("刪除帳號");
        logoutBtn = new JButton("登出");
        actionRow.add(deleteUserBtn);
        actionRow.add(logoutBtn);
        panel.add(actionRow);
        panel.add(Box.createVerticalStrut(10));

        // message output
        manageOutputArea = new JTextArea(4, 30);
        manageOutputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(manageOutputArea);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scroll);

        // listener
        updateEmailBtn.addActionListener(e -> handleUpdateEmail());
        updatePasswordBtn.addActionListener(e -> handleUpdatePassword());
        deleteUserBtn.addActionListener(e -> handleDeleteUser());
        logoutBtn.addActionListener(e -> handleLogout());

        return panel;
    }
    /* ================== 邏輯處理 ================== */
    // login
    private void handleLogin() {
        String userName = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        if (userName.isBlank() | password.isBlank()) {
        	JOptionPane.showMessageDialog(this, "欄位不可為空 !");
        	return;
        }
        
        try {
        	Optional<AppUser> opt = userService.login(userName, password);
        	currentUser = opt.get();
        	// renew interface
            loginOutputArea.setText("登入成功，歡迎：" + currentUser.username());
            loadUserInfo();
            
            // send login event to MainFrame for unlock and switch to movie list
            firePropertyChange("login", null, currentUser);
            
            // switch to user management UI
            cardLayout.show(cards, "MANAGE");

		} catch (UserNotFoundException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "兄弟 還請你先註冊喔", JOptionPane.WARNING_MESSAGE);

		} catch (InvalidCredentialException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), " 再打一次 ", JOptionPane.WARNING_MESSAGE);
		}
    }
    
    // register
    private void handleRegister() {
        String userName = loginUsernameField.getText().trim();
        String email = registerEmailField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        
        if (userName.isBlank() || email.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "資料填好再來 (๑>ᴗ<๑)");
            return;
        }
        
        try {
			userService.register(userName, email, password);
			JOptionPane.showMessageDialog(this, "(・∀・)b 用戶建立成功！");
		} catch (UserAlreadyExistsException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "再想個帥名字", JOptionPane.WARNING_MESSAGE);
		}
    }
    
    // email
    private void handleUpdateEmail() {
        String newEmail = emailField.getText().trim();
        if (newEmail.isBlank()) {
        	JOptionPane.showMessageDialog(this, "欄位不可為空");
        	return;
        }
        try {
			userService.updateEmail(currentUser.userId(), newEmail);
			JOptionPane.showMessageDialog(this, "更新成功 ＼(^o^)／");
		} catch (EmailAlreadyExistsException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), " 請使用別的 Email ", JOptionPane.WARNING_MESSAGE);
		}

    }
    
    // update password
    private void handleUpdatePassword() {
        String newPwd = new String(passwordField.getPassword());
        if (newPwd.isBlank()) {
        	JOptionPane.showMessageDialog(this, "密碼打一下啦 (￣ー￣) ");
        	return;
        }
        try {
			userService.updatePassword(currentUser.userId(), newPwd);
			JOptionPane.showMessageDialog(this, "更新成功 ＼(^o^)／");
		} catch (DataAccessRuntimeException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "", JOptionPane.WARNING_MESSAGE);
		}
    }
    
    // delete account
    private void handleDeleteUser() {
        JOptionPane.showConfirmDialog(this, "確定要刪除帳號？ 真的不用了嗎 (╥_╥) ", "刪除確認", JOptionPane.YES_NO_OPTION);
        try {
			userService.deleteUser(currentUser.userId());
		} catch (DataAccessRuntimeException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "", JOptionPane.WARNING_MESSAGE);
		}
    }
    
    // show user info
    private void loadUserInfo() {
        welcomeLabel.setText("用戶：" + currentUser.username());
        emailField.setText(currentUser.email());
        passwordField.setText("");
        manageOutputArea.setText("");
    }
    
    // logout
    private void handleLogout() {
        AppUser old = currentUser;
        currentUser = null;
        // ★ send logout event to MainFrame lock Members Area
        firePropertyChange("logout", old, null);

        loginUsernameField.setText("");
        loginPasswordField.setText("");
        registerEmailField.setText("");
        loginOutputArea.setText("");
        cardLayout.show(cards, "LOGIN");
    }
}

