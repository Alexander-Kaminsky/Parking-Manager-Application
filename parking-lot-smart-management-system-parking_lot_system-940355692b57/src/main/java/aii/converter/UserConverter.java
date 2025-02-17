package aii.converter;

import org.springframework.stereotype.Component;

import aii.boundary.UserBoundary;
import aii.boundary.UserBoundary.UserId;
import aii.data.UserEntity;

@Component
public class UserConverter {

    // Convert UserBoundary to UserEntity
    public UserEntity toEntity(UserBoundary boundary) {
        if (boundary == null) {
            return null;
        }

        // Generate uniqueId from email and systemID
        String uniqueId = boundary.getUserId().getEmail() + "@@" + boundary.getUserId().getSystemID();

        return new UserEntity(
                uniqueId,
                boundary.getUsername(),
                boundary.getAvatar(),
                boundary.getRole()
        );
    }

    // Convert UserEntity to UserBoundary
    public UserBoundary toBoundary(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        // Decompose uniqueId into email and systemID
        String[] parts = entity.getUniqueId().split("@@");
        String email = parts[0];
        String systemID = parts[1];

        return new UserBoundary(
                new UserId(systemID, email),
                entity.getRole(),
                entity.getUsername(),
                entity.getAvatar()
        );
    }
}
