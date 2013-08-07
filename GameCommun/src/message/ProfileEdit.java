package message;

import java.util.regex.Pattern;

public class ProfileEdit implements IMsgChess {
	/* Indique si c'est une identification ou une inscription */
	public boolean edit = false;
	public boolean chgPwd = false;

	private String oldPassword = "";
	private String newPassword = "";
	private String username = "";
	private String email = "";
	private String country = "";

	public ProfileEdit() {

	}

	public ProfileEdit(String oldPassword, String newPassword, String email, String username, String country) {
		this.username = username;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.email = email;
		this.country = country;
	}

	public boolean isChgPwd() {
		return chgPwd;
	}

	public void setChgPwd(boolean chgPwd) {
		this.chgPwd = chgPwd;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public boolean isMailValid() {
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		return (p.matcher(email).matches());
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
