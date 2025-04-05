package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "mail_table")
public class MailEntity {

	
	@Id
	int id;
	@Column(name = "sender_address")
	String senderAddress;
	@Column(name = "receiver_address")
	String receiverAddress;
	@Column(columnDefinition = "TEXT",length = 65535)
	String subject;
	@Column(columnDefinition = "TEXT",length = 65535)
	String text;
	
	@OneToMany(mappedBy = "message",cascade = CascadeType.ALL)
	List<FileEntity> attachedFiles;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
	LocalDateTime created;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
	LocalDateTime exist;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
	LocalDateTime delivered;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
	LocalDateTime opened;

	public MailEntity() {

	}


	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDateTime getExist() {
		return exist;
	}

	public void setExist(LocalDateTime exist) {
		this.exist = exist;
	}

	public LocalDateTime getDelivered() {
		return delivered;
	}

	public void setDelivered(LocalDateTime delivered) {
		this.delivered = delivered;
	}

	public LocalDateTime getOpened() {
		return opened;
	}

	public void setOpened(LocalDateTime opened) {
		this.opened = opened;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public List<FileEntity> getAttachedFiles() {
		return attachedFiles;
	}


	public void setAttachedFiles(List<FileEntity> attachedFiles) {
		this.attachedFiles = attachedFiles;
	}


	public LocalDateTime getCreated() {
		return created;
	}


	public void setCreated(LocalDateTime created) {
		this.created = created;
	}


	@Override
	public String toString() {
		return "MailEntity [id=" + id + ", senderAddress=" + senderAddress + ", receiverAddress=" + receiverAddress
				+ ", subject=" + subject + ", text=" + text + ", exist=" + exist + ", delivered=" + delivered
				+ ", opened=" + opened + "]";
	}


}
