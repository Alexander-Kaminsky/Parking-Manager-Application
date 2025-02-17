package demo.model;

public class UserBoundary {

	private UserId userId;
	private String role;
	private String username;
	private String avatar;

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public static class UserId {
		private String systemID;
		private String email;

		public String getSystemID() {
			return systemID;
		}

		public void setSystemID(String systemID) {
			this.systemID = systemID;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
	}
}

