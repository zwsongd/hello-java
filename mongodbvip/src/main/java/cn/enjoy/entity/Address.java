package cn.enjoy.entity;

public class Address {
	
	private String aCode;
	private String add;
	public String getaCode() {
		return aCode;
	}
	public void setaCode(String aCode) {
		this.aCode = aCode;
	}
	public String getAdd() {
		return add;
	}
	public void setAdd(String add) {
		this.add = add;
	}
	@Override
	public String toString() {
		return "Address [aCode=" + aCode + ", add=" + add + "]";
	}
}
