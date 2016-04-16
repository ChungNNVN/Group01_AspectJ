package smm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

	private String username;
	private String pass;
	private String hoten;
	private double tienmat;

	/**
	 * Map<maCP,[KL,giaTB]> (Danh sach Co phieu so huu)
	 */
	private Map<String, Object[]> dsSohuu;

	/**
	 * List<maCP> (Danh sach Co phieu theo doi)
	 */
	private List<String> dsXem;

	// KHOI TAO -----------------------------------
	/**
	 * Khoi tao
	 */
	public User(String username, String pass, String hoten, double tienmat) {
		this.username = username;
		this.pass = pass;
		this.hoten = hoten;
		this.tienmat = tienmat;

		dsSohuu = new HashMap<String, Object[]>();
		dsXem = new ArrayList<>();
	}

	// GETTER - SETTER -----------------------------------
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHoten() {
		return hoten;
	}

	public void setHoten(String hoten) {
		this.hoten = hoten;
	}

	public double getTienmat() {
		return tienmat;
	}

	public void setTienmat(double tienmat) {
		this.tienmat = tienmat;
	}

	public Map<String, Object[]> getDsSohuu() {
		return dsSohuu;
	}

	public void setDsSohuu(Map<String, Object[]> dsSohuu) {
		this.dsSohuu = dsSohuu;
	}

	public List<String> getDsXem() {
		return dsXem;
	}

	public void setDsXem(List<String> dsXem) {
		this.dsXem = dsXem;
	}

	// PUBLIC FUNCTION -----------------------------------
	// addSohuu(maCP, [kl,giaTB])
	public void addSohuu(String maCP, Object[] values) {
		dsSohuu.put(maCP, values);
	}

	// addXem(maCP)
	/**
	 * @param maCP
	 * @return 0: success -1: da ton tai -2: co phieu ko ton tai
	 */
	public int addXem(String maCP) {
		if (!SmmServer.getInstance().tontaiCP(maCP)) {
			return -2;
		}

		if (dsXem.contains(maCP)) {
			return -1;
		}

		dsXem.add(maCP);

		return 0;
	}

	// huyXem(maCP)
	/**
	 * @param maCP
	 * @return 0: success -1: khong dang xem CP nay
	 */
	public int removeXem(String maCP) {
		if (!dsXem.contains(maCP)) {
			return -1;
		}
		
		dsXem.remove(maCP);

		return 0;
	}

	// muaCP(maCP,kl,giamua)
	/**
	 * @param maCP
	 * @param khoiluong
	 * @param giamua
	 * @return 0: success -1: CP khong ton tai -2: kl hoac gia mua > 0 -3:
	 *         khoiluong phai chan 10 (% 10 = 0) -4: tien mat khong du
	 */
	public int muaCP(String maCP, int khoiluong, double giamua) {

		if (!SmmServer.getInstance().tontaiCP(maCP)) {
			return -1;
		}

		if (khoiluong <= 0 || giamua <= 0) {
			return -2;
		}

		if (khoiluong % 10 != 0) {
			return -3;
		}

		if (khoiluong * giamua > tienmat) {
			return -4;
		}

		Object[] values = dsSohuu.get(maCP);

		if (values == null) {
			// them moi so huu
			dsSohuu.put(maCP, new Object[] { khoiluong, giamua });

		} else {
			// update so huu
			int klTong = (int) values[0];
			double giaTB = (double) values[1];

			giaTB = (klTong * giaTB + khoiluong * giamua) / (klTong + khoiluong);
			klTong += khoiluong;

			dsSohuu.put(maCP, new Object[] { klTong, giaTB });
		}

		tienmat -= khoiluong * giamua * 1000;

		return 0;
	}

	// banCP(maCP,kl,giaban)
	/**
	 * @param maCP
	 * @param khoiluong
	 * @param giaban
	 * @return 0: success -1: CP khong ton tai -2: kl hoac gia mua > 0 -3:
	 *         khoiluong phai chan 10 (% 10 = 0) -4: KL so huu < kl ban
	 */
	public int banCP(String maCP, int khoiluong, double giaban) {
		if (!SmmServer.getInstance().tontaiCP(maCP)) {
			return -1;
		}

		if (khoiluong <= 0 || giaban <= 0) {
			return -2;
		}

		if (khoiluong % 10 != 0) {
			return -3;
		}

		Object[] values = dsSohuu.get(maCP);

		if (values == null) {
			// tra ve loi
			return -4;

		} else {
			// Co so huu
			int klTong = (int) values[0];
			double giaTB = (double) values[1];

			if (klTong < khoiluong) {
				return -4;
			}

			klTong -= khoiluong;

			if (klTong > 0) {
				// update so luong so huu

				dsSohuu.put(maCP, new Object[] { klTong, giaTB });

			} else {
				// xoa cp so huu
				dsSohuu.remove(maCP);
			}
		}

		tienmat += khoiluong * giaban * 1000;

		return 0;
	}

	// getSohuu(maCP) -> boolean
	public boolean coSohuu(String maCP) {
		return dsSohuu.containsKey(maCP);
	}

	// get Tong tai san
	public double getTongTaisan() {
		return tienmat + getTongGiatriCP();
	}

	// getTong gia tri co phieu
	public double getTongGiatriCP() {
		double tongGiatriCP = 0;

		Collection<Object[]> listSohuu = dsSohuu.values();

		for (Object[] objects : listSohuu) {
			tongGiatriCP += (int) objects[0] * (double) objects[1];
		}

		return tongGiatriCP;
	}

	// login() --> boolean
	public boolean login(String pass) {
		return this.pass.equals(pass);
	}

	// PRIVATE FUNCTION -----------------------------------

}
