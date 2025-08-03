package com.gps.tracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@DiscriminatorValue("BUSINESS")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BusinessUser extends User {

    @NotBlank
    @Column(name = "company_name")
    private String companyName;

    @NotBlank
    @Column(name = "registration_number")
    private String registrationNumber;

    @NotBlank
    @Column(name = "manager_name")
    private String managerName;

    public BusinessUser(String email, String password, String fullName, String phone, 
                       String companyName, String registrationNumber, String managerName) {
        super();
        setEmail(email);
        setPassword(password);
        setFullName(fullName);
        setPhone(phone);
        setUserType(UserType.BUSINESS);
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.managerName = managerName;
    }
}
