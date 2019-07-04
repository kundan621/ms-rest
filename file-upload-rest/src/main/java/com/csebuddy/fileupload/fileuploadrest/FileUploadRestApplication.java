package com.csebuddy.fileupload.fileuploadrest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@SpringBootApplication
@EnableConfigurationProperties({
    FileStorageProperties.class
})
public class FileUploadRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileUploadRestApplication.class, args);
	}

}

/**
 * bean with auto configuration properties
 * @author kkumar42
 *
 */
@ConfigurationProperties(prefix = "file")
 class FileStorageProperties {
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}


/**
 * controller class
 * @author kkumar42
 *
 */
@RestController
 class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

/**
 * file response bean
 * @author kkumar42
 *
 */
 class UploadFileResponse {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

    public UploadFileResponse(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDownloadUri() {
		return fileDownloadUri;
	}

	public void setFileDownloadUri(String fileDownloadUri) {
		this.fileDownloadUri = fileDownloadUri;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
 
 
 /**
  * file storage service
  * @author kkumar42
  *
  */
 @Service
 class FileStorageService {

     private final Path fileStorageLocation;

     @Autowired
     public FileStorageService(FileStorageProperties fileStorageProperties) {
         this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                 .toAbsolutePath().normalize();

         try {
             Files.createDirectories(this.fileStorageLocation);
         } catch (Exception ex) {
             throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
         }
     }

     public String storeFile(MultipartFile file) {
         // Normalize file name
         String fileName = StringUtils.cleanPath(file.getOriginalFilename());

         try {
             // Check if the file's name contains invalid characters
             if(fileName.contains("..")) {
                 throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
             }

             // Copy file to the target location (Replacing existing file with the same name)
             Path targetLocation = this.fileStorageLocation.resolve(fileName);
             Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

             return fileName;
         } catch (IOException ex) {
             throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
         }
     }

     public Resource loadFileAsResource(String fileName) {
         try {
             Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
             Resource resource = new UrlResource(filePath.toUri());
             if(resource.exists()) {
                 return resource;
             } else {
                 throw new MyFileNotFoundException("File not found " + fileName);
             }
         } catch (MalformedURLException ex) {
             throw new MyFileNotFoundException("File not found " + fileName, ex);
         }
     }
 }
 
 
 
/*************************************************************************************************/
 /**
  * Exception class
  * @author kkumar42
  *
  */
  class FileStorageException extends RuntimeException {
	    public FileStorageException(String message) {
	        super(message);
	    }

	    public FileStorageException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}
  
  
  
/******************************************************************************************************/
  /**
   * Exception class
   * @author kkumar42
   *
   */
  @ResponseStatus(HttpStatus.NOT_FOUND)
   class MyFileNotFoundException extends RuntimeException {
      public MyFileNotFoundException(String message) {
          super(message);
      }

      public MyFileNotFoundException(String message, Throwable cause) {
          super(message, cause);
      }
  }
  
  