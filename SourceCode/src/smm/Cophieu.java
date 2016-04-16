package smm;

public class Cophieu {
	private String maCP;
	private double giaThamchieu;
	private double giaTran;
	private double giaSan;
	private double giaHientai;

	// INIT -------------------------------
	public Cophieu(String maCP, double giaThamchieu, double giaTran, double giaSan, double giaHientai) {
		this.maCP = maCP;
		this.giaHientai = giaHientai;
		this.giaThamchieu = giaThamchieu;
		this.giaTran = giaTran;
		this.giaSan = giaSan;
	}

	// GETTER -------------------------------
	public String getMaCP() {
		return maCP;
	}

	public double getGiaThamchieu() {
		return giaThamchieu;
	}

	public double getGiaTran() {
		return giaTran;
	}

	public double getGiaSan() {
		return giaSan;
	}

	public double getGiaHientai() {
		return giaHientai;
	}

	public double getTanggiam() {
		return giaHientai - giaThamchieu;
	}

	// PUBLIC FUNCTION --------------------------------
	// update gia hien tai
	public void updateGia() {
		double ran = Math.random();

		if (ran >= 0 && ran < 0.4) {
			// Giam. Neu dang la gia SAN thi ko giam nua
			this.giaHientai -= this.giaHientai > this.giaSan ? getBuocgia() : 0;

		} else if (ran >= 0.4 && ran < 0.6) {
			// gia khong thay doi.
			return;

		} else {
			// tang. Neu dang la gia TRAN thi ko tang nua
			this.giaHientai += this.giaHientai < this.giaTran? getBuocgia() : 0;
		}
	}

	private double getBuocgia() {
		return this.giaHientai < 50 ? 0.1 : 0.5;
	}
}
