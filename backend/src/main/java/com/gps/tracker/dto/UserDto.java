package com.gps.tracker.dto;

import com.gps.tracker.model.BusinessUser;
import com.gps.tracker.model.SimpleUser;
import com.gps.tracker.model.User;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private User.UserType userType;
    
    // Business user specific fields
    private String companyName;
    private String registrationNumber;
    private String managerName;
    
    // Simple user specific fields
    private String identityCardNumber;
    
    public static UserDto from(User user) {
        UserDto dto = new UserDto(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getPhone(),
            user.getUserType(),
            null, null, null, null
        );
        
        if (user instanceof BusinessUser businessUser) {
            dto.setCompanyName(businessUser.getCompanyName());
            dto.setRegistrationNumber(businessUser.getRegistrationNumber());
            dto.setManagerName(businessUser.getManagerName());
        } else if (user instanceof SimpleUser simpleUser) {
            dto.setIdentityCardNumber(simpleUser.getIdentityCardNumber());
        }
        
        return dto;
    }
}
