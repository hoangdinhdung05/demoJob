package com.demoJob.demo.validator;

import com.demoJob.demo.exception.InvalidDataException;
import com.demoJob.demo.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CompanyValidator {

    private final CompanyRepository companyRepository;

    public void validateUniqueName(String name, String currentName) {
        if (name != null && !name.equals(currentName) && companyRepository.existsByName(name)) {
            throw new InvalidDataException("Company name already exists " + name);
        }
    }

    public void validateUniqueEmail(String email, String currentEmail) {
        if (email != null && !email.equals(currentEmail) && companyRepository.existsByEmail(email)) {
            throw new InvalidDataException("Company email already exists " + email);
        }
    }
}
