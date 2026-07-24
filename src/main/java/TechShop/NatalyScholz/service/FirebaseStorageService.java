/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseStorageService {

    @Value("${firebase.bucket.name}")
    private String bucketName;

    @Value("${firebase.storage.path}")
    private String storagePath;

    private final Storage storage;

    public FirebaseStorageService(Storage storage) {
        this.storage = storage;
    }

    /**
     * Sube una imagen a Firebase Storage.
     *
     * @param localFile archivo recibido desde el formulario
     * @param folder carpeta lógica, por ejemplo: categoria, producto o usuario
     * @param id identificador del registro
     * @return URL firmada de la imagen
     * @throws IOException si ocurre un problema al procesar o subir el archivo
     */
    public String uploadImage(
            MultipartFile localFile,
            String folder,
            Long id
    ) throws IOException {

        if (localFile == null || localFile.isEmpty()) {
            throw new IllegalArgumentException(
                    "No se recibió ningún archivo para subir."
            );
        }

        if (id == null) {
            throw new IllegalArgumentException(
                    "El ID del registro no puede ser nulo."
            );
        }

        String originalName = localFile.getOriginalFilename();
        String fileExtension = obtenerExtension(originalName);

        String fileName = "img"
                + getFormattedNumber(id)
                + fileExtension;

        File tempFile = convertToFile(localFile);

        try {
            return uploadToFirebase(tempFile, folder, fileName);
        } finally {
            if (tempFile.exists() && !tempFile.delete()) {
                tempFile.deleteOnExit();
            }
        }
    }

    /**
     * Obtiene la extensión del archivo original.
     */
    private String obtenerExtension(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }

        return originalName.substring(
                originalName.lastIndexOf(".")
        );
    }

    /**
     * Convierte MultipartFile en un archivo temporal.
     */
    private File convertToFile(
            MultipartFile multipartFile
    ) throws IOException {

        String originalName = multipartFile.getOriginalFilename();
        String extension = obtenerExtension(originalName);

        if (extension.isBlank()) {
            extension = ".tmp";
        }

        File tempFile = File.createTempFile(
                "upload-",
                extension
        );

        try (FileOutputStream fos =
                     new FileOutputStream(tempFile)) {

            fos.write(multipartFile.getBytes());
        }

        return tempFile;
    }

    /**
     * Sube el archivo a Firebase Storage y genera una URL firmada.
     */
    private String uploadToFirebase(
            File file,
            String folder,
            String fileName
    ) throws IOException {

        String rutaCompleta = construirRuta(
                storagePath,
                folder,
                fileName
        );

        BlobId blobId = BlobId.of(
                bucketName,
                rutaCompleta
        );

        String mimeType = Files.probeContentType(
                file.toPath()
        );

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(
                        mimeType != null
                                ? mimeType
                                : "application/octet-stream"
                )
                .build();

        storage.create(
                blobInfo,
                Files.readAllBytes(file.toPath())
        );

        return storage.signUrl(
                blobInfo,
                1825,
                TimeUnit.DAYS
        ).toString();
    }

    /**
     * Construye una ruta limpia dentro del bucket.
     */
    private String construirRuta(
            String basePath,
            String folder,
            String fileName
    ) {

        String base = limpiarSegmento(basePath);
        String carpeta = limpiarSegmento(folder);

        if (base.isBlank()) {
            return carpeta + "/" + fileName;
        }

        if (carpeta.isBlank()) {
            return base + "/" + fileName;
        }

        return base
                + "/"
                + carpeta
                + "/"
                + fileName;
    }

    /**
     * Limpia barras al inicio y al final.
     */
    private String limpiarSegmento(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace("\\", "/")
                .replaceAll("^/+", "")
                .replaceAll("/+$", "");
    }

    /**
     * Genera un número de 14 dígitos con ceros a la izquierda.
     */
    private String getFormattedNumber(long id) {
        return String.format("%014d", id);
    }
}