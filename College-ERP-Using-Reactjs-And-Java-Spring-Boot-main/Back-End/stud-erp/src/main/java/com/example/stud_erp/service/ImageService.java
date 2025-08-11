//package com.example.stud_erp.service;
//
//import com.example.stud_erp.entity.HOD;
//import com.example.stud_erp.entity.Professor;
//import com.example.stud_erp.entity.Student;
//import com.example.stud_erp.repository.HODRepository;
//import com.example.stud_erp.repository.ProfessorRepository;
//import com.example.stud_erp.repository.StudentRepository;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.storage.BlobId;
//import com.google.cloud.storage.BlobInfo;
//import com.google.cloud.storage.Storage;
//import com.google.cloud.storage.StorageOptions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//public class ImageService {
//    @Autowired
//    private StudentRepository studentRepository;
//
//    @Autowired
//    private ProfessorRepository professorRepository;
//
//    @Autowired
//    private HODRepository hodRepository;
//
//    public void saveStudent(Student student) {
//        studentRepository.save(student);
//    }
//
//    public void saveProfessor(Professor professor) {
//        professorRepository.save(professor);
//    }
//
//    public void saveHod(HOD hod){
//        hodRepository.save(hod);
//    }
//
//    public List<Student> getAllStudents() {
//        return studentRepository.findAll();
//    }
//
//
//    private String uploadFile(File file, String fileName) throws IOException {
//        BlobId blobId = BlobId.of("clg-erp-42daf.appspot.com", fileName);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
//        InputStream inputStream = ImageService.class.getClassLoader().getResourceAsStream("serviceAccountKey.json"); // change the file name with your one
//        GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);
//        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
//        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
//
//        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/clg-erp-42daf.appspot.com/o/%s?alt=media";
//        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
//    }
//
//    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
//        File tempFile = new File(fileName);
//        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//            fos.write(multipartFile.getBytes());
//        }
//        return tempFile;
//    }
//
//    private String getExtension(String fileName) {
//        return fileName.substring(fileName.lastIndexOf("."));
//    }
//
//    public String upload(MultipartFile multipartFile) {
//        try {
//            String fileName = UUID.randomUUID().toString().concat(this.getExtension(multipartFile.getOriginalFilename()));
//            File file = this.convertToFile(multipartFile, fileName);
//
//            try {
//                return this.uploadFile(file, fileName);
//            } finally {
//                file.delete(); // Delete temp file in finally block to ensure cleanup
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Image couldn't upload, Something went wrong";
//        }
//    }
//
//    public String uploadStudentData(MultipartFile multipartFile, Student student) {
//        String imageUrl = this.upload(multipartFile);
//        student.setImageUrl(imageUrl);
//        return imageUrl;
//    }
//
//    public String uploadProfData(MultipartFile multipartFile, Professor professor) {
//        String imageUrl = this.upload(multipartFile);
//        professor.setImageUrl(imageUrl);    
//        return imageUrl;
//    }
//
//    public String uploadHodData(MultipartFile multipartFile , HOD hod){
//        String imageUrl = this.upload(multipartFile);
//        hod.setImageUrl(imageUrl);
//        return imageUrl;
//    }
//}


package com.example.stud_erp.service;

import com.example.stud_erp.entity.HOD;
import com.example.stud_erp.entity.Professor;
import com.example.stud_erp.entity.Student;
import com.example.stud_erp.repository.HODRepository;
import com.example.stud_erp.repository.ProfessorRepository;
import com.example.stud_erp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private HODRepository hodRepository;

    public void saveStudent(Student student) {
        studentRepository.save(student);
    }

    public void saveProfessor(Professor professor) {
        professorRepository.save(professor);
    }

    public void saveHod(HOD hod) {
        hodRepository.save(hod);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

//    private String saveFileLocally(MultipartFile multipartFile) throws IOException {
//        // Create uploads folder if not exists
//        File uploadDir = new File(UPLOAD_DIR);
//        if (!uploadDir.exists()) {
//            uploadDir.mkdirs();
//        }
//
//        // Generate unique file name
//        String fileName = UUID.randomUUID().toString() + getExtension(multipartFile.getOriginalFilename());
//        File file = new File(uploadDir, fileName);
//
//        // Save the file to local storage
//        multipartFile.transferTo(file);
//
//        // Return the public URL (assuming you serve /uploads/**)
//        return "/uploads/" + fileName;
//    }
    
    private String saveFileLocally(MultipartFile multipartFile) throws IOException {
        // Absolute path (better than Tomcat temp)
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

        // Make sure folder exists
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Unique file name
        String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
        File destination = new File(dir, fileName);

        // Save the file
        multipartFile.transferTo(destination);

        // Return URL for access
        return "/uploads/" + fileName;
    }



    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public String upload(MultipartFile multipartFile) {
        try {
            return saveFileLocally(multipartFile);
        } catch (Exception e) {
            e.printStackTrace();
            return "Image couldn't upload, Something went wrong";
        }
    }

    public String uploadStudentData(MultipartFile multipartFile, Student student) {
        String imageUrl = this.upload(multipartFile);
        student.setImageUrl(imageUrl);
        return imageUrl;
    }

    public String uploadProfData(MultipartFile multipartFile, Professor professor) {
        String imageUrl = this.upload(multipartFile);
        professor.setImageUrl(imageUrl);
        return imageUrl;
    }

    public String uploadHodData(MultipartFile multipartFile, HOD hod) {
        String imageUrl = this.upload(multipartFile);
        hod.setImageUrl(imageUrl);
        return imageUrl;
    }
}
