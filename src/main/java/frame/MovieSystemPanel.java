package frame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import exception.DataAccessRuntimeException;
import exception.MovieNotFoundException;
import model.api.MovieItem;
import service.MovieSystemService;

public class MovieSystemPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private MovieSystemService movieService;
    private JTextField movieTitleField;
    private JButton searchBtn, allBtn, popBtn, voteBtn;
    private JTable table;
    private DefaultTableModel tableModel;

    public MovieSystemPanel(MovieSystemService service) {
        this.movieService = service;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("電影名稱:"));
        movieTitleField = new JTextField(15);
        top.add(movieTitleField);
        searchBtn = new JButton("電影名稱查詢");
        top.add(searchBtn);

        allBtn = new JButton("全部電影"); 
        popBtn = new JButton("熱度 Top10");
        voteBtn = new JButton("評分 Top10");
        top.add(allBtn); 
        top.add(popBtn);
        top.add(voteBtn);

        add(top, BorderLayout.NORTH);

        // JTable list
        String[] columns = {"ID", "標題", "熱度", "綜合評分"};
        tableModel = new DefaultTableModel(columns, 0) {

			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
				// can't editor cell
                return false; 
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // listener
        searchBtn.addActionListener(e -> searchByTitle());
        allBtn.addActionListener(e -> showAllMovies());
        popBtn.addActionListener(e -> showTopByPop());
        voteBtn.addActionListener(e -> showTopByVote());
        
        // load all movie at first
        SwingUtilities.invokeLater(this::showAllMovies);
    }
    
    
    /* ======================= 邏輯控制 ====================== */
    
    // get movie by title
    private void searchByTitle() {
        String title = movieTitleField.getText().trim();
        if (title.isBlank()) {
            JOptionPane.showMessageDialog(this, "(⁀ᗢ⁀) 請輸入電影名稱！");
            return;
        }
        try {
            List<MovieItem> items = movieService.getMovieByTitle(title);
            tableModel.setRowCount(0);
            if (items == null | items.isEmpty()) {
            	JOptionPane.showMessageDialog(this, "抱歉 電影查無資料 ❌");
            }
            for (MovieItem mi : items) {
                String pop = String.format("%.0f", mi.popularity());
                String vote = String.format("%.1f", mi.voteAverage());
                tableModel.addRow(new Object[]{ mi.id(), mi.title(), pop, vote });
            }
        } catch (MovieNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "查詢失敗 " + ex.getMessage());
        }
    }
    
    // show all movie
    private void showAllMovies() {
        try {
            List<MovieItem> list = movieService.getAllMovies();
            displayMovies(list);
        } catch (DataAccessRuntimeException ex) {
            JOptionPane.showMessageDialog(this, "讀取失敗: " + ex.getMessage());
        }
    }
    
    // show top 10 movie
    private void showTopByPop() {
        try {
            List<MovieItem> list = movieService.getTop10ByPopularity();
            displayMovies(list);
        } catch (DataAccessRuntimeException ex) {
            JOptionPane.showMessageDialog(this, "讀取失敗: " + ex.getMessage());
        }
    }
    
    // show top 10 vote movie
    private void showTopByVote() {
        try {
            List<MovieItem> list = movieService.getTop10ByVoteAverage();
            displayMovies(list);
        } catch (DataAccessRuntimeException ex) {
            JOptionPane.showMessageDialog(this, "讀取失敗: " + ex.getMessage());
        }
    }
    
    // print movie on table
    private void displayMovies(List<MovieItem> list) {
        tableModel.setRowCount(0);
        for (MovieItem m : list) {
            String pop = String.format("%.0f", m.popularity());
            String vote = String.format("%.1f", m.voteAverage());
            tableModel.addRow(new Object[]{
                m.id(),
                m.title(),
                pop,
                vote
            });
        }
    }
}

