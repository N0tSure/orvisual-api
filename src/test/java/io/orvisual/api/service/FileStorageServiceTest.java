package io.orvisual.api.service;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import io.orvisual.api.repository.PictureRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * <p>
 * Created on 20.05.2018.
 * </p>
 *
 * @author Artemis A. Sirosh
 */
@RunWith(MockitoJUnitRunner.class)
public class FileStorageServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageServiceTest.class);

    private static final byte[] OKLAHOMA_BYTES = new byte[]{'O', 'K', 'L', 'A', 'H', 'O', 'M', 'A'};
    
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Path rootPath;

    private FileStorageService storageService;

    @Before
    public void setUp() throws Exception {
        this.rootPath = Paths.get(temporaryFolder.newFolder().toURI());
        this.storageService = new FileStorageService(this.rootPath);
    }

    @Test
    public void shouldCreateDirectoryForPicture() {
        PictureFileItem item = pictureSupplier().get();

        Path expectedDirectoryPath = this.rootPath.resolve(item.getPictureItem().getDirectory());
        LOGGER.info("Expected directory: {}", expectedDirectoryPath);

        storageService.savePictureFileItem(item);

        assertTrue(Files.exists(expectedDirectoryPath));
        assertTrue(Files.isDirectory(expectedDirectoryPath));

    }

    @Test
    public void shouldSaveFileInExistedDir() {
        final String checksumExpected = Hashing.sha256().hashBytes(OKLAHOMA_BYTES).toString();
        final String nameOfFileExpected = checksumExpected + ".jpg";
        final String directoryNameExpected = checksumExpected.substring(0, 4);

        final PictureFileItem fileItem = new PictureFileItem(
                new Picture(
                        checksumExpected,
                        nameOfFileExpected,
                        MediaType.IMAGE_JPEG_VALUE,
                        directoryNameExpected,
                        Instant.EPOCH
                ), OKLAHOMA_BYTES
        );
        LOGGER.info("Picture file item: {}", fileItem);

        final Path directoryPathExpected = this.rootPath.resolve(directoryNameExpected);
        LOGGER.info("Directory expected: {}", directoryPathExpected);

        final Path pathOfFileExpected = directoryPathExpected.resolve(nameOfFileExpected);
        LOGGER.info("File expected: {}", pathOfFileExpected);

        storageService.savePictureFileItem(fileItem);

        assertTrue(Files.exists(directoryPathExpected));
        assertTrue(Files.isDirectory(directoryPathExpected));
        assertTrue(Files.exists(pathOfFileExpected));

    }

    @Test(expected = PictureFileProcessingException.class)
    public void shouldProcessExceptionWhileFileDirectoryCreated() throws IOException {
        PictureFileItem item = pictureSupplier().get();
        LOGGER.info("Picture file item: {}", item);

        Path expectedDirectory = this.rootPath.resolve(item.getPictureItem().getDirectory());
        LOGGER.info("Corrupted directory: {}", expectedDirectory);

        // we create file, rather than directory, this should confuse FileStorageService
        Files.createFile(expectedDirectory);

        assertTrue("Sanity check for existence", Files.exists(expectedDirectory));
        assertTrue("Sanity check for wrong file item type",Files.isRegularFile(expectedDirectory));

        storageService.savePictureFileItem(item);

    }

    @Test
    public void shouldWritePictureFileProperly() throws IOException {
        final String checksumExpected = Hashing.sha256().hashBytes(OKLAHOMA_BYTES).toString();
        final PictureFileItem fileItem = new PictureFileItem(
                new Picture(
                        checksumExpected,
                        checksumExpected + ".jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        checksumExpected.substring(0, 4),
                        Instant.EPOCH
                ), OKLAHOMA_BYTES
        );
        LOGGER.info("Picture file item: {}", fileItem);

        final Path directoryPathExpected = this.rootPath.resolve(fileItem.getPictureItem().getDirectory());
        LOGGER.info("Directory expected: {}", directoryPathExpected);

        final Path pathOfFileExpected = directoryPathExpected.resolve(fileItem.getPictureItem().getFileName());
        LOGGER.info("File expected: {}", pathOfFileExpected);

        storageService.savePictureFileItem(fileItem);

        assertTrue(Files.exists(directoryPathExpected));
        assertTrue(Files.exists(pathOfFileExpected));
        assertArrayEquals("File content not match", OKLAHOMA_BYTES, Files.readAllBytes(pathOfFileExpected));
    }

    @Test(expected = PictureFileProcessingException.class)
    public void shouldProcessExceptionWhileWritingFile() throws IOException {
        PictureFileItem item = pictureSupplier().get();
        LOGGER.info("Picture file item: {}", item);

        Path directory = this.rootPath.resolve(item.getPictureItem().getDirectory());
        LOGGER.info("Directory: {}", directory);

        Path file = directory.resolve(item.getPictureItem().getFileName());
        LOGGER.info("Corrupted file: {}", file);

        Files.createDirectories(directory);
        Files.createDirectories(file);

        assertTrue("sanity check", Files.exists(file) && Files.isDirectory(file));

        storageService.savePictureFileItem(item);
    }

    @Test
    public void shouldReturnResourceToPictureFile() throws IOException {
        Picture picture = pictureSupplier().get().getPictureItem();
        LOGGER.info("Picture: {}", picture);

        Path parentDir = this.rootPath.resolve(picture.getDirectory());
        LOGGER.info("Picture directory: {}", parentDir);

        Path pictureFile = parentDir.resolve(picture.getFileName());
        LOGGER.info("Picture file directory: {}", pictureFile);

        Files.createDirectories(parentDir);
        Files.createFile(pictureFile);

        Resource resource = storageService.resolvePictureResource(picture);

        assertTrue(resource.exists());
        assertTrue(resource.isFile());
    }

    @Test
    public void shouldDeletePictureFile() throws IOException {
        PictureFileItem item = pictureSupplier().get();
        LOGGER.info("Picture file item: {}", item);

        Path directory = this.rootPath.resolve(item.getPictureItem().getDirectory());
        LOGGER.info("Directory: {}", directory);

        Path file = directory.resolve(item.getPictureItem().getFileName());
        LOGGER.info("Picture file: {}", file);

        Files.createDirectories(directory);
        Files.write(file, item.getFileContent());

        storageService.deleteFile(item.getPictureItem());

        assertTrue("Directory not exists", Files.exists(directory));
        assertFalse("File yet exists", Files.exists(file));
    }

    @Test(expected = PictureFileProcessingException.class)
    public void shouldProcessExceptionWhileFileDeleting() throws IOException {
        PictureFileItem item = pictureSupplier().get();
        LOGGER.info("Picture file item: {}", item);

        Path directory = this.rootPath.resolve(item.getPictureItem().getDirectory());
        LOGGER.info("Directory: {}", directory);

        Files.createDirectories(directory);

        storageService.deleteFile(item.getPictureItem());
    }

    private static Supplier<PictureFileItem> pictureSupplier() {
        final HashFunction sha256Function = Hashing.sha256();
        return () -> new PictureFileItem(
                new Picture(
                        sha256Function.hashBytes(OKLAHOMA_BYTES).toString(),
                        "foo.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "bar",
                        Instant.EPOCH
                ), OKLAHOMA_BYTES
        );
    }
}
