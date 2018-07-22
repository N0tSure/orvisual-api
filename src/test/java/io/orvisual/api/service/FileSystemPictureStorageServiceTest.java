package io.orvisual.api.service;

import com.google.common.hash.Hashing;
import io.orvisual.api.TestHelper;
import io.orvisual.api.model.Picture;
import io.orvisual.api.model.PictureFileItem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
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
public class FileSystemPictureStorageServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemPictureStorageServiceTest.class);

    private static final byte[] OKLAHOMA_BYTES = new byte[]{'O', 'K', 'L', 'A', 'H', 'O', 'M', 'A'};
    
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Path rootPath;

    private PictureStorageService storageService;
    private final Supplier<PictureFileItem> fileItemSupplier = TestHelper.uniformPictureItemSupplier();

    @Before
    public void setUp() throws Exception {
        this.rootPath = Paths.get(temporaryFolder.newFolder().toURI());
        this.storageService = new FileSystemPictureStorageService(this.rootPath);
    }

    @Test
    public void shouldCreateDirectoryForPicture() {
        PictureFileItem item = fileItemSupplier.get();

        Path expectedDirectoryPath = this.rootPath.resolve(item.getPictureItem().getChecksum().substring(0, 4));
        LOGGER.info("Expected directory: {}", expectedDirectoryPath);

        storageService.savePictureFileItem(item);

        assertTrue("Picture directory not exists", Files.exists(expectedDirectoryPath));
        assertTrue("Picture directory file item not a directory", Files.isDirectory(expectedDirectoryPath));

    }

    @Test
    public void shouldSaveFileInExistedDir() {
        final String checksumExpected = Hashing.sha256().hashBytes(OKLAHOMA_BYTES).toString();
        final String nameOfFileExpected = checksumExpected + ".jpg";
        final String directoryNameExpected = checksumExpected.substring(0, 4);

        final PictureFileItem fileItem = new PictureFileItem(
                new Picture(
                        checksumExpected,
                        null,
                        MediaType.IMAGE_JPEG_VALUE,
                        null,
                        Instant.EPOCH
                ), OKLAHOMA_BYTES
        );
        LOGGER.info("Picture file item: {}", fileItem);

        final Path directoryPathExpected = this.rootPath.resolve(directoryNameExpected);
        LOGGER.info("Directory expected: {}", directoryPathExpected);

        final Path pathOfFileExpected = directoryPathExpected.resolve(nameOfFileExpected);
        LOGGER.info("File expected: {}", pathOfFileExpected);

        storageService.savePictureFileItem(fileItem);

        assertTrue("Directory not exists", Files.exists(directoryPathExpected));
        assertTrue("Directory file item not a directory", Files.isDirectory(directoryPathExpected));
        assertTrue("Picture file not exists", Files.exists(pathOfFileExpected));

    }

    @Test(expected = PictureFileProcessingException.class)
    public void shouldProcessExceptionWhileFileDirectoryCreated() throws IOException {
        PictureFileItem item = fileItemSupplier.get();
        LOGGER.info("Picture file item: {}", item);

        Path expectedDirectory = this.rootPath.resolve(item.getPictureItem().getChecksum().substring(0, 4));
        LOGGER.info("Corrupted directory: {}", expectedDirectory);

        // we create file, rather than directory, this should confuse FileSystemPictureStorageService
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
                        null,
                        MediaType.IMAGE_JPEG_VALUE,
                        null,
                        Instant.EPOCH
                ), OKLAHOMA_BYTES
        );
        LOGGER.info("Picture file item: {}", fileItem);

        final Path directoryPathExpected = this.rootPath.resolve(checksumExpected.substring(0, 4));
        LOGGER.info("Directory expected: {}", directoryPathExpected);

        final Path pathOfFileExpected = directoryPathExpected.resolve(checksumExpected + ".jpg");
        LOGGER.info("File expected: {}", pathOfFileExpected);

        storageService.savePictureFileItem(fileItem);

        assertTrue(Files.exists(directoryPathExpected));
        assertTrue(Files.exists(pathOfFileExpected));
        assertArrayEquals("File content not match", OKLAHOMA_BYTES, Files.readAllBytes(pathOfFileExpected));
    }

    @Test(expected = PictureFileProcessingException.class)
    public void shouldProcessExceptionWhileWritingFile() throws IOException {
        PictureFileItem item = fileItemSupplier.get();
        LOGGER.info("Picture file item: {}", item);

        Path directory = this.rootPath.resolve(item.getPictureItem().getChecksum().substring(0, 4));
        LOGGER.info("Directory: {}", directory);

        Path file = directory.resolve(item.getPictureItem().getChecksum() + ".jpg");
        LOGGER.info("Corrupted file: {}", file);

        Files.createDirectories(directory);
        Files.createDirectories(file);

        assertTrue("sanity check", Files.exists(file) && Files.isDirectory(file));

        storageService.savePictureFileItem(item);
    }

    @Test
    public void shouldReturnResourceToPictureFile() throws IOException {
        Picture picture = fileItemSupplier.get().getPictureItem();
        LOGGER.info("Picture: {}", picture);

        Path parentDir = this.rootPath.resolve(picture.getChecksum().substring(0, 4));
        LOGGER.info("Picture directory: {}", parentDir);

        Path pictureFile = parentDir.resolve(picture.getChecksum() + ".jpg");
        LOGGER.info("Picture file directory: {}", pictureFile);

        Files.createDirectories(parentDir);
        Files.createFile(pictureFile);

        Resource resource = storageService.resolvePictureResource(picture);

        assertTrue(resource.exists());
        assertTrue(resource.isFile());
    }

    @Test
    public void shouldDeletePictureFile() throws IOException {
        PictureFileItem item = fileItemSupplier.get();
        LOGGER.info("Picture file item: {}", item);

        Path directory = this.rootPath.resolve(item.getPictureItem().getChecksum().substring(0, 4));
        LOGGER.info("Directory: {}", directory);

        Path file = directory.resolve(item.getPictureItem().getChecksum() + ".jpg");
        LOGGER.info("Picture file: {}", file);

        Files.createDirectories(directory);
        Files.write(file, item.getFileContent());

        storageService.deletePictureFile(item.getPictureItem());

        assertTrue("Directory not exists", Files.exists(directory));
        assertFalse("File yet exists", Files.exists(file));
    }

    @Test(expected = PictureFileProcessingException.class)
    public void shouldProcessExceptionWhileFileDeleting() throws IOException {
        PictureFileItem item = fileItemSupplier.get();
        LOGGER.info("Picture file item: {}", item);

        Path directory = this.rootPath.resolve(item.getPictureItem().getChecksum().substring(0, 4));
        LOGGER.info("Directory: {}", directory);

        Files.createDirectories(directory);

        storageService.deletePictureFile(item.getPictureItem());
    }

}
