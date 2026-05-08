package com.example.email_automation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.email_automation.model.EmailModel;

@Repository
public interface EmailRepository extends JpaRepository<EmailModel, Integer> {

}
