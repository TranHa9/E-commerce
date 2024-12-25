package vn.techmaster.tranha.ecommerce.resource;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
public class FileResource {

    @PostMapping()
    public ResponseEntity<?> uploadImages(@RequestPart("files") MultipartFile[] files) {
        String uploadDir = "images/product" + File.separator;
        File dir = new File(uploadDir);
        // Kiểm tra nếu thư mục không tồn tại thì tạo mới
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Danh sách để lưu các đường dẫn của ảnh
        List<String> uploadedFileNames = new ArrayList<>();
        try {
            // Lặp qua tất cả các tệp và lưu từng tệp
            for (MultipartFile file : files) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + fileName);

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                uploadedFileNames.add("/product/" + fileName);
            }

            return ResponseEntity.ok(uploadedFileNames);

        } catch (IOException e) {
            // Trả về lỗi nếu không thể lưu ảnh
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not save images: " + e.getMessage());
        }
    }

    @GetMapping("/{type}/{fileName}")
    public ResponseEntity<?> download(@PathVariable String type,
                                      @PathVariable String fileName) throws IOException {
        if (!StringUtils.hasText(fileName)) {
            return ResponseEntity.badRequest().body("File name is empty");
        }

        String basePath = switch (type) {
            case "user" -> "images/user/";
            case "logo" -> "images/logo/";
            case "product" -> "images/product/";
            default -> null;
        };

        File file = new File(basePath + fileName);

        HttpHeaders headers = new HttpHeaders();
        List<String> customHeaders = new ArrayList<>();
        customHeaders.add(HttpHeaders.CONTENT_DISPOSITION);
        customHeaders.add("Content-Response");
        headers.setAccessControlExposeHeaders(customHeaders);
        headers.set("Content-Disposition", "attachment;filename=" + file.getName());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        byte[] imageData = Files.readAllBytes(file.toPath());
        if (ObjectUtils.isEmpty(imageData)) {
            return ResponseEntity.noContent().build();
        }
        ByteArrayResource resource = new ByteArrayResource(imageData);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
