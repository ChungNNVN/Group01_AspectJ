package smm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class SmmServer {
	DecimalFormat df = new DecimalFormat("#,###.##");

	// ---------------------------------
	private static SmmServer instance;

	// dsCphieu - Map<maCP, CP>
	private Map<String, Cophieu> dsCophieu;
	// dsUser - Map<username, User>
	private Map<String, User> dsUser;

	// INIT ---------------------------------
	public SmmServer() {
		// init dsCophieu, dsUserupdateGia
		dsCophieu = new HashMap<String, Cophieu>();
		dsUser = new HashMap<String, User>();
	}

	private void loadDatabase() {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("database.txt");

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;

		try {
			// read ds co phieu --------------------
			line = reader.readLine();
			int soCP = Integer.parseInt(line.substring(line.indexOf(":") + 1));
			// skip comment line
			line = reader.readLine();

			for (int i = 0; i < soCP; i++) {
				line = reader.readLine();
				String[] values = line.split("\\|");

				String maCP = values[0];
				Double giaTC = Double.parseDouble(values[1]);
				Double giaTran = Double.parseDouble(values[2]);
				Double giaSan = Double.parseDouble(values[3]);
				Double giaHientai = Double.parseDouble(values[4]);

				dsCophieu.put(maCP, new Cophieu(maCP, giaTC, giaTran, giaSan, giaHientai));
			}

			// read ds user ---------------
			line = reader.readLine();
			int soUser = Integer.parseInt(line.substring(line.indexOf(":") + 1));
			// skip comment line
			line = reader.readLine();

			for (int i = 0; i < soUser; i++) {
				line = reader.readLine();
				String[] values = line.split("\\|");

				String username = values[0];
				String pass = values[1];
				String hoten = values[2];
				Double tienmat = Double.parseDouble(values[3]);

				dsUser.put(username, new User(username, pass, hoten, tienmat));
			}

			// read ds so huu ---------------
			line = reader.readLine();
			int soSohuu = Integer.parseInt(line.substring(line.indexOf(":") + 1));
			// skip comment line
			line = reader.readLine();

			for (int i = 0; i < soSohuu; i++) {
				// voi moi user
				line = reader.readLine();
				String[] values = line.split("\\|");
				User user = dsUser.get(values[0]);
				if (user != null) {
					// voi moi cp so huu
					for (int j = 1; j < values.length; j++) {
						String[] cpValues = values[j].split("\\:");
						user.addSohuu(cpValues[0],
								new Object[] { Integer.parseInt(cpValues[1]), Double.parseDouble(cpValues[2]) });
					}
				}
			}

			// read ds xem ---------------
			line = reader.readLine();
			int soXem = Integer.parseInt(line.substring(line.indexOf(":") + 1));
			// skip comment line
			line = reader.readLine();

			for (int i = 0; i < soXem; i++) {
				// voi moi user
				line = reader.readLine();
				String username = line.substring(0, line.indexOf(":"));
				String[] values = line.substring(line.indexOf(":") + 1).split("\\|");
				User user = dsUser.get(username);
				if (user != null) {
					for (String maCP : values) {
						user.addXem(maCP);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				is.close();
			} catch (Exception e) {
			}
		}
	}

	private void makeThread() {
		Thread thread = new Thread() {
			public void run() {
				while (true) {
					updateGiaCP();

					try {
						Thread.sleep(1000 * 10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		thread.start();
	}

	private void updateGiaCP() {
		for (Cophieu cophieu : dsCophieu.values()) {
			cophieu.updateGia();
		}
	}

	// PUBLIC FUNCTION ---------------------------------
	public static SmmServer getInstance() {
		if (instance == null) {
			instance = new SmmServer();
			// load database from database.txt
			instance.loadDatabase();

			// tao thread dinh ky update gia CP
			instance.makeThread();
		}

		return instance;
	}

	/**
	 * @param username
	 * @param pass
	 * @return
	 */
	public User login(String username, String pass) {

		User user = dsUser.get(username);

		if (user == null) {
			// Khong ton tai user
			return null;
		}

		if (user.login(pass)) {
			return user;
		} else {
			return null;
		}
	}

	public boolean tontaiCP(String maCP) {
		return dsCophieu.containsKey(maCP);
	}

	public String getUser(String username) {
		User user = dsUser.get(username);

		return user == null ? "" : user.getHoten();
	}

	public Cophieu getCophieu(String maCP) {
		return dsCophieu.get(maCP);
	}
	
	public Object[] getDSMaCP(){
		return dsCophieu.keySet().toArray();
	}
}
