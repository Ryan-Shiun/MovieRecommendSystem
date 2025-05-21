package frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import exception.DataAccessRuntimeException;
import exception.ReviewAlreadyExistsException;
import exception.ReviewNotFoundException;
import model.api.MovieItem;
import model.javaBean.AppUser;
import model.javaBean.MovieReview;
import service.AppUserService;
import service.MovieReviewService;
import service.MovieSystemService;

public class ReviewPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private AppUserService userService;
    private MovieSystemService movieService;
    private MovieReviewService reviewService;

    private JTabbedPane tabbedPane;
    
    
    // manage review page
    private JPanel managePanel;
    private JTextField manageMovieTitleField;
    private JButton deleteBtn, toggleVisibilityBtn;
    private DefaultTableModel favoritesTableModel;  
    private JTable favoritesTable;
    private JButton updateBtn;
    private JTextField manageReviewField;
    private JLabel userNameLabel;
    private JComboBox<Integer> ratingBox;
    private JButton submitBtn;
    
    // public review page
    private JPanel queryPanel;
    private JComboBox<String> orderByCombo;
    private JButton findAllBtn, searchByNameBtn;
    private JTextField searchUserField;
    private JTable queryTable;
    private DefaultTableModel queryModel;
    private JButton exportBtn;

    // save user current info
    private int currentUserId;
    private List<MovieReview> currentQueryList;

    public ReviewPanel(AppUserService userService,
                       MovieSystemService movieService,
                       MovieReviewService reviewService) {
        this.userService = userService;
        this.movieService = movieService;
        this.reviewService = reviewService;
        initComponents();
    }
    

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        

        queryPanel = createQueryPanel();
        managePanel = createManagePanel();

        tabbedPane.addTab("公開評論", queryPanel);
        tabbedPane.addTab("我的評論", managePanel);
        

        tabbedPane.setEnabledAt(0, false);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    // public review area
    private JPanel createQueryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
            	handleQueryAll();
            }
        });

        top.add(new JLabel("排序依據:"));
        orderByCombo = new JComboBox<>(new String[]{"popularity", "user_rating"});
        top.add(orderByCombo);

        findAllBtn = new JButton("按條件排序"); 
        top.add(findAllBtn);       
        top.add(new JLabel("使用者名稱:"));
        
        searchUserField = new JTextField(10); 
        top.add(searchUserField);
        searchByNameBtn = new JButton("查詢指定用戶評論"); 
        top.add(searchByNameBtn);
        panel.add(top, BorderLayout.NORTH);

  
        String[] cols = {"用戶", "電影", "熱度", "評分", "評論內容"};
        queryModel = new DefaultTableModel(cols, 0) {

			private static final long serialVersionUID = 1L;

			@Override public boolean isCellEditable(int row, int col) { return false; }
        };
        queryTable = new JTable(queryModel);
        queryTable.setFillsViewportHeight(true);
        panel.add(new JScrollPane(queryTable), BorderLayout.CENTER);


        findAllBtn.addActionListener(e -> handleQueryAll());
        searchByNameBtn.addActionListener(e -> handleQueryByName());
        return panel;
    }    
    
    // my review
    private JPanel createManagePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("私有評論"));
        
        // Automatic show my review 
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadFavorites();
            }
        });
        
        JLabel favLabel = new JLabel();
        favLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(favLabel);


        String[] cols = {"電影名稱", "熱度", "評分", "評論"};
        favoritesTableModel = new DefaultTableModel(cols, 0);
        favoritesTable = new JTable(favoritesTableModel);
        JScrollPane favScroll = new JScrollPane(favoritesTable);
        favScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        favScroll.setPreferredSize(new Dimension(400, 150));
        panel.add(favScroll);

        panel.add(Box.createVerticalStrut(10));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        userNameLabel = new JLabel();

        
        // movie input
        inputPanel.add(new JLabel("電影名稱 :"));
        manageMovieTitleField = new JTextField(15);
        inputPanel.add(manageMovieTitleField);

        // comment input
        inputPanel.add(new JLabel("影評 :"));
        manageReviewField = new JTextField(25);
        inputPanel.add(manageReviewField);
        inputPanel.add(new JLabel("評分（1-5）："));
        
        // rating choose
        ratingBox = new JComboBox<>();
        for (int i = 1; i <= 5; i++) ratingBox.addItem(i);
        ratingBox.setPreferredSize(new Dimension(50, 25));
        inputPanel.add(ratingBox);

        // add review
        submitBtn = new JButton("新增評論");
        submitBtn.setPreferredSize(new Dimension(100, 30));
        inputPanel.add(submitBtn);
        
        // public review
        toggleVisibilityBtn = new JButton("發表評論");
        toggleVisibilityBtn.setPreferredSize(new Dimension(100, 30));
        inputPanel.add(toggleVisibilityBtn);
        
        // delete review
        deleteBtn = new JButton("刪除評論");
        deleteBtn.setPreferredSize(new Dimension(100, 30));
        inputPanel.add(deleteBtn);
        
        // renew review
        updateBtn = new JButton("更新評論");
        updateBtn.setPreferredSize(new Dimension(100, 30));
        inputPanel.add(updateBtn);

        // export review
        exportBtn = new JButton("匯出我的評論"); 
        exportBtn.setPreferredSize(new Dimension(120, 30));
        panel.add(exportBtn);

        panel.add(inputPanel);

        submitBtn.addActionListener(e -> handleWrite());
        deleteBtn.addActionListener(e -> handleDelete());
        updateBtn.addActionListener(e -> updateReview());
        toggleVisibilityBtn.addActionListener(e -> publishReview());
        exportBtn.addActionListener(e -> handleExport());

        return panel;
    }
       
    
    /* ================================ 邏輯處理 ============================= */
 
    // login save user name and user ID
    public void enableWriteReview(String userName) {
                
        try {
        	Optional<AppUser> opt = userService.getUserByName(userName);
            currentUserId = opt.get().userId();
        } catch (Exception e) {
        	JOptionPane.showMessageDialog(this, "讀取失敗: " + e.getMessage());
            currentUserId = -1;
        }	
        userNameLabel.setText(userName);
        tabbedPane.setEnabledAt(0, true);
        tabbedPane.setSelectedIndex(0);
    }
       
    // add review
    private void handleWrite() {
        String title = manageMovieTitleField.getText().trim();
        int score = (Integer) ratingBox.getSelectedItem();
        String content = manageReviewField.getText().trim();
        if (currentUserId < 0 || title.isBlank() || content.isBlank()) {
            JOptionPane.showMessageDialog(this, "欄位不可為空 !");
            return;
        }
        try {       	
            List<MovieItem> movies = movieService.getMovieByTitle(title);
            int movieId = movies.isEmpty() ? -1 : movies.get(0).id();
            reviewService.postReview(currentUserId, movieId, score, content);
            loadFavorites();
            JOptionPane.showMessageDialog(this, "新增成功！");
        } catch (ReviewAlreadyExistsException e) {
        	JOptionPane.showMessageDialog(this, "" + e.getMessage());
		} catch (DataAccessRuntimeException e1) {
			JOptionPane.showMessageDialog(this, "" + e1.getMessage());
		}
    }

    // delete review
    private void handleDelete() {
        String title = manageMovieTitleField.getText().trim();
        if (currentUserId < 0 || title.isBlank() ) {
        	JOptionPane.showMessageDialog(this, "沒給電影名字是要怎麼刪 (・ω・`) ");
            return;
        }
        try {
        	List<MovieReview> reviews = reviewService.getPrivewReview(currentUserId, title); // 只能刪除公開評論
        	int mid = reviews.isEmpty() ? -1 : reviews.get(0).movieId();
            reviewService.deleteRecommendation(currentUserId, mid);
            JOptionPane.showMessageDialog(this, "評論已刪除");
            loadFavorites();
        } catch (ReviewNotFoundException ex) {	
            JOptionPane.showMessageDialog(this, "尚未對電影: " + title + " 進行評論", "錯誤", JOptionPane.ERROR_MESSAGE);
        } catch (DataAccessRuntimeException e1) {
        	JOptionPane.showMessageDialog(this, "", "", JOptionPane.ERROR_MESSAGE);
		} 
    }
    
    // renew review
    private void updateReview() {
        String title = manageMovieTitleField.getText().trim();
        int score = (Integer) ratingBox.getSelectedItem();
        String content = manageReviewField.getText().trim();
        if (currentUserId < 0 || title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "欄位沒寫是要我通靈嗎 (¬_¬) ");
            return;
        }
        try {
            reviewService.updateReview(currentUserId, title, score, content);
            loadFavorites();
            JOptionPane.showMessageDialog(this, "修改成功！");
        } catch (ReviewNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "找不到電影: " + title + " 相關評論" ,"錯誤",  JOptionPane.ERROR_MESSAGE);
        } catch (DataAccessRuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex + "資料庫忙線中 稍後在試", "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    // publish review
    private void publishReview() {
        String title = manageMovieTitleField.getText().trim();
        if (currentUserId < 0 || title.isBlank() ) {
        	JOptionPane.showMessageDialog(this, "沒給電影名字是要我通靈嗎 (¬_¬) ");
            return;
        }       
        try {
        	List<MovieReview> checkPublish = reviewService.getPublicReviw(currentUserId, title);
        	if (!checkPublish.isEmpty()) {
        		JOptionPane.showMessageDialog(this, "兄弟 你已經發表過啦 (-_-)");
        		return;
        	}
        	List<MovieReview> reviews = reviewService.getPrivewReview(currentUserId, title);        	
        	int confirm = JOptionPane.showConfirmDialog(null, "評論發表就無法更動囉，確定要發表嗎 ?", "確認", JOptionPane.YES_NO_OPTION);
        	if (confirm == JOptionPane.YES_OPTION) {       		
        		reviewService.publicReview(currentUserId, reviews.get(0).movieId());
        		loadFavorites();
        		JOptionPane.showMessageDialog(this, "發表成功 (≧▽≦)");
        	}
        } catch (DataAccessRuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex + "資料庫忙線中 稍後在試", "錯誤", JOptionPane.ERROR_MESSAGE);
        } catch (ReviewNotFoundException e) {
        	JOptionPane.showMessageDialog(this, "尚未對電影： " + title + " 進行評論",  "錯誤", JOptionPane.ERROR_MESSAGE);
		}
    }
    
    // get all public review
    private void handleQueryAll() {
        try {
            String order = (String) orderByCombo.getSelectedItem();
            currentQueryList = reviewService.findAllPublic(order);
            refreshQueryTable();
        } catch (ReviewNotFoundException e) {
            JOptionPane.showMessageDialog(this, "" + e.getMessage());
        } catch (DataAccessRuntimeException e1) {
        	JOptionPane.showMessageDialog(this, "", "", JOptionPane.ERROR_MESSAGE);
		}
    }
    
    // sort public review
    private void refreshQueryTable() {
    	if (currentQueryList.isEmpty() | currentQueryList.isEmpty()) {
    		System.out.println("空的");
    	}
        queryModel.setRowCount(0);
        for (MovieReview r: currentQueryList) {
            String userName = userService.getUserById(r.userId())
                .map(AppUser::username).orElse("Unknown");
            String rating = String.format("%.0f", r.popularity());
            queryModel.addRow(new Object[]{
                userName,
                r.title(),
                rating,
                r.userRating(),
                r.comment()
            });
        }
    }
    
    
    // get public review by user name
    private void handleQueryByName() {
        try {
        	String movieName = searchUserField.getText().trim();
        	if (movieName.isBlank()) {
        		JOptionPane.showMessageDialog(this, "輸入個大名給我 (-_-)");
        		return;
        	}
            currentQueryList = reviewService.searchByName(movieName);
            refreshQueryTable();
        } catch (ReviewNotFoundException e) {
            JOptionPane.showMessageDialog(this, "" + e.getMessage());
        } catch (DataAccessRuntimeException e1) {
        	JOptionPane.showMessageDialog(this, "", "", JOptionPane.ERROR_MESSAGE);
		}
    }
    
    // show private review
    private void loadFavorites() {
        // clear old data
        favoritesTableModel.setRowCount(0);
        List<MovieReview> reviews;
		try {
			reviews = reviewService.findAllPrivate(currentUserId);
	        for (MovieReview r : reviews) {
	            favoritesTableModel.addRow(new Object[]{
	                r.title(),
	                r.popularity(),
	                r.userRating(),
	                r.comment()
	            });
	        }
		} catch (DataAccessRuntimeException e1) {
        	JOptionPane.showMessageDialog(this, "", "", JOptionPane.ERROR_MESSAGE);
		}
        
    }
    
    // export private review
    private void handleExport() {
    	try {
			List<MovieReview> reviews = reviewService.findAllPrivate(currentUserId);
			if (reviews == null | reviews.isEmpty()) {
				JOptionPane.showMessageDialog(this, "沒有可匯出的評論資料");
				return;
			}
			
	        JFileChooser chooser = new JFileChooser();
	        chooser.setDialogTitle("選擇儲存位置");
	        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
	            File file = chooser.getSelectedFile();
                reviewService.exportCsv(currentQueryList, file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "成功匯出: " + file.getAbsolutePath());
	        }
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "匯出失敗: " + e.getMessage());
		}
    }
}
