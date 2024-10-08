package com.crud.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.crud.demo.model.MailDetails;

public interface MailDetailsRepository extends JpaRepository<MailDetails, Long> {
}
