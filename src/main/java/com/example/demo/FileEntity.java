package com.example.demo;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "file_table")
public class FileEntity {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	int id;
	
	@Column(name = "file_name")
	String fileName;
	@Column(name = "file_type")
	String fileType;
	@Column(name = "file_size")
	Long fileSize;
	@Column(name = "file_bytes",columnDefinition = "LONGBLOB")
	byte[] fileBytes;
	
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "message_id")
	MailEntity message;
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	public byte[] getFileBytes() {
		return fileBytes;
	}
	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}
	public MailEntity getMessage() {
		return message;
	}
	public void setMessage(MailEntity message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "FileEntity [id=" + id + ", fileName=" + fileName + ", fileType=" + fileType + ", fileSize=" + fileSize
				+ ", fileBytes=" + Arrays.toString(fileBytes) + ", message=" + message + "]";
	}
	
	
	
	
}
