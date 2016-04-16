/* This library is distributed in the hope that it will be useful, 
  * but WITHOUT ANY WARRANTY; without even the implied warranty of 
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
  * Lesser General Public License for more details. 
  *  
  * You should have received a copy of the GNU Lesser General Public 
  * License along with this library; if not, write to the Free Software 
  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA 
  */
package smm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.auth.LoginService;

@SuppressWarnings("serial")
public class TaskPaneDemo extends JPanel {
	private JXTaskPane themCPGroup;
	private JXTaskPane danhsachCPGroup;

	JXTaskPaneContainer mainPanel = new JXTaskPaneContainer();
	JXLoginPane loginPanel = new JXLoginPane();

	private static Properties properties = new Properties();

	private static TaskPaneDemo me;

	/**
	 * main method allows us to run as a standalone demo.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				me = new TaskPaneDemo();
				me.showLoginPanel();
			}
		});
	}

	protected void showLoginPanel() {
		JXLoginPane loginPanel = new JXLoginPane(new LoginService() {

			@Override
			public boolean authenticate(String arg0, char[] arg1, String arg2) throws Exception {
				System.out.println(arg0 + "---"+ new String(arg1) + "---"+arg2);
				return true;
			}
		});
		
		loginPanel.setMessage("Please your password for Last.fm account:");
		loginPanel.setBannerText("Last.fm login");
		
		if (JXLoginPane.showLoginDialog(me, loginPanel) == JXLoginPane.Status.SUCCEEDED) {
			properties.setProperty("user", loginPanel.getUserName());
			String encPassword = "123";
			properties.setProperty("password", encPassword);
			// Write properties file.
			// try {
			// properties.store(new
			// FileOutputStream(getResource("g15lastfm.properties").getPath()),
			// null);
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		}

		JFrame frame = JXLoginPane.showLoginFrame(loginPanel);

		// JFrame frame = new JFrame("Hello");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.getContentPane().add(new TaskPaneDemo());
		frame.setPreferredSize(new Dimension(800, 600));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public TaskPaneDemo() {
		super(new BorderLayout());

		createMainPanel();
	}

	private void createMainPanel() {
		// mainPanel = new JXTaskPaneContainer();

		// "System" GROUP
		themCPGroup = new JXTaskPane();
		themCPGroup.setName("addCPGroup");
		themCPGroup.setTitle("Thêm cổ phiếu");
		themCPGroup.add(createAddCPPanel());
		mainPanel.add(themCPGroup);

		// "Office" GROUP
		danhsachCPGroup = new JXTaskPane();
		danhsachCPGroup.setName("dscophieuGroup");
		danhsachCPGroup.setTitle("Danh sách của tôi");
		danhsachCPGroup.add(createListCPPanel());
		mainPanel.add(danhsachCPGroup);

		add(mainPanel);
		// add(new JScrollPane(mainPanel));
	}

	private JPanel createAddCPPanel() {
		JXPanel panel = new JXPanel();

		JXButton bt = new JXButton("Tìm và thêm");
		bt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addCophieu();
			}
		});

		JXTextField text = new JXTextField("Mã cổ phiếu");
		text.setPreferredSize(new Dimension(60, 26));
		text.setMaximumSize(text.getPreferredSize());

		panel.add(text);
		panel.add(bt);
		panel.add(new JXLabel("Nhap ma CP de them"));

		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		return panel;
	}

	private JXPanel createListCPPanel() {
		JXPanel panel = new JXPanel();

		panel.setLayout(new GridLayout(0, 9, 2, 2));

		panel.add(new JXLabel("Mã"));
		panel.add(new JXLabel("TC"));
		panel.add(new JXLabel("Trần"));
		panel.add(new JXLabel("Sàn"));
		panel.add(new JXLabel("Giá"));
		panel.add(new JXLabel("+/-"));
		panel.add(new JXLabel("Tổng KL"));
		panel.add(new JXLabel("Tổng GT"));
		panel.add(new JXLabel(""));

		return panel;
	}

	public void addCophieu() {
		themCPGroup.setTitle("My doing");
	}
}
