package demo.model;

import demo.model.UserBoundary.UserId;

public class CreatedBy {
	private UserId userId;
    
    public CreatedBy() {}
    
    // Getters and setters
    public UserId getUserId() { return userId; }
    public void setUserId(UserId userId) { this.userId = userId; }

}
