package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(value = "*",allowedHeaders = "*")
public class MailController {

	@Autowired
	MailRepository mailRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	FileRepository fileRepository;
	

 static JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();

	static{
		 
       javaMailSenderImpl.setHost("smtp.gmail.com");
        javaMailSenderImpl.setPort(587);
//	javaMailSenderImpl.setUsername("MS_yw5WEq@trial-51ndgwv7v2dlzqx8.mlsender.net");
//	javaMailSenderImpl.setPassword("mssp.z2asxBG.o65qngkvp8olwr12.1sHLJk2");
	}

	String api_key = "36aece1351d1445b8b170ed3fef90a56&";
//	String website_url = "https://emailtracker.up.railway.app";
	String website_url = "http://localhost:8080";
	@GetMapping("/")
	public RedirectView index() {
		return new RedirectView("/index.html");
	}

	@GetMapping("/login")
	public RedirectView login(HttpSession session) {
		if (session.getAttribute("username") != null && session.getAttribute("password") != null)
			return new RedirectView("/email_home.html");

		return new RedirectView("/email_login.html");
	}

	@GetMapping("/home")
	public RedirectView home(HttpSession session) {

		if (session.getAttribute("username") != null && session.getAttribute("password") != null)
			return new RedirectView("/email_home.html");

		return new RedirectView("/index.html");
	}

	@GetMapping("/dashboard")
	public RedirectView dashboard(HttpSession session) {

		System.out.println(session.getAttribute("username"));
		System.out.println(session.getAttribute("password"));
		if (session.getAttribute("username") != null && session.getAttribute("password") != null)
			return new RedirectView("/email_dashboard.html");

		return new RedirectView("/index.html");
	}

	@GetMapping("/get-id")
	public int getId() {
		return mailRepository.getID().get();
	}

	@PostMapping("/logout")
	public RedirectView postLogin(HttpSession session) {

		session.invalidate();
		return new RedirectView("email_home.html");

	}

	@GetMapping("/open")
public ResponseEntity<byte[]> open(@RequestParam("id") int id, HttpServletRequest request) throws IOException {
    // Get User-Agent and IP Address
    String userAgent = request.getHeader("User-Agent");
    String ipAddress = request.getRemoteAddr();

    // Log the request details
    System.out.println("Tracking Image Requested - ID: " + id + ", User-Agent: " + userAgent + ", IP: " + ipAddress);

    // Detect bots (example logic, improve as needed)
    if (userAgent == null || userAgent.toLowerCase().contains("bot") || userAgent.toLowerCase().contains("crawler")) {
        return ResponseEntity.status(HttpStatus.OK).build(); // Return empty response for bots
    }

    // Load the image
    InputStream in = getClass().getResourceAsStream("/static/photos/favicon.png");
    byte[] imageBytes = IOUtils.toByteArray(in);

    // Update mail entity
    mailRepository.findById(id).ifPresent(mailEntity -> {
        mailEntity.setOpened(LocalDateTime.now());
        mailRepository.save(mailEntity);
    });

    // Set response headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.IMAGE_JPEG);

    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
}


	@GetMapping("/exist/{type}")
	public int exist(@RequestParam("email") String email, @PathVariable("type") int type)
			throws JsonMappingException, JsonProcessingException {

		List<UserEntity> userEntitiy = userRepository.findByUsername(email);
		List<MailEntity> mailEntitiy = mailRepository.findByReceiverAddress(email);

		String url = "https://emailvalidation.abstractapi.com/v1/?" + "api_key=" + api_key + "&" + "email=" + email;

		String res = "{\"deliverability\": \"NOTDELIVERABLE\"}";

		if (!userEntitiy.isEmpty() || !mailEntitiy.isEmpty())
			res = "{\"deliverability\": \"DELIVERABLE\"}";

		try {
			if (userEntitiy.isEmpty() && mailEntitiy.isEmpty())
				res = WebClient.create().get().uri(url).retrieve().bodyToMono(String.class).block();
		} catch (Exception e) {
			return -1;
		}

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(res);

		String deliverability = jsonNode.get("deliverability").asText();

		int id = 0;

		if (deliverability.equals("DELIVERABLE")) {
			id = mailRepository.getID().get() + 1;
			MailEntity entity = new MailEntity();
			entity.setId(id);
			if (type == 1)
				entity.setExist(LocalDateTime.now());
			mailRepository.save(entity);

		}

		return id;
	}

	@PostMapping("/send")
	@Transactional(rollbackOn = Exception.class)
	public int send(@RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("text") String text,
            @RequestParam(name="files",required = false) MultipartFile[] files) throws IOException {

		

		int result = -1;

		int id = -1;
		try {
			id = WebClient.create().get().uri(website_url + "/exist/1?email=" + to).retrieve().bodyToMono(Integer.class)
					.block();
		} catch (Exception e) {
			return result;
		}
		System.out.println(id);

		result = 0;

		if (id == 0)
			return result;

		String img_url = website_url + "/open?id=" + id ;
		result = 0;

		MailEntity entity = mailRepository.findById(id).get();

		entity.setSenderAddress(username);
		entity.setReceiverAddress(to);
		entity.setSubject(subject);
		entity.setText(text + "<br><img src='" + img_url + "'/>");
		
		List<FileEntity> listFileEntities=new ArrayList<>();
		
		if(files!=null)
	for(MultipartFile file: files) {
		FileEntity fileEntity = new FileEntity();
		fileEntity.setFileBytes(file.getBytes());
		fileEntity.setFileName(file.getOriginalFilename());
		fileEntity.setFileSize(file.getSize());
		fileEntity.setFileType(file.getContentType());
		fileEntity.setMessage(entity);
		
		listFileEntities.add(fileEntity);
	  }
		entity.setAttachedFiles(listFileEntities);
		

       	//	javaMailSenderImpl.setHost("smtp.gmail.com");
	//	javaMailSenderImpl.setHost("mail.smtpbucket.com");
		javaMailSenderImpl.setUsername(username);
	//	javaMailSenderImpl.setPort(587);
	//	javaMailSenderImpl.setPort(8025);
		javaMailSenderImpl.setPassword(password);

		Properties properties = javaMailSenderImpl.getJavaMailProperties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);

		try {
			MimeMessage mimeMessage = javaMailSenderImpl.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);

			helper.setTo(to);
	
//			String htmlContent = "<html><body>"
//			        + "<p>Hello,</p>"
//			        + "<img src='" + trackingUrl + "' width='1' height='1' style='display:none;'/>"
//			        + "</body></html>";

			helper.setText(text+ "<br><img src='" + img_url + "' width='1' height='1' style=''/>", true);

			
//helper.setText("<html><body><img src='https://emailtracker.up.railway.app/open?id=68' width='300px' height='auto' /></body></html>", true);
			helper.setSubject(subject);
			
			if (files != null && files.length > 0) {
	            for (MultipartFile file : files) {
	                InputStreamSource attachment = new ByteArrayResource(file.getBytes());
	                helper.addAttachment(file.getOriginalFilename(), attachment);
	            }
	        }

			javaMailSenderImpl.send(mimeMessage);
			mailRepository.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Email sending failed, rolling back transaction");
		}

		entity.setDelivered(LocalDateTime.now());

		result = 1;

		return result;
	}

	
	@PostMapping("/authenticate")
	public boolean authenticate(@RequestBody Map<String, Object> body) {

	//	javaMailSenderImpl.setHost("smtp.gmail.com");
	//	javaMailSenderImpl.setHost("mail.smtpbucket.com");	
		javaMailSenderImpl.setUsername((String) body.get("username"));
	//	javaMailSenderImpl.setPort(8025);
		javaMailSenderImpl.setPassword((String) body.get("password"));

		Properties properties = javaMailSenderImpl.getJavaMailProperties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);

		String subject = "Authenticate Your Profile ";
		String text = "<br><strong>Hi! User</strong><br><br>You Confirmation code is: <strong>" + (String) body.get("code")
				+ "</strong>";
		String to = (String) body.get("username");

		try {
			MimeMessage mimeMessage = javaMailSenderImpl.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

			helper.setTo(to);
			helper.setText(text, true);
			helper.setSubject(subject);

			javaMailSenderImpl.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		List<UserEntity> foundEntity = userRepository.findByUsername((String) body.get("username"));

		UserEntity user = new UserEntity();
		user.setUsername((String) body.get("username"));
		user.setPassword((String) body.get("password"));

		if (foundEntity.isEmpty()) {

			user.setAccounCreationDate(LocalDate.now());
			user.setAccountType("free");
			user.setMailLimit(10);

		
			userRepository.save(user);
		}
		else{
			 user = foundEntity.get(0); // Assuming foundEntity is a List<UserEntity>
user.setPassword((String) body.get("password")); 
userRepository.save(user);
}

		System.out.println("after");
		return true;

	}

	@GetMapping("/get-mails/{type}")
	public List<MailEntity> getMails(@RequestParam("sender") String sender, @PathVariable("type") int type) {

		if (type == 1)
			return mailRepository.findAllNewest(sender).get().stream().map(entity->{
				
				if (entity.delivered != null)
					entity.delivered.plusHours(5).plusMinutes(30);
				if (entity.exist != null)
					entity.exist.plusHours(5).plusMinutes(30);
				if (entity.opened != null)
					entity.opened.plusHours(5).plusMinutes(30);
				if (entity.created != null)
					entity.created.plusHours(5).plusMinutes(30);
				return entity;
			}
			).collect(Collectors.toList());
		

		return mailRepository.findAllNewest(sender).get().stream()
				.filter(entity -> entity.exist == null)
				.sorted((entity1,entity2)->entity2.getCreated().compareTo(entity1.getCreated()))
				.collect(Collectors.toList());
	}

	@PostMapping("/save-as-draft")
	@Transactional
	public int saveAsDraft(@RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("text") String text,
            @RequestParam(name="files",required = false) MultipartFile[] files) throws IOException {


		int result = -1;
		int id;

		try {
			id = WebClient.create().get().uri(website_url + "/exist/0?email=" + to).retrieve().bodyToMono(Integer.class)
					.block();
		} catch (Exception e) {
			System.out.println(e);
			return -1;
		}

		if (id == -1 || id == 0)
			return id;

		MailEntity entity = mailRepository.findById(id).get();

		entity.setSenderAddress(username);
		entity.setReceiverAddress(to);
		entity.setSubject(subject);
		entity.setText(text);
		entity.setCreated(LocalDateTime.now());
			
		List<FileEntity> listFileEntities=new ArrayList<>();
		
		if (files != null)
			for (MultipartFile file : files) {
				FileEntity fileEntity = new FileEntity();
				fileEntity.setFileBytes(file.getBytes());
				fileEntity.setFileName(file.getOriginalFilename());
				fileEntity.setFileSize(file.getSize());
				fileEntity.setFileType(file.getContentType());
				fileEntity.setMessage(entity);

				listFileEntities.add(fileEntity);
			}
		entity.setAttachedFiles(listFileEntities);
		
		mailRepository.save(entity);
		

		result = 1;
		System.out.println(result);

		return result;
	}

	 @PostMapping("/upload")
	    public ResponseEntity<byte[]> uploadFile( @RequestParam("file") MultipartFile file) throws IOException {
	        // Read file bytes
	        byte[] fileBytes = file.getBytes();

	        // Set headers
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.parseMediaType(file.getContentType())); // Set content type
	        headers.setContentLength(file.getSize());
	        headers.setContentDisposition(ContentDisposition.attachment().filename(file.getOriginalFilename()).build());
	        	
	        FileEntity fileEntity = new FileEntity();
	        
	        System.err.println(file);
	        
	        fileEntity.setFileBytes(fileBytes);
	        fileEntity.setFileName(file.getOriginalFilename());
	        fileEntity.setFileSize(file.getSize());
	        fileEntity.setFileType(file.getContentType());
	        
	        fileRepository.save(fileEntity);
	        
	        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
	    }
	    
	    
	    @GetMapping("/get-single-mail")
	    public MailEntity getSingleMail(@RequestParam("message-id") int id) {
	    	
	    	return mailRepository.findById(id).get();
	    }

	@GetMapping("/debug-time")
public ResponseEntity<String> debugTime() {
    return ResponseEntity.ok("Server Time: " + LocalDateTime.now());
}
	
//		@GetMapping("/get-file")
//		public ResponseEntity<byte[]> getFile(@RequestParam("name") String fileName) {
//
//			FileEntity file = fileRepository.findByFileName(fileName);
//			// Set headers
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.parseMediaType(file.getFileType())); // Set content type
//			headers.setContentLength(file.getFileSize());
//			headers.setContentDisposition(ContentDisposition.attachment().filename(file.getFileName()).build());
//			headers.add("message-id", String.valueOf(file.getMessageId()));
//			
//			headers.add("Access-Control-Expose-Headers", "message-id,content-type,content-length,content-disposition");
//			
//			System.err.println(headers);
//			
//			
//			return new ResponseEntity<>(file.getFileBytes(), headers, HttpStatus.OK);
//		}
//	    

}
