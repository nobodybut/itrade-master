package com.trade.common.infrastructure.util.refout;

import java.io.Serializable;

public class RefString implements Serializable {

	private static final long serialVersionUID = 923658729389452922L;

	/**
	 * ref
	 */
	public String ref = "";

	/**
	 * ref_2
	 */
	public String ref_2 = "";

	/**
	 * ref_3
	 */
	public String ref_3 = "";

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getRef_2() {
		return ref_2;
	}

	public void setRef_2(String ref_2) {
		this.ref_2 = ref_2;
	}

	public String getRef_3() {
		return ref_3;
	}

	public void setRef_3(String ref_3) {
		this.ref_3 = ref_3;
	}
}
