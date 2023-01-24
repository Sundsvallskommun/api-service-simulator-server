package se.sundsvall.simulatorserver;

import java.util.Objects;

public class TestClass {

	private String field1;
	private int field2;
	private boolean field3;

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public int getField2() {
		return field2;
	}

	public void setField2(int field2) {
		this.field2 = field2;
	}

	public boolean isField3() {
		return field3;
	}

	public void setField3(boolean field3) {
		this.field3 = field3;
	}

	@Override
	public int hashCode() {
		return Objects.hash(field1, field2, field3);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TestClass other = (TestClass) obj;
		return Objects.equals(field1, other.field1) && field2 == other.field2 && field3 == other.field3;
	}
}
