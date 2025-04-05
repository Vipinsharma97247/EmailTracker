package com.example.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

@Transactional(rollbackOn = Exception.class)
public interface MailRepository extends JpaRepository<MailEntity, Integer>{
	
	@Query("SELECT max(id) from MailEntity m")
	Optional<Integer> getID();

	@Query("SELECT m FROM MailEntity m WHERE m.senderAddress= :senderAddress ORDER BY m.exist DESC ")
	Optional<List<MailEntity>> findAllNewest(@Param("senderAddress") String senderAddress);
	
	List<MailEntity> findByReceiverAddress(String receiverAddres);
	
	List<MailEntity> findByExist(LocalDateTime exist);
}
