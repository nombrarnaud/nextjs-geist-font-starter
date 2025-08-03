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
@DiscriminatorValue("SIMPLE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SimpleUser extends User {

    @NotBlank
    @Column(name = "identity_card_number")
    private String identityCardNumber;

    public SimpleUser(String email, String password, String fullName, String phone, 
                     String identityCardNumber) {
        super();
        setEmail(email);
        setPassword(password);
        setFullName(fullName);
        setPhone(phone);
        setUserType(UserType.SIMPLE);
        this.identityCardNumber = identityCardNumber;
    }
}
