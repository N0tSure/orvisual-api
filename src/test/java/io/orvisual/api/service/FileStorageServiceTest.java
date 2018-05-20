package io.orvisual.api.service;

import io.orvisual.api.repository.PictureRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>
 * Created on 20.05.2018.
 * </p>
 *
 * @author Artemis A. Sirosh
 */
@RunWith(MockitoJUnitRunner.class)
public class FileStorageServiceTest {
    
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final Path rootPath = Paths.get(temporaryFolder.getRoot().toURI());

    @Mock
    private PictureRepository repository;

    private FileStorageService storageService;

    @Before
    public void setUp() {
        this.storageService = new FileStorageService(this.rootPath, this.repository);
    }

    @Test
    public void shouldSavePictureFile() {
        final MultipartFile multipartFile = new MockMultipartFile(
                "image",
                "foo.jpg",
                "image/jpeg",
                new byte[]{'O', 'K', 'L', 'A', 'H', 'O', 'M', 'A'}
                );
//        storageService.saveMultiPartFile(multipartFile);
    }
}
