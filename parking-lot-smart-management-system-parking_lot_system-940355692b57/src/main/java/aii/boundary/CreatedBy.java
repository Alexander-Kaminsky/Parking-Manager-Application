package aii.boundary;

// class to handle createdBy structure

public class CreatedBy {
	private UserBoundary.UserId userId;

	// Default Constructor
	public CreatedBy() {}

	// Full Constructor
	public CreatedBy(UserBoundary.UserId userId) {
		this.userId = userId;
	}

	// Getters and Setters
	public UserBoundary.UserId getUserId() {
		return userId;
	}

	public void setUserId(UserBoundary.UserId userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "CreatedBy{" +
				"userId=" + userId +
				'}';
	}
}
