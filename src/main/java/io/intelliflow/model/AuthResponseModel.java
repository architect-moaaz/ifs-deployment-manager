package io.intelliflow.model;

public class AuthResponseModel {
	private String _class;
	private String crumb;
	private String crumbRequestField;

	public String get_class() {
		return _class;
	}

	public String getCrumb() {
		return crumb;
	}

	public String getCrumbRequestField() {
		return crumbRequestField;
	}


	public void set_class(String _class) {
		this._class = _class;
	}

	public void setCrumb(String crumb) {
		this.crumb = crumb;
	}

	public void setCrumbRequestField(String crumbRequestField) {
		this.crumbRequestField = crumbRequestField;
	}
}
