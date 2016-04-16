package smm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.auth.LoginAdapter;
import org.jdesktop.swingx.auth.LoginEvent;
import org.jdesktop.swingx.auth.LoginListener;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

@SuppressWarnings("serial")
public class MainForm extends JPanel {
	DecimalFormat df = new DecimalFormat("#,###.##");

	private JXTaskPane sohuuCPGroup;
	private JXTaskPane xemCPGroup;

	JXTaskPaneContainer mainPanel;
	JXLoginPane loginPanel;

	public static Properties properties = new Properties();

	private static MainForm me;

	// ------------------------------------------------

	public MainForm() {
		super(new BorderLayout());
	}

	// ------------------------------------------------
	/**
	 * main method allows us to run as a standalone demo.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				me = new MainForm();
				me.showLoginDialog();
			}
		});
	}

	// ------------------------------------------------
	private User currentUser;

	private void showLoginDialog() {

		JFrame loginFrame = new JFrame("Welcome!");
		loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		final JXLoginPane loginPane = new JXLoginPane();

		LoginListener loginListener = new LoginAdapter() {
			@Override
			public void loginFailed(LoginEvent source) {
				String message = "Username hoặc Password không đúng!";
				loginPane.setErrorMessage(message);
			}

			@Override
			public void loginSucceeded(LoginEvent source) {
				me.showMainFrame();
			}
		};

		LoginService loginService = new LoginService() {
			@Override
			public boolean authenticate(String name, char[] password, String server) throws Exception {
				currentUser = SmmServer.getInstance().login(name, new String(password));

				return currentUser != null;
			}
		};

		loginService.addLoginListener(loginListener);
		loginPane.setLoginService(loginService);
		JXLoginPane.JXLoginDialog dialog = new JXLoginPane.JXLoginDialog(loginFrame, loginPane);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);

		// if loginPane was cancelled or closed then its status is CANCELLED
		// and still need to dispose main JFrame to exiting application
		if (loginPane.getStatus() == JXLoginPane.Status.CANCELLED) {
			loginFrame.dispatchEvent(new WindowEvent(loginFrame, WindowEvent.WINDOW_CLOSING));
		}
	}

	// ------------------------------------------------
	JFrame mainFrame;
	JXButton btnLogout;
	JXPanel panelTheodoiCP;
	JXTable tableSohuu;
	JXPanel panelMainSohuu;
	JXLabel labelUsername, labelInfoCPMuaban, labelInfoTienmat, labelInfoXemCP;

	private void showMainFrame() {
		createMainframe();
	}

	private void createMainframe() {
		if (mainFrame == null) {
			createMainPanel();

			mainFrame = new JFrame("Stock Manager");

			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainFrame.setPreferredSize(new Dimension(800, 700));
			mainFrame.getContentPane().add(mainPanel);
			mainFrame.pack();
			mainFrame.setLocationRelativeTo(null);
		}

		mainFrame.setVisible(true);

		reloadDSSohuu();
		reloadDSXemCP();
		labelUsername.setText("Chào " + currentUser.getHoten());
	}

	private void createMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JXTaskPaneContainer();

			// "Logout" PANEL
			JXPanel logoutPanel = new JXPanel(new FlowLayout(FlowLayout.RIGHT, 5, 3));
			btnLogout = new JXButton("Thoát");
			labelUsername = new JXLabel();
			logoutPanel.add(labelUsername);
			logoutPanel.add(btnLogout);
			mainPanel.add(logoutPanel);

			// "sohuuCPGroup" GROUP
			sohuuCPGroup = new JXTaskPane();
			sohuuCPGroup.setName("sohuuCPGroup");
			sohuuCPGroup.setTitle("Cổ phiếu của tôi");
			sohuuCPGroup.add(createSohuuCPPanel());
			mainPanel.add(sohuuCPGroup);

			// "Office" GROUP
			xemCPGroup = new JXTaskPane();
			xemCPGroup.setName("xemCPGroup");
			xemCPGroup.setTitle("Danh sách theo dõi");
			xemCPGroup.add(createXemCPPanel());
			mainPanel.add(xemCPGroup);

			add(mainPanel);
			// add(new JScrollPane(mainPanel));

			setActionListener();
		}
	}

	private void setActionListener() {
		btnLogout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.setVisible(false);
				properties.clear();
				showLoginDialog();
			}
		});
	}

	private JPanel createSohuuCPPanel() {
		panelMainSohuu = new JXPanel();
		panelMainSohuu.setLayout(new VerticalLayout(5));

		// Panel mua/ban CP
		JXPanel panelMuaban = new JXPanel();
		panelMuaban.setLayout(new GridLayout(0, 5, 1, 1));

		panelMuaban.add(new JXLabel());
		panelMuaban.add(new JXLabel("Mã CP"));
		panelMuaban.add(new JXLabel("Khối lượng"));
		panelMuaban.add(new JXLabel("Giá"));
		panelMuaban.add(new JXLabel());

		String[] strMuaban = new String[] { "Mua", "Bán" };
		final JComboBox<String> selectMuaban = new JComboBox<String>(strMuaban);
		selectMuaban.setSelectedIndex(0);
		final JXTextField txtMaCP = new JXTextField();
		final JXTextField txtKhoiluong = new JXTextField();
		final JXTextField txtGia = new JXTextField();
		JXButton btnMuaban = new JXButton("Thực hiện");
		btnMuaban.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnMuabanClicked(selectMuaban.getSelectedIndex(), txtMaCP.getText().trim(),
						txtKhoiluong.getText().trim(), txtGia.getText().trim());

				txtMaCP.setText("");
				txtKhoiluong.setText("");
				txtGia.setText("");
			}
		});
		txtMaCP.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				int code = (int) e.getKeyChar();
				if (code >= 97 && code <= 122) {
					e.setKeyChar((char) (code - 32));
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				txtMaCPChanged(txtMaCP.getText());
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		panelMuaban.add(selectMuaban);
		panelMuaban.add(txtMaCP);
		panelMuaban.add(txtKhoiluong);
		panelMuaban.add(txtGia);
		panelMuaban.add(btnMuaban);

		// Panel tien mat hien tai
		JXPanel panelTienmat = new JXPanel();
		panelTienmat.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 1));
		labelInfoTienmat = new JXLabel("Tiền mặt: " + df.format(currentUser.getTienmat()) + " VND.");
		labelInfoCPMuaban = new JXLabel();
		panelTienmat.add(labelInfoTienmat);
		panelTienmat.add(labelInfoCPMuaban);

		// Panel danh sach so huu
		tableSohuu = new JXTable();

		panelMainSohuu.add(panelMuaban);
		panelMainSohuu.add(panelTienmat);
		panelMainSohuu.add(tableSohuu);

		return panelMainSohuu;
	}

	/**
	 * Tao panel xem CP
	 * 
	 * @return
	 */
	private JXPanel createXemCPPanel() {
		JXPanel panelLV1 = new JXPanel();
		panelLV1.setLayout(new VerticalLayout());

		// Panel button them CP
		JXPanel panelLV2_1 = new JXPanel();
		panelLV2_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));

		Object[] dsCP = SmmServer.getInstance().getDSMaCP();
		final JComboBox<Object> cbxMacp = new JComboBox<>(dsCP);
		AutoCompleteDecorator.decorate(cbxMacp);
		JXButton bt = new JXButton("Tìm và thêm");
		bt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addCophieu((String) cbxMacp.getSelectedItem());
			}
		});
		labelInfoXemCP = new JXLabel();

		panelLV2_1.add(cbxMacp);
		panelLV2_1.add(bt);
		panelLV2_1.add(labelInfoXemCP);

		// panel hien danh sach CP theo doi
		panelTheodoiCP = new JXPanel();
		panelTheodoiCP.setLayout(new GridLayout(0, 7, 2, 2));

		panelLV1.add(panelLV2_1);
		panelLV1.add(panelTheodoiCP);

		return panelLV1;
	}

	private void reloadDSSohuu() {
		panelMainSohuu.remove(tableSohuu);

		tableSohuu = new JXTable(currentUser.getDsSohuu().size() + 1, 6);
		tableSohuu.setRowSelectionAllowed(false);
		tableSohuu.setColumnSelectionAllowed(false);
		tableSohuu.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		panelMainSohuu.add(tableSohuu);

		tableSohuu.setValueAt("Mã", 0, 0);
		tableSohuu.setValueAt("Khối lượng", 0, 1);
		tableSohuu.setValueAt("Giá TB", 0, 2);
		tableSohuu.setValueAt("Giá Hiện tại", 0, 3);
		tableSohuu.setValueAt("Lợi nhận", 0, 4);
		tableSohuu.setValueAt("Giá trị", 0, 5);

		Map<String, Object[]> dsSohuu = currentUser.getDsSohuu();
		int index = 1;
		for (String maCP : dsSohuu.keySet()) {
			Cophieu cophieu = SmmServer.getInstance().getCophieu(maCP);

			int khoiluong = (int) dsSohuu.get(maCP)[0];
			double giaTB = (double) dsSohuu.get(maCP)[1];
			double giaHT = cophieu.getGiaHientai();
			double loinhuan = (giaHT - giaTB) / giaTB * 100;
			double taisan = giaHT * khoiluong * 1000;

			tableSohuu.setValueAt(maCP, index, 0);
			tableSohuu.setValueAt(df.format(khoiluong), index, 1);
			tableSohuu.setValueAt(df.format(giaTB), index, 2);
			tableSohuu.setValueAt(df.format(giaHT), index, 3);
			tableSohuu.setValueAt(df.format(loinhuan) + " %", index, 4);
			tableSohuu.setValueAt(df.format(taisan), index, 5);

			index++;
		}

		tableSohuu.setEditable(false);
		sohuuCPGroup.updateUI();
	}

	private void reloadDSXemCP() {
		panelTheodoiCP.removeAll();
		panelTheodoiCP.add(new JXLabel("Mã"));
		panelTheodoiCP.add(new JXLabel("Tham chiếu"));
		panelTheodoiCP.add(new JXLabel("Giá Trần"));
		panelTheodoiCP.add(new JXLabel("Giá Sàn"));
		panelTheodoiCP.add(new JXLabel("Hiện tại"));
		panelTheodoiCP.add(new JXLabel("+/-"));
		panelTheodoiCP.add(new JXLabel(""));

		List<String> listXemCP = currentUser.getDsXem();

		for (String maCP : listXemCP) {
			final Cophieu cophieu = SmmServer.getInstance().getCophieu(maCP);

			panelTheodoiCP.add(new JXLabel(cophieu.getMaCP()));
			panelTheodoiCP.add(new JXLabel("" + df.format(cophieu.getGiaThamchieu())));
			panelTheodoiCP.add(new JXLabel("" + df.format(cophieu.getGiaTran())));
			panelTheodoiCP.add(new JXLabel("" + df.format(cophieu.getGiaSan())));
			panelTheodoiCP.add(new JXLabel("" + df.format(cophieu.getGiaHientai())));
			panelTheodoiCP.add(new JXLabel("" + df.format(cophieu.getTanggiam())));
			JXButton btnXoa = new JXButton("Xóa");
			panelTheodoiCP.add(btnXoa);

			btnXoa.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					xoaKhoiDSXem(cophieu.getMaCP());
				}
			});
		}

		xemCPGroup.updateUI();
		panelTheodoiCP.doLayout();
	}

	// Form action function ------------------------------------------------
	// Mua/Ban co phieu
	private void btnMuabanClicked(int selectedIndex, String maCP, String khoiluong, String gia) {
		int code = 0;
		if (selectedIndex == 0) {
			code = currentUser.muaCP(maCP, Integer.parseInt(khoiluong), Double.parseDouble(gia));
		} else {
			code = currentUser.banCP(maCP, Integer.parseInt(khoiluong), Double.parseDouble(gia));
		}

		if (code == 0) {
			labelInfoCPMuaban
					.setText((selectedIndex == 0 ? "Mua" : "Bán") + " thành công " + maCP + " giá " + gia + ".");
			labelInfoTienmat.setText("Tiền mặt: " + df.format(currentUser.getTienmat()) + " VND.");

			reloadDSSohuu();
		} else {
			labelInfoCPMuaban.setText((selectedIndex == 0 ? "Mua" : "Bán") + " thất bại " + maCP + " giá "
					+ df.format(gia) + ". Mã lỗi: " + code);
		}

	}

	// textbox txtMaCP duoc nhap
	protected void txtMaCPChanged(String text) {
		Cophieu cophieu = SmmServer.getInstance().getCophieu(text);

		if (cophieu == null) {
			this.labelInfoCPMuaban.setText("Không có cổ phiếu " + text);
		} else {
			this.labelInfoCPMuaban.setText("   " + text + " - Sàn: " + df.format(cophieu.getGiaSan()) + " - Trần: "
					+ df.format(cophieu.getGiaTran()) + "    Có thể mua: "
					+ (int) (currentUser.getTienmat() / cophieu.getGiaHientai() / 1000) + " CP");
		}
	}

	// Them CP theo doi
	public void addCophieu(String maCP) {
		int code = currentUser.addXem(maCP);

		if (code == 0) {
			labelInfoXemCP.setText("");
		} else if (code == -1) {
			labelInfoXemCP.setText(maCP + " đã theo dõi.");
		} else if (code == -2) {
			labelInfoXemCP.setText(maCP + " không tìm thấy.");
		}

		reloadDSXemCP();
	}

	// Xoa CP khoi danh sach theo doi
	protected void xoaKhoiDSXem(String maCP) {
		currentUser.removeXem(maCP);

		reloadDSXemCP();
	}

}
