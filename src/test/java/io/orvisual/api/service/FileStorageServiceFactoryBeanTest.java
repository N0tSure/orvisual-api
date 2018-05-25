package io.orvisual.api.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.env.Environment;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * <p>
 * Created on 25.05.2018.
 * </p>
 *
 * This test of {@link FileStorageServiceFactoryBean}
 *
 * @author Artemis A. Sirosh
 */
@RunWith(MockitoJUnitRunner.class)
public class FileStorageServiceFactoryBeanTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Path rootPath;
    private FactoryBean<FileStorageService> factoryBean;

    @Mock
    private Environment environment;

    @Before
    public void setUp() throws Exception {
        this.rootPath = Paths.get(temporaryFolder.newFolder().toURI());
        this.factoryBean = new FileStorageServiceFactoryBean(environment);
    }

    @Test
    public void shouldReturnCorrectType() {
        assertEquals(FileStorageService.class, factoryBean.getObjectType());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldAssertNotExistedDirectory() throws Exception {

        when(environment.getRequiredProperty("GALLERY_DIR"))
                .thenReturn(this.rootPath.resolve("gallery").toUri().toString());

        this.factoryBean.getObject();
    }


}
